import { Component, OnInit, HostListener } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { InvoiceNumberSequenceService } from '../../services/invoice-number-sequence.service';
import { InvoiceTypeService } from '../../services/invoice-type.service';
import { InvoiceTypeDto } from '../../models/invoice-type.dto';
import { CreateInvoiceNumberSequenceCommand } from '../../models/create-invoice-number-sequence-command';
import { UpdateInvoiceNumberSequenceCommand } from '../../models/update-invoice-number-sequence-command';

@Component({
  selector: 'app-invoice-number-sequence-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './invoice-number-sequence-form.component.html'
})
export class InvoiceNumberSequenceFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  sequenceId?: number;
  invoiceTypes: InvoiceTypeDto[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private sequenceService: InvoiceNumberSequenceService,
    private invoiceTypeService: InvoiceTypeService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      invoiceTypeId: [null, Validators.required],
      invoicePrefix: ['', Validators.required],
      lastNumber: [0, [Validators.required, Validators.min(0)]]
    });

    this.loadInvoiceTypes();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.sequenceId = +idParam;
      this.loadSequence(this.sequenceId);
    }
  }

  loadInvoiceTypes(): void {
    this.invoiceTypeService.getInvoiceTypes().subscribe({
      next: (types) => this.invoiceTypes = types,
      error: () => alert('Nem sikerült betölteni a számlatípusokat.')
    });
  }

  loadSequence(id: number): void {
    this.sequenceService.findInvoiceNumberSequenceById(id).subscribe({
      next: (sequence) => {
        this.form.patchValue({
          invoiceTypeId: sequence.invoiceType.id,
          invoicePrefix: sequence.invoicePrefix,
          lastNumber: sequence.lastNumber
        });
      },
      error: () => alert('Nem sikerült betölteni a sorszámgenerátor adatait.')
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const createInvoiceNumberSequenceCommand: CreateInvoiceNumberSequenceCommand = this.form.value;
    const updateInvoiceNumberSequenceCommand: UpdateInvoiceNumberSequenceCommand = this.form.value;

    if (this.isEditMode && this.sequenceId) {
      this.sequenceService.updateInvoiceNumberSequence(this.sequenceId, updateInvoiceNumberSequenceCommand).subscribe({
        next: () => this.router.navigate(['/master-data/invoice-number-sequences']),
        error: () => alert('Nem sikerült frissíteni a sorszámgenerátort.')
      });
    } else {
      this.sequenceService.createInvoiceNumberSequence(createInvoiceNumberSequenceCommand).subscribe({
        next: () => this.router.navigate(['/master-data/invoice-number-sequences']),
        error: () => alert('Nem sikerült létrehozni a sorszámgenerátort.')
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/master-data/invoice-number-sequences']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(): void {
    this.cancel();
  }
}
