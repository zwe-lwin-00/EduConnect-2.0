import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { PlaceholderComponent } from './components/placeholder/placeholder.component';
import { FormFieldComponent } from './components/form-field/form-field.component';
import { PrimaryButtonComponent } from './components/primary-button/primary-button.component';
import { BrandComponent } from './components/brand/brand.component';
import { LogoutButtonComponent } from './components/logout-button/logout-button.component';
import { CardComponent } from './components/card/card.component';
import { NotificationBellComponent } from './components/notification-bell/notification-bell.component';
import { MyanmarDatePipe } from './pipes/myanmar-date.pipe';

/**
 * Shared module for reusable components used across auth, admin, teacher, and parent.
 */
@NgModule({
  declarations: [
    PlaceholderComponent,
    FormFieldComponent,
    PrimaryButtonComponent,
    BrandComponent,
    LogoutButtonComponent,
    CardComponent,
    NotificationBellComponent,
    MyanmarDatePipe
  ],
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  exports: [
    PlaceholderComponent,
    FormFieldComponent,
    PrimaryButtonComponent,
    BrandComponent,
    LogoutButtonComponent,
    CardComponent,
    NotificationBellComponent,
    MyanmarDatePipe
  ]
})
export class SharedModule { }
