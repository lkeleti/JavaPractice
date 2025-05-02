import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { InvoiceTypeService } from '../../services/invoice-type.service';
import { UpdateInvoiceTypeCommand } from '../../models/update-invoice-type-command';
import { CreateInvoiceTypeCommand } from '../../models/create-invoice-type-command';

@Component({
  selector: 'app-invoice-type-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './invoice-type-form.component.html'
})

export class InvoiceTypeFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  invoiceTypeId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private invoiceTypeService: InvoiceTypeService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      deleted: [false] // csak edit módban marad
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.invoiceTypeId = +idParam;
      this.loadInvoiceType(this.invoiceTypeId);
    } else {
      this.form.removeControl('deleted'); // create módban nem kell
    }
  }

  loadInvoiceType(id: number): void {
    this.invoiceTypeService.findInvoiceTypeById(id).subscribe({
      next: (type) => this.form.patchValue(type),
      error: () => alert('Nem sikerült betölteni a számlatípus adatait.')
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const createInvoiceTypeCommand: CreateInvoiceTypeCommand = this.form.value;
    const updateInvoiceTypeCommand: UpdateInvoiceTypeCommand = this.form.value;

    if (this.isEditMode && this.invoiceTypeId) {
      this.invoiceTypeService.updateInvoiceType(this.invoiceTypeId, updateInvoiceTypeCommand).subscribe({
        next: () => this.router.navigate(['/master-data/invoice-types']),
        error: () => alert('Nem sikerült frissíteni a számlatípust.')
      });
    } else {
      this.invoiceTypeService.createInvoiceType(createInvoiceTypeCommand).subscribe({
        next: () => this.router.navigate(['/master-data/invoice-types']),
        error: () => alert('Nem sikerült létrehozni a számlatípust.')
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/master-data/invoice-types']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent): void {
    this.cancel();
  }
}
