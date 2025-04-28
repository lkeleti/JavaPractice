import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ZipCodeService } from '../../services/zip-code.service';
import { Component, OnInit } from '@angular/core';
import { ZipCodeDto } from '../../models/zip-code.dto';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { CommonModule } from '@angular/common';
import { PaginatedZipCodesResponse } from '../../models/paginated-response.dto';
import { PaginationControlsComponent } from '../../shared/pagination-controls/pagination-controls.component';


@Component({
  selector: 'app-zip-code-list',
  imports: [
    FormsModule,
    RouterModule,
    CommonModule,
    PaginationControlsComponent
  ],
  templateUrl: './zip-code-list.component.html',
  styleUrl: './zip-code-list.component.scss'
})
export class ZipCodeListComponent {

  isLoading = false;
  errorMessage: string | null = null;

  zipCodes: ZipCodeDto[] = [];
  totalItems = 0;
  currentPage = 1;
  pageSizeOptions: number[] = [10, 25, 50, 100];
  itemsPerPage: number = 10;
  searchTerm = '';
  currentSortField = 'zip'; // alapértelmezett mező
  sortDirection = 'asc'; // alapértelmezett irány

  constructor(
    private zipCodeService: ZipCodeService,
    private modalService: NgbModal
  ) { }


  ngOnInit(): void {
    this.loadZipCodes();
  }

  loadZipCodes(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const sortParam = `${this.currentSortField},${this.sortDirection}`;

    this.zipCodeService.getZipCodes(
      this.currentPage,
      this.itemsPerPage,
      this.searchTerm,
      sortParam
    ).subscribe({
      next: (response: PaginatedZipCodesResponse) => {
        this.zipCodes = response.content;
        this.totalItems = response.page.totalElements;
        this.itemsPerPage = response.page.size;
        this.currentPage = response.page.number + 1;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching zipcodes:', err);
        this.errorMessage = err.error?.message || err.message || 'Hiba az irányítószámok betöltésekor';
        this.isLoading = false;
      }
    });
  }

  pageChanged(newPage: number): void {
    this.currentPage = newPage;
    this.loadZipCodes();
  }

  onSearchTermChange(): void {
    this.currentPage = 1;
    this.loadZipCodes();
  }

  changeSort(field: string): void {
    if (this.currentSortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortField = field;
      this.sortDirection = 'asc';
    }
    this.loadZipCodes();
  }

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  get pages(): (number | string)[] {
    const total = this.totalPages;
    const current = this.currentPage;
    const delta = 2; // Aktuális oldal körüli megjelenített oldalak száma

    const range: number[] = [];
    const rangeWithDots: (number | string)[] = [];
    let l: number | null = null; // <<< FONTOS! null-al kezdjük

    for (let i = 1; i <= total; i++) {
      if (i === 1 || i === total || (i >= current - delta && i <= current + delta)) {
        range.push(i);
      }
    }

    for (let i of range) {
      if (l !== null) {  // <<< Itt is pontosan ellenőrzünk!
        if (i - l === 2) {
          rangeWithDots.push(l + 1);
        } else if (i - l > 2) {
          rangeWithDots.push('...');
        }
      }
      rangeWithDots.push(i);
      l = i;
    }

    return rangeWithDots;
  }


  onItemsPerPageChange(newItemsPerPage: number): void {
    this.itemsPerPage = newItemsPerPage;
    this.currentPage = 1;
    this.loadZipCodes();
  }


  goToPage(page: number | string): void {
    if (typeof page === 'number' && page !== this.currentPage) {
      this.currentPage = page;
      this.loadZipCodes();
    }
  }

  openDeleteConfirmation(zipCode: ZipCodeDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés megerősítése';
    modalRef.componentInstance.message = `Biztosan törölni akarja az irányítószámot "${zipCode.zip}" Város: ${zipCode.city})? Ez a művelet csak logikai törlés, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Törlés';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for zipcode:', zipCode.id);
          this.deleteZipCode(zipCode.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deleteZipCode(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.zipCodeService.deleteZipCode(id)
      .subscribe({
        next: () => {
          console.log('Zipcode deleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadZipCodes();
        },
        error: (err) => {
          console.error('Error deleting zipcode:', err);
          this.errorMessage = err.error?.message || 'Failed to delete zipcode.';
          this.isLoading = false;
        }
      });
  }

  openUnDeleteConfirmation(zipCode: ZipCodeDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés visszavonásának megerősítése';
    modalRef.componentInstance.message = `Biztosan aktívvá akarja tenni az irányítószámot "${zipCode.zip}" Város: ${zipCode.city})? Ez a művelet csak logikai, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Aktivál';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Undeletion confirmed for zipcode:', zipCode.id);
          this.unDeleteZipCode(zipCode.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Undeletion dismissed:', reason);
      }
    );
  }

  unDeleteZipCode(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.zipCodeService.unDeleteZipCode(id)
      .subscribe({
        next: () => {
          console.log('Zipcode undeleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadZipCodes();
        },
        error: (err) => {
          console.error('Error deleting zipcode:', err);
          this.errorMessage = err.error?.message || 'Failed to delete zipcode.';
          this.isLoading = false;
        }
      });
  }
}
