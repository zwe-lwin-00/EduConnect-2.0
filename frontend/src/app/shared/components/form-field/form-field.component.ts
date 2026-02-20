import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

/**
 * Reusable form field: label + input + error message.
 */
@Component({
  selector: 'app-form-field',
  templateUrl: './form-field.component.html',
  styleUrls: ['./form-field.component.css']
})
export class FormFieldComponent {
  @Input() label = '';
  @Input() control: AbstractControl | null = null;
  @Input() type: string = 'text';
  @Input() placeholder = '';
  @Input() id = '';
  @Input() errorMessage = '';

  get showError(): boolean {
    return !!this.control && this.control.invalid && this.control.touched;
  }
}
