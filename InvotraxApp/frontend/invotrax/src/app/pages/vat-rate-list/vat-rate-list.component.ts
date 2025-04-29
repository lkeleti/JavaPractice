import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { VatRateDto } from '../../models/vat-rate.dto';
import { CommonModule } from '@angular/common';
import { VatRateService } from '../../services/vat-rate.service';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-vat-rate-list',
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './vat-rate-list.component.html',
  styleUrl: './vat-rate-list.component.scss',
})
export class VatRateListComponent implements OnInit {
  vatRates: VatRateDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private vatRateService: VatRateService,
    private modalService: NgbModal
  ) {}

  searchTerm: string = '';

  ngOnInit(): void {
    this.loadVatRates();
  }

  loadVatRates(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.vatRateService.getVatRates().subscribe({
      next: (data) => {
        this.vatRates = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching vat rates:', err);
        this.errorMessage =
          'Nem sikerült betölteni az Áfa kódokat. Kérem próbálja meg később.';
        this.isLoading = false;
      },
    });
  }

  get filteredvatRates(): VatRateDto[] {
    if (!this.searchTerm) return this.vatRates;
    const term = this.searchTerm.toLowerCase();
    return this.vatRates.filter(
      (vatRates) =>
        vatRates.name.toLowerCase().includes(term) ||
        vatRates.rate.toString().includes(term)
    );
  }

  openDeleteConfirmation(vatRate: VatRateDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés megerősítése';
    modalRef.componentInstance.message = `Biztosan törölni akarja az Áfa kódot "${vatRate.name}" Érték: ${vatRate.rate})? Ez a művelet csak logikai törlés, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Törlés';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for vat rate:', vatRate.id);
          this.deleteVatRate(vatRate.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deleteVatRate(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.vatRateService.deleteVatRate(id).subscribe({
      next: () => {
        console.log('Vat rate deleted successfully:', id);
        this.isLoading = false;
        // Frissítjük a listát a törlés után
        this.loadVatRates();
      },
      error: (err) => {
        console.error('Error deleting vat rate:', err);
        this.errorMessage = err.error?.message || 'Failed to delete vat rate.';
        this.isLoading = false;
      },
    });
  }

  openUnDeleteConfirmation(vatRate: VatRateDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés visszavonásának megerősítése';
    modalRef.componentInstance.message = `Biztosan aktívvá akarja tenni az Áfa kódot "${vatRate.name}" Érték: ${vatRate.rate})? Ez a művelet csak logikai, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Aktivál';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Undeletion confirmed for vat rate type:', vatRate.id);
          this.unDeleteVatRate(vatRate.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Undeletion dismissed:', reason);
      }
    );
  }

  unDeleteVatRate(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.vatRateService.unDeleteVatRate(id).subscribe({
      next: () => {
        console.log('Zipcode undeleted successfully:', id);
        this.isLoading = false;
        // Frissítjük a listát a törlés után
        this.loadVatRates();
      },
      error: (err) => {
        console.error('Error deleting vat rate:', err);
        this.errorMessage = err.error?.message || 'Failed to delete vat rate.';
        this.isLoading = false;
      },
    });
  }
}
