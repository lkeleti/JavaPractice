import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { PaymentMethodDto } from '../../models/paymentMethod.dto';
import { CommonModule } from '@angular/common';
import { PaymentMethodService } from '../../services/payment-method.service';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

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

  constructor(private paymentMethodService: PaymentMethodService, private modalService: NgbModal) { }

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

  openDeleteConfirmation(paymentMethod: PaymentMethodDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés megerősítése';
    modalRef.componentInstance.message = `Biztosan törölni akarja a fizetési módot "${paymentMethod.name}" Azonosító: ${paymentMethod.id}? Ez a művelet csak logikai törlés, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Törlés';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for payment method:', paymentMethod.id);
          this.deletePaymentMethod(paymentMethod.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deletePaymentMethod(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.paymentMethodService.deletePaymentMethod(id)
      .subscribe({
        next: () => {
          console.log('Payment method deleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadPaymentMethods();
        },
        error: (err) => {
          console.error('Error deleting payment method:', err);
          this.errorMessage = err.error?.message || 'Failed to delete payment method.';
          this.isLoading = false;
        }
      });
  }

  openUnDeleteConfirmation(paymentMethod: PaymentMethodDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés visszavonásának megerősítése';
    modalRef.componentInstance.message = `Biztosan aktívvá akarja tenni a fizetési módot "${paymentMethod.name}" Azonosító: ${paymentMethod.id})? Ez a művelet csak logikai, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Aktivál';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Undeletion confirmed for partner:', paymentMethod.id);
          this.unDeletePaymentMethod(paymentMethod.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Undeletion dismissed:', reason);
      }
    );
  }

  unDeletePaymentMethod(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.paymentMethodService.unDeletePaymentMethod(id)
      .subscribe({
        next: () => {
          console.log('Partner undeleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadPaymentMethods();
        },
        error: (err) => {
          console.error('Error deleting paymnet method:', err);
          this.errorMessage = err.error?.message || 'Failed to delete payment method.';
          this.isLoading = false;
        }
      });
  }
}
