import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnInit,
  OnDestroy,
  HostListener,
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
import { ActivatedRoute, Router } from '@angular/router';
import { PartnerService } from '../../services/partner.service';
import { CreatePartnerCommand } from '../../models/create-partner.command';
import { UpdatePartnerCommand } from '../../models/update-partner.command';
import { PaymentMethodDto } from '../../models/paymentMethod.dto';
import { PaymentMethodService } from '../../services/payment-method.service';
import { SellerService } from '../../services/seller.service';
import { CreateSellerCommand } from './../../models/create-seller-command';
import { UpdateSellerCommand } from '../../models/update-seller-commad';
import { getPartnerAddress } from '../../shared/utils/address.util';


@Component({
  selector: 'app-partner-form',
  templateUrl: './partner-form.component.html',
  styleUrls: ['./partner-form.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
})
export class PartnerFormComponent implements OnInit, OnDestroy {
  @Input() zipCodes: ZipCodeDto[] = [];
  @Input() mode: 'partner' | 'seller' = 'partner';
  @Output() formSubmit = new EventEmitter<PartnerDto>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;
  isEditMode = false;
  partnerId?: number;
  paymentMethods: PaymentMethodDto[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
    private partnerService: PartnerService,
    private zipCodeService: ZipCodeService,
    private paymentMethodService: PaymentMethodService,
    private sellerService: SellerService
  ) { }

  ngOnInit(): void {
    const routeMode = this.route.snapshot.data['mode'];
    if (routeMode === 'seller') {
      this.mode = 'seller';

      this.sellerService.getSeller().subscribe({
        next: (seller) => {
          this.isEditMode = true;
          this.partnerId = seller.partner.id;
          this.form.patchValue({
            ...seller.partner,
            headOfficeAddress: seller.headOfficeAddress,
            companyRegistrationNumber: seller.companyRegistrationNumber
          });

          const matched = this.paymentMethods.find(pm => pm.id === seller.partner.preferredPaymentMethod?.id);
          if (matched) {
            this.form.get('preferredPaymentMethod')?.setValue(matched);
          }
        },
        error: (err) => {
          if (err.status === 404 && err.error?.message === 'SELLER_NOT_FOUND') {
            this.isEditMode = false; // új seller létrehozása
          } else {
            alert('Nem sikerült betölteni az eladó adatait.');
          }
        }
      });
    }

    this.createForm();

    if (this.mode === 'seller') {
      this.form.addControl('headOfficeAddress', this.fb.control('', Validators.required));
      this.form.addControl('companyRegistrationNumber', this.fb.control('', Validators.required));
    }

    this.paymentMethodService.getActivePaymentMethods().subscribe({
      next: (methods) => {
        this.paymentMethods = methods;

        // Ha edit módban vagyunk és a partner már be van töltve
        if (this.isEditMode && this.form.value.preferredPaymentMethod) {
          const matched = this.paymentMethods.find(pm => pm.id === this.form.value.preferredPaymentMethod.id);
          if (matched) {
            this.form.get('preferredPaymentMethod')?.setValue(matched);
          }
        }
      },
      error: () => console.error('Nem sikerült betölteni a fizetési módokat.')
    });

    this.route.paramMap.subscribe((params) => {
      const idParam = params.get('id');
      if (idParam) {
        this.isEditMode = true;
        this.partnerId = +idParam;
        this.loadPartner(this.partnerId);
      }
    });
  }

  createForm() {
    this.form = this.fb.group({
      name: ['', Validators.required],
      zipCode: [null, Validators.required],
      isPrivate: [false],
      taxNumber: [''],
      email: ['', Validators.email],
      phoneNumber: [''],
      preferredPaymentMethod: [null, Validators.required],
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
      defaultPaymentDeadline: [1],
      balance: [0],
    });

    this.setupValidation();
  }
  setupValidation() {
    this.form.get('isPrivate')?.valueChanges.subscribe((value) => {
      const tax = this.form.get('taxNumber');
      const bankFields = ['bankName', 'bankNumber', 'iban'];
      if (value) {
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

  loadPartner(id: number): void {
    this.partnerService.findPartnerById(id).subscribe({
      next: (partner) => {
        this.form.patchValue(partner);

        if (partner.preferredPaymentMethod && this.paymentMethods.length) {
          const matched = this.paymentMethods.find(pm => pm.id === partner.preferredPaymentMethod.id);
          if (matched) {
            this.form.get('preferredPaymentMethod')?.setValue(matched);
          }
        }
      },
      error: () => alert('Nem sikerült betölteni a partner adatokat.')
    });
  }



  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.value;

    const createCommand: CreatePartnerCommand = {
      ...formValue,
      zipCodeId: formValue.zipCode?.id,
      preferredPaymentMethodId: formValue.preferredPaymentMethod.id,
      deleted: false, // vagy formValue.deleted ha szükséges
      createdAt: this.isEditMode ? formValue.createdAt : new Date(),
    };

    const updateCommand: UpdatePartnerCommand = {
      ...formValue,
      zipCodeId: formValue.zipCode?.id,
      preferredPaymentMethodId: formValue.preferredPaymentMethod?.id ?? null,
      deleted: false, // vagy formValue.deleted ha szükséges
      createdAt: this.isEditMode ? formValue.createdAt : new Date(),
    };

    if (this.mode === 'seller') {
      if (this.isEditMode && this.partnerId) {
        this.partnerService.updatePartner(this.partnerId, updateCommand).subscribe({
          next: () => {
            const sellerCommand: UpdateSellerCommand = {
              partnerId: this.partnerId!, // biztosan ismert
              headOfficeAddress: formValue.headOfficeAddress,
              companyRegistrationNumber: formValue.companyRegistrationNumber,
              defaultBranchAddress: getPartnerAddress(this.form.value),
            };
            this.sellerService.updateSeller(this.partnerId!, sellerCommand).subscribe({
              next: () => this.router.navigate(['/master-data/partners']),
              error: () => alert('Nem sikerült frissíteni az eladó adatokat.')
            });
          },
          error: () => alert('Nem sikerült frissíteni a partner adatokat.')
        });
      } else {
        this.partnerService.createPartner(createCommand).subscribe({
          next: (created) => {
            const sellerCommand: UpdateSellerCommand = {
              partnerId: created.id, // itt kapjuk meg a partnerId-t
              headOfficeAddress: formValue.headOfficeAddress,
              companyRegistrationNumber: formValue.companyRegistrationNumber,
              defaultBranchAddress: getPartnerAddress(created),
            };
            this.sellerService.createSeller(created.id, sellerCommand).subscribe({
              next: () => this.router.navigate(['/master-data/partners']),
              error: () => alert('Nem sikerült létrehozni az eladó adatokat.')
            });
          },
          error: () => alert('Nem sikerült létrehozni a partner adatokat.')
        });
      }
    } else {
      // partner mód
      if (this.isEditMode && this.partnerId) {
        this.partnerService.updatePartner(this.partnerId, updateCommand).subscribe({
          next: () => this.router.navigate(['/master-data/partners']),
          error: () => alert('Nem sikerült frissíteni a partnert.')
        });
      } else {
        this.partnerService.createPartner(createCommand).subscribe({
          next: () => this.router.navigate(['/master-data/partners']),
          error: () => alert('Nem sikerült létrehozni a partnert.')
        });
      }
    }
  }

  onCancel(): void {
    const route = '/master-data/partners';
    this.router.navigate([route]);
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

  @HostListener('window:keydown', ['$event'])
  handleKeyDown(event: KeyboardEvent): void {
    const isModalOpen = document.body.classList.contains('modal-open');

    if (event.key === 'Escape' && !isModalOpen) {
      event.preventDefault();
      this.onCancel();
    }
  }

  @HostListener('keydown.enter', ['$event'])
  handleEnterToNext(event: KeyboardEvent): void {
    const formElements = Array.from(document.querySelectorAll<HTMLElement>('input, select, textarea, button'))
      .filter(el => !el.hasAttribute('disabled') && el.tabIndex >= 0);

    const currentIndex = formElements.indexOf(document.activeElement as HTMLElement);
    if (currentIndex > -1 && currentIndex < formElements.length - 1) {
      event.preventDefault();
      formElements[currentIndex + 1].focus();
    }
  }

  ngOnDestroy(): void { }
}
