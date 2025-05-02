import {
  Component,
  OnInit,
  HostListener,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormsModule
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PaymentMethodService } from '../../services/payment-method.service';
import { PaymentMethodDto } from '../../models/paymentMethod.dto';
import { CreatePaymentMethodCommand } from '../../models/create-payment-method.command';
import { UpdatePaymentMethodCommand } from './../../models/update-payment-method.command';


@Component({
  selector: 'app-payment-method-form',
  templateUrl: './payment-method-form.component.html',
  standalone: true,
  styleUrls: ['./payment-method-form.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class PaymentMethodFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  paymentMethodId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private paymentMethodService: PaymentMethodService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.paymentMethodId = +idParam;
      this.loadPaymentMethod(this.paymentMethodId);
    }
  }

  loadPaymentMethod(id: number): void {
    this.paymentMethodService.findPaymentMethodById(id).subscribe({
      next: (pm: PaymentMethodDto) => this.form.patchValue(pm),
      error: () => alert('Nem sikerült betölteni a fizetési módot.')
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.value;

    if (this.isEditMode && this.paymentMethodId) {
      const command: UpdatePaymentMethodCommand = { name: value.name };
      this.paymentMethodService.updatePaymentMethod(this.paymentMethodId, command).subscribe({
        next: () => this.router.navigate(['/master-data/payment-methods']),
        error: () => alert('Nem sikerült frissíteni a fizetési módot.')
      });
    } else {
      const command: CreatePaymentMethodCommand = { name: value.name };
      this.paymentMethodService.createPaymentMethod(command).subscribe({
        next: () => this.router.navigate(['/master-data/payment-methods']),
        error: () => alert('Nem sikerült létrehozni a fizetési módot.')
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/master-data/payment-methods']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent): void {
    this.onCancel();
  }
}

