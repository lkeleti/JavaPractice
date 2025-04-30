import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { PartnerDto } from '../../models/partner.dto';
import { ZipCodeDto } from '../../models/zip-code.dto';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-partner-form',
  templateUrl: './partner-form.component.html',
  styleUrls: ['./partner-form.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule]
})
export class PartnerFormComponent implements OnInit {
  @Input() initialPartner?: PartnerDto;
  @Input() zipCodes: ZipCodeDto[] = [];
  @Output() formSubmit = new EventEmitter<PartnerDto>();

  form!: FormGroup;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      zipCode: [null, Validators.required],
      isPrivate: [false],
      taxNumber: [''],
      email: ['', Validators.email],
      phoneNumber: [''],
      preferredPaymentMethod: [''],
      streetName: [''],
      streetType: [''],
      houseNumber: [''],
      building: [''],
      staircase: [''],
      floor: [''],
      door: [''],
      landRegistryNumber: [''],
      bankName: [''],
      bankNumber: [''],
      iban: [''],
      defaultPaymentDeadline: [null],
      balance: [0]
    });

    if (this.initialPartner) {
      this.form.patchValue(this.initialPartner);
    }

    this.form.get('isPrivate')?.valueChanges.subscribe(value => {
      const tax = this.form.get('taxNumber');
      const bankFields = ['bankName', 'bankNumber', 'iban'];
      if (value === true) {
        tax?.setValidators([Validators.required]);
        bankFields.forEach(field => this.form.get(field)?.clearValidators());
      } else {
        tax?.clearValidators();
        bankFields.forEach(field => this.form.get(field)?.setValidators([Validators.required]));
      }
      tax?.updateValueAndValidity();
      bankFields.forEach(field => this.form.get(field)?.updateValueAndValidity());
    });

    this.form.get('landRegistryNumber')?.valueChanges.subscribe(value => {
      const addressFields = ['streetName', 'streetType', 'houseNumber'];
      if (!value) {
        addressFields.forEach(field => this.form.get(field)?.setValidators([Validators.required]));
      } else {
        addressFields.forEach(field => this.form.get(field)?.clearValidators());
      }
      addressFields.forEach(field => this.form.get(field)?.updateValueAndValidity());
    });
  }

  submit(): void {
    if (this.form.valid) {
      this.formSubmit.emit(this.form.value);
    } else {
      this.form.markAllAsTouched();
    }
  }
}
