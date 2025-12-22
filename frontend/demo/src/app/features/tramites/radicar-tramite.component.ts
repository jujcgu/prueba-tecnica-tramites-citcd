import {ChangeDetectionStrategy, Component, computed, effect, inject, signal,} from '@angular/core';
import {FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators,} from '@angular/forms';
import {Router} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {catchError, EMPTY, firstValueFrom, of, startWith,} from 'rxjs';
import {TramiteService} from './data-access/tramite.service';
import {RequisitoService} from './data-access/requisito.service';
import {AdjuntoService} from './data-access/adjunto.service';
import {TipoTramiteService} from './data-access/tipo-tramite.service';
import {AdjuntoUploadResponse} from './models/adjunto-upload-response.model';
import {DocumentoRequerido} from './models/documento-requerido.model';
import {TipoTramiteDto} from './models/tipo-tramite.dto';
import {filesCountValidator} from '../../shared/validators/file.validators';

type DocVm = DocumentoRequerido & {
  accept: string; minEfectivo: number;
};

type DocGroup = FormGroup<{
  tipoDocumentoId: FormControl<number>; archivos: FormControl<AdjuntoUploadResponse[]>;
}>;

@Component({
  selector: 'app-radicar-tramite-page',
  imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatSelectModule, MatInputModule, MatButtonModule, MatIconModule, MatDividerModule, MatProgressSpinnerModule, MatSnackBarModule,],
  templateUrl: './radicar-tramite.component.html',
  styleUrl: './radicar-tramite.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {class: 'radicar-page'},
})
export class RadicarTramiteComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  readonly form = this.fb.group({
    tipoTramiteId: this.fb.control<number | null>(null, {
      validators: [Validators.required],
    }), comentario: this.fb.control('', {
      validators: [Validators.required, Validators.minLength(10)],
    }), documentos: this.fb.array<DocGroup>([]),
  });
  readonly tipoTramiteId = this.form.controls.tipoTramiteId;
  readonly comentario = this.form.controls.comentario;
  readonly documentos = this.form.controls.documentos;
  readonly submitting = signal(false);
  readonly uploading = signal<Record<number, number>>({});
  readonly uploadError = signal<Record<number, string | null>>({});
  readonly docsVm = signal<DocVm[]>([]);
  private readonly tiposApi = inject(TipoTramiteService);
  private readonly reqApi = inject(RequisitoService);
  private readonly adjuntosApi = inject(AdjuntoService);
  private readonly tramitesApi = inject(TramiteService);
  private readonly snack = inject(MatSnackBar);
  private readonly router = inject(Router);
  private readonly tipos$ = this.tiposApi
    .getActivos()
    .pipe(catchError(() => of([] as TipoTramiteDto[])));
  readonly tipos = toSignal(this.tipos$, {initialValue: []});

  private readonly selectedTipoId = toSignal(this.tipoTramiteId.valueChanges);
  private readonly formStatus = toSignal(this.form.statusChanges.pipe(startWith(this.form.status)), {initialValue: this.form.status});

  readonly canSubmit = computed(() => {
    if (this.submitting() || this.formStatus() !== 'VALID') {
      return false;
    }
    const hasUploading = Object.values(this.uploading()).some((n) => n > 0);
    return !hasUploading;
  });

  constructor() {
    effect(async () => {
      const tipoId = this.selectedTipoId();
      this.resetDocs();

      if (typeof tipoId !== 'number') return;

      try {
        const docs = await firstValueFrom(this.reqApi
          .getDocumentosRequeridos(tipoId)
          .pipe(catchError(() => EMPTY)),);
        const sortedDocs = docs.slice().sort((a, b) => a.orden - b.orden);
        this.buildDocs(sortedDocs);
      } catch (e) {
        console.error('Failed to fetch document requirements:', e);
      }
    },);
  }

  isUploading(tipoDocumentoId: number): boolean {
    return (this.uploading()[tipoDocumentoId] ?? 0) > 0;
  }

  async onFilesSelected(index: number, input: HTMLInputElement): Promise<void> {
    const files = input.files ? Array.from(input.files) : [];
    input.value = '';

    const doc = this.docsVm()[index];
    const group = this.documentos.at(index);
    group.markAsTouched();

    if (!doc || !files.length) return;

    this.setUploadError(doc.tipoDocumentoId, null);

    const currentFiles = group.controls.archivos.value;
    const availableSlots = doc.cantidadMaxima - currentFiles.length;
    if (availableSlots <= 0) {
      this.setUploadError(doc.tipoDocumentoId, `Máximo ${doc.cantidadMaxima} archivo(s).`,);
      return;
    }

    const filesToUpload = files.slice(0, availableSlots);
    const successfullyUploaded: AdjuntoUploadResponse[] = [];

    for (const file of filesToUpload) {
      if (!doc.mimePermitidos.includes(file.type)) {
        this.setUploadError(doc.tipoDocumentoId, `Tipo no permitido: ${file.type || 'desconocido'}`,);
        continue;
      }

      if (doc.tamanoMaxMb != null) {
        const maxBytes = doc.tamanoMaxMb * 1024 * 1024;
        if (file.size > maxBytes) {
          this.setUploadError(doc.tipoDocumentoId, `El archivo supera ${doc.tamanoMaxMb} MB.`,);
          continue;
        }
      }

      this.incUploading(doc.tipoDocumentoId);
      try {
        const uploaded = await firstValueFrom(this.adjuntosApi.upload(file));
        successfullyUploaded.push(uploaded);
      } catch {
        this.setUploadError(doc.tipoDocumentoId, 'Error cargando el archivo. Intenta de nuevo.',);
      } finally {
        this.decUploading(doc.tipoDocumentoId);
      }
    }

    if (successfullyUploaded.length > 0) {
      group.controls.archivos.setValue([...group.controls.archivos.value, ...successfullyUploaded,]);
    }

    group.controls.archivos.updateValueAndValidity();
  }

  removeArchivo(index: number, id: string): void {
    const group = this.documentos.at(index);
    const currentFiles = group.controls.archivos.value;
    const nextFiles = currentFiles.filter((a) => a.identificadorAlmacenamiento !== id,);
    group.controls.archivos.setValue(nextFiles);
    group.controls.archivos.updateValueAndValidity();
  }

  async submit(): Promise<void> {
    this.form.markAllAsTouched();
    if (!this.canSubmit()) {
      return;
    }

    this.submitting.set(true);
    const raw = this.form.getRawValue();
    const adjuntos = raw.documentos.flatMap((g) => (g.archivos ?? []).map((a) => ({
      tipoDocumentoId: g.tipoDocumentoId,
      nombreArchivo: a.nombreArchivoOriginal,
      identificadorAlmacenamiento: a.identificadorAlmacenamiento,
    })),);

    try {
      await firstValueFrom(this.tramitesApi.create({
        tipoTramiteId: raw.tipoTramiteId!, comentario: raw.comentario, adjuntos,
      }),);

      this.snack.open('Trámite radicado correctamente.', 'Cerrar', {
        duration: 4000,
      });
      await this.router.navigateByUrl('/home');
    } catch (e) {
      console.error('Failed to submit form:', e);
      this.snack.open('Error al radicar el trámite.', 'Cerrar', {
        duration: 4000, panelClass: ['error-snackbar']
      });
    } finally {
      this.submitting.set(false);
    }
  }

  private resetDocs(): void {
    this.docsVm.set([]);
    this.uploading.set({});
    this.uploadError.set({});
    this.documentos.clear();
  }

  private buildDocs(docs: DocumentoRequerido[]): void {
    const vm: DocVm[] = docs.map((d) => ({
      ...d,
      accept: (d.mimePermitidos ?? []).join(','),
      minEfectivo: d.esObligatorio ? Math.max(1, d.cantidadMinima ?? 1) : 0,
    }));
    this.docsVm.set(vm);
    vm.forEach((d) => this.documentos.push(this.createDocGroup(d)));
  }

  private createDocGroup(doc: DocVm): DocGroup {
    // **THE FIX**: Pass the validator when the control is created.
    // This ensures the initial empty array is correctly marked as invalid if files are required.
    return this.fb.group({
      tipoDocumentoId: this.fb.control(doc.tipoDocumentoId), archivos: this.fb.control<AdjuntoUploadResponse[]>([], // Initial value
        [filesCountValidator(doc.minEfectivo, doc.cantidadMaxima)] // Validator array
      ),
    });
  }

  private incUploading(id: number): void {
    this.uploading.update((m) => ({...m, [id]: (m[id] ?? 0) + 1}));
  }

  private decUploading(id: number): void {
    this.uploading.update((m) => ({...m, [id]: Math.max(0, (m[id] ?? 1) - 1)}));
  }

  private setUploadError(id: number, msg: string | null): void {
    this.uploadError.update((m) => ({...m, [id]: msg}));
  }
}
