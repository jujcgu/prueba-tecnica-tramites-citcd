import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdjuntoUploadResponse } from '../models/adjunto-upload-response.model';

@Injectable({
  providedIn: 'root'
})
export class AdjuntoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/adjuntos';

  upload(file: File): Observable<AdjuntoUploadResponse> {
    const formData = new FormData();
    formData.append('file', file, file.name);

    return this.http.post<AdjuntoUploadResponse>(`${this.apiUrl}/cargar`, formData);
  }
}
