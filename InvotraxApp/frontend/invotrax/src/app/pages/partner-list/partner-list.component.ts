import { PartnerDto } from './../../models/partner.dto';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PartnerService } from '../../services/partner.service';
import { Component, OnInit } from '@angular/core';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { CommonModule } from '@angular/common';
import { PaginatedPartnersResponse } from '../../models/paginated-response.dto';
import { PaginationControlsComponent } from '../../shared/pagination-controls/pagination-controls.component';
import { getPartnerAddress } from '../../shared/utils/address.util';


@Component({
  selector: 'app-partner-list',
  imports: [
    FormsModule,
    RouterModule,
    CommonModule,
    PaginationControlsComponent
  ],
  templateUrl: './partner-list.component.html',
  styleUrl: './partner-list.component.scss'
})
export class PartnerListComponent implements OnInit {
  getPartnerAddress = getPartnerAddress;
  isLoading = false;
  errorMessage: string | null = null;

  partners: PartnerDto[] = [];
  totalItems = 0;
  currentPage = 1;
  pageSizeOptions: number[] = [10, 25, 50, 100];
  itemsPerPage: number = 10;
  searchTerm = '';
  currentSortField = 'name'; // alapértelmezett mező
  sortDirection = 'asc'; // alapértelmezett irány

  constructor(
    private partnerService: PartnerService,
    private modalService: NgbModal
  ) { }


  ngOnInit(): void {
    this.loadPartners();
  }

  loadPartners(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const sortParam = `${this.currentSortField},${this.sortDirection}`;

    this.partnerService.getPartners(
      this.currentPage,
      this.itemsPerPage,
      this.searchTerm,
      sortParam
    ).subscribe({
      next: (response: PaginatedPartnersResponse) => {
        this.partners = response.content;
        this.totalItems = response.page.totalElements;
        this.itemsPerPage = response.page.size;
        this.currentPage = response.page.number + 1;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching partners:', err);
        this.errorMessage = err.error?.message || err.message || 'Hiba a partnerek betöltésekor';
        this.isLoading = false;
      }
    });
  }

  pageChanged(newPage: number): void {
    this.currentPage = newPage;
    this.loadPartners();
  }

  onSearchTermChange(): void {
    this.currentPage = 1;
    this.loadPartners();
  }

  changeSort(field: string): void {
    if (this.currentSortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortField = field;
      this.sortDirection = 'asc';
    }
    this.loadPartners();
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
    this.loadPartners();
  }


  goToPage(page: number | string): void {
    if (typeof page === 'number' && page !== this.currentPage) {
      this.currentPage = page;
      this.loadPartners();
    }
  }

  openDeleteConfirmation(partner: PartnerDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés megerősítése';
    modalRef.componentInstance.message = `Biztosan törölni akarja a partnert "${partner.name}" Város: ${getPartnerAddress(partner)})? Ez a művelet csak logikai törlés, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Törlés';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for partner:', partner.id);
          this.deletePartner(partner.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deletePartner(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.partnerService.deletePartner(id)
      .subscribe({
        next: () => {
          console.log('Partner deleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadPartners();
        },
        error: (err) => {
          console.error('Error deleting partner:', err);
          this.errorMessage = err.error?.message || 'Failed to delete partner.';
          this.isLoading = false;
        }
      });
  }

  openUnDeleteConfirmation(partner: PartnerDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés visszavonásának megerősítése';
    modalRef.componentInstance.message = `Biztosan aktívvá akarja tenni a partnert "${partner.name}" Város: ${getPartnerAddress(partner)})? Ez a művelet csak logikai, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Aktivál';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Undeletion confirmed for partner:', partner.id);
          this.unDeletePartner(partner.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Undeletion dismissed:', reason);
      }
    );
  }

  unDeletePartner(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.partnerService.unDeletePartner(id)
      .subscribe({
        next: () => {
          console.log('Partner undeleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadPartners();
        },
        error: (err) => {
          console.error('Error deleting partner:', err);
          this.errorMessage = err.error?.message || 'Failed to delete partner.';
          this.isLoading = false;
        }
      });
  }
}