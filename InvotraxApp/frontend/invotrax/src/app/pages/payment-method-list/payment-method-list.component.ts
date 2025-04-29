import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { PaymentMethodDto } from '../../models/paymentMethod.dto';
import { CommonModule } from '@angular/common';
import { PaymentMethodService } from '../../services/payment-method.service';

@Component({
  selector: 'app-manufacturer-list',
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './payment-method-list.component.html',
  styleUrl: './payment-method-list.component.scss',
})
export class PaymentMethodListComponent implements OnInit {
  paymentMethods: PaymentMethodDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private paymentMethodService: PaymentMethodService) {}

  searchTerm: string = '';

  ngOnInit(): void {
    this.loadPaymentMethods();
  }

  loadPaymentMethods(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.paymentMethodService.getPaymentMethods().subscribe({
      next: (data) => {
        this.paymentMethods = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching payment methods:', err);
        this.errorMessage =
          'Nem sikerült betölteni a fizetési módokat. Kérem próbálja meg később.';
        this.isLoading = false;
      },
    });
  }

  get filteredPaymentMethods(): PaymentMethodDto[] {
    if (!this.searchTerm) return this.paymentMethods;
    const term = this.searchTerm.toLowerCase();
    return this.paymentMethods.filter((paymentMethod) =>
      paymentMethod.name.toLowerCase().includes(term)
    );
  }
}
