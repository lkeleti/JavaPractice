import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnInit,
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
import { ZipCodeService } from '../../services/zip-code.service';
import { EntityLookupModalComponent } from '../../shared/entity-lookup-modal/entity-lookup-modal.component';
import { map } from 'rxjs/operators';


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

  constructor(
    private fb: FormBuilder,
    private modalService: NgbModal,
    private zipCodeService: ZipCodeService
  ) { }

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

  openEntityLookupModal(): void {
    const modalRef = this.modalService.open(EntityLookupModalComponent<ZipCodeDto>, {
      size: 'lg',
      backdrop: 'static',
      keyboard: true
    });

    modalRef.componentInstance.title = 'Irányítószám keresés';
    modalRef.componentInstance.fetchFunction = (page: number, size: number, search: string | undefined) =>
      this.zipCodeService.getZipCodes(page, size, search).pipe(map(res => res.content));

    modalRef.componentInstance.displayFn = (zip: ZipCodeDto) => ({
      label: zip.zip,
      subLabel: zip.city
    });

    modalRef.result
      .then((selectedId: number) => {
        if (selectedId) {
          this.zipCodeService.findZipCodeById(selectedId).subscribe({
            next: zip => this.form.get('zipCode')?.setValue(zip)
          });
        }
      })
      .catch(() => { });
  }
  ngOnDestroy(): void { }
}
