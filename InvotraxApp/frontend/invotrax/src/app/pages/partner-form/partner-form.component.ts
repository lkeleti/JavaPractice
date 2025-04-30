import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  OnDestroy,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { PartnerDto } from '../../models/partner.dto';
import { ZipCodeDto } from '../../models/zip-code.dto';
import { CommonModule } from '@angular/common';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {
  EntityLookupModalComponent,
  LookupItem,
} from '../../shared/entity-lookup-modal/entity-lookup-modal/entity-lookup-modal.component';
import { ZipCodeService } from '../../services/zip-code.service';

@Component({
  selector: 'app-partner-form',
  templateUrl: './partner-form.component.html',
  styleUrls: ['./partner-form.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
})
export class PartnerFormComponent implements OnInit, OnDestroy {
  @Input() initialPartner?: PartnerDto;
  @Input() zipCodes: ZipCodeDto[] = [];
  @Output() formSubmit = new EventEmitter<PartnerDto>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;
  zipCodeLookupItems: any;

  constructor(
    private fb: FormBuilder,
    private modalService: NgbModal,
    private zipCodeService: ZipCodeService
  ) {}

  keyListener = (event: KeyboardEvent): void => {
    if (event.ctrlKey && event.key.toLowerCase() === 'h') {
      event.preventDefault();
      this.openZipCodeLookupModal();
    }
  };

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
      balance: [0],
    });

    if (this.initialPartner) {
      this.form.patchValue(this.initialPartner);
    }

    this.form.get('isPrivate')?.valueChanges.subscribe((value) => {
      const tax = this.form.get('taxNumber');
      const bankFields = ['bankName', 'bankNumber', 'iban'];
      if (value === true) {
        tax?.setValidators([Validators.required]);
        bankFields.forEach((field) => this.form.get(field)?.clearValidators());
      } else {
        tax?.clearValidators();
        bankFields.forEach((field) =>
          this.form.get(field)?.setValidators([Validators.required])
        );
      }
      tax?.updateValueAndValidity();
      bankFields.forEach((field) =>
        this.form.get(field)?.updateValueAndValidity()
      );
    });

    this.form.get('landRegistryNumber')?.valueChanges.subscribe((value) => {
      const addressFields = ['streetName', 'streetType', 'houseNumber'];
      if (!value) {
        addressFields.forEach((field) =>
          this.form.get(field)?.setValidators([Validators.required])
        );
      } else {
        addressFields.forEach((field) =>
          this.form.get(field)?.clearValidators()
        );
      }
      addressFields.forEach((field) =>
        this.form.get(field)?.updateValueAndValidity()
      );
    });

    window.addEventListener('keydown', this.keyListener);
  }

  submit(): void {
    if (this.form.valid) {
      this.formSubmit.emit(this.form.value);
    } else {
      this.form.markAllAsTouched();
    }
  }

  onCancel(): void {
    this.cancel.emit();
  }

  openZipCodeLookupModal(): void {
    this.zipCodeService.getZipCodes(1, 1000).subscribe((response) => {
      const zipCodeLookupItems: LookupItem[] = response.content.map((zip) => ({
        id: zip.id,
        label: zip.zip,
        subLabel: zip.city,
      }));

      const modalRef = this.modalService.open(EntityLookupModalComponent, {
        size: 'lg',
        backdrop: 'static',
        keyboard: true,
      });

      modalRef.componentInstance.items = zipCodeLookupItems;
      modalRef.componentInstance.title = 'Irányítószám keresés';

      modalRef.componentInstance.selected.subscribe((item: LookupItem) => {
        const selectedZip = response.content.find((z) => z.id === item.id);
        if (selectedZip) {
          this.form.get('zipCode')?.setValue(selectedZip);
        }
        modalRef.close();
      });

      modalRef.componentInstance.cancelled.subscribe(() => modalRef.dismiss());
    });
  }

  ngOnDestroy(): void {
    window.removeEventListener('keydown', this.keyListener);
  }
}
