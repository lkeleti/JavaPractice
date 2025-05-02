import { CreateVatRateCommand } from './../../models/create-vat-rate-command';
import { UpdateVatRateCommand } from './../../models/update-vat-rate-command';
import { CreateManufacturerCommand } from './../../models/create-manufacturer-command';
import { UpdateManufacturerCommand } from './../../models/update-manufacturer-command';
import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { VatRateService } from '../../services/vat-rate.service';
import { VatRateDto } from '../../models/vat-rate.dto';

@Component({
  selector: 'app-vat-rate-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './vat-rate-form.component.html'
})

export class VatRateFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  vatRateId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private vatRateService: VatRateService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      rate: [null, [Validators.required, Validators.min(0)]],
      deleted: [false] // csak szerkesztésnél töltjük fel
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.vatRateId = +idParam;
      this.loadVatRate(this.vatRateId);
    } else {
      this.form.removeControl('deleted'); // create módban ne legyen benne
    }
  }

  loadVatRate(id: number): void {
    this.vatRateService.findVatRateById(id).subscribe({
      next: (vatRate) => this.form.patchValue(vatRate),
      error: () => alert('Nem sikerült betölteni az ÁFA kulcs adatait.')
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const updateVatRateCommand: UpdateVatRateCommand = this.form.value;
    const createVatRateCommand: CreateVatRateCommand = this.form.value;

    if (this.isEditMode && this.vatRateId) {
      this.vatRateService.updateVatRate(this.vatRateId, updateVatRateCommand).subscribe({
        next: () => this.router.navigate(['/master-data/vat-rates']),
        error: () => alert('Nem sikerült frissíteni az ÁFA kulcsot.')
      });
    } else {
      this.vatRateService.createVatRate(createVatRateCommand).subscribe({
        next: () => this.router.navigate(['/master-data/vat-rates']),
        error: () => alert('Nem sikerült létrehozni az ÁFA kulcsot.')
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/master-data/vat-rates']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent): void {
    this.cancel();
  }
}

