import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function filesCountValidator(min: number, max: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const files = control.value;

    if (!files || !Array.isArray(files)) {
      return null;
    }

    const length = files.length;

    if (length < min) {
      return { filesMin: { required: min, actual: length } };
    }

    if (length > max) {
      return { filesMax: { required: max, actual: length } };
    }

    return null;
  };
}
