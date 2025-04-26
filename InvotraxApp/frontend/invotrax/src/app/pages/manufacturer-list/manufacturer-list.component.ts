import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ManufacturerService } from './../../services/manufacturer.service';
import { Component, OnInit } from '@angular/core';
import { ManufacturerDto } from '../../models/manufacturer.dto';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-manufacturer-list',
  imports: [
    FormsModule,
    RouterModule,
    CommonModule
  ],
  templateUrl: './manufacturer-list.component.html',
  styleUrl: './manufacturer-list.component.scss'
})
export class ManufacturerListComponent implements OnInit {
  manufacturers: ManufacturerDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private manufacturerService: ManufacturerService,
    private modalService: NgbModal
  ) { }

  searchTerm: string = '';

  ngOnInit(): void {
    this.loadManufacturers();
  }

  loadManufacturers(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.manufacturerService.getManufacturers()
      .subscribe({
        next: (data) => {
          this.manufacturers = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching manufacturers:', err);
          this.errorMessage = 'Nem sikerült betölteni a gyártókat. Kérem próbálja meg később.';
          this.isLoading = false;
        }
      });
  }

  openDeleteConfirmation(manufacturer: ManufacturerDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés megerősítése';
    modalRef.componentInstance.message = `Biztosan törölni akarja a gyártót "${manufacturer.name}" (Azonosító: ${manufacturer.id})? Ez a művelet nem vonható vissza.`;
    modalRef.componentInstance.confirmText = 'Törlés';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for manufacturer:', manufacturer.id);
          this.deleteManufacturer(manufacturer.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deleteManufacturer(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.manufacturerService.deleteManufacturer(id)
      .subscribe({
        next: () => {
          console.log('Manufacturer deleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadManufacturers();
        },
        error: (err) => {
          console.error('Error deleting manufacturer:', err);
          this.errorMessage = err.error?.message || 'Failed to delete manufacturer.';
          this.isLoading = false;
        }
      });
  }

  get filteredManufacturers(): ManufacturerDto[] {
    if (!this.searchTerm) return this.manufacturers;
    const term = this.searchTerm.toLowerCase();
    return this.manufacturers.filter(
      (manufacturer) =>
        manufacturer.name.toLowerCase().includes(term) ||
        manufacturer.website?.toString().toLowerCase().includes(term)
    );
  }

}
