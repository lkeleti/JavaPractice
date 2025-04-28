import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ManufacturerService } from './../../services/manufacturer.service';
import { Component, OnInit } from '@angular/core';
import { ManufacturerDto } from '../../models/manufacturer.dto';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { CommonModule } from '@angular/common';
import { PaginatedManufacturersResponse } from '../../models/paginated-response.dto';

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
  isLoading = false;
  errorMessage: string | null = null;

  manufacturers: ManufacturerDto[] = [];
  totalItems = 0;
  currentPage = 1;
  pageSizeOptions: number[] = [10, 25, 50, 100];
  itemsPerPage: number = 10;
  searchTerm = '';
  currentSortField = 'name'; // alapértelmezett mező
  sortDirection = 'asc'; // alapértelmezett irány

  constructor(
    private manufacturerService: ManufacturerService,
    private modalService: NgbModal
  ) { }


  ngOnInit(): void {
    this.loadManufacturers();
  }

  loadManufacturers(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const sortParam = `${this.currentSortField},${this.sortDirection}`;

    this.manufacturerService.getManufacturers(
      this.currentPage,
      this.itemsPerPage,
      this.searchTerm,
      sortParam
    ).subscribe({
      next: (response: PaginatedManufacturersResponse) => {
        this.manufacturers = response.content;
        this.totalItems = response.page.totalElements;
        this.itemsPerPage = response.page.size;
        this.currentPage = response.page.number + 1;
        this.isLoading = false;
        console.log(this.totalItems + " " + this.itemsPerPage);
        console.log(response);
      },
      error: (err) => {
        console.error('Error fetching manufacturers:', err);
        this.errorMessage = err.error?.message || err.message || 'Hiba a gyártók betöltésekor';
        this.isLoading = false;
      }
    });
  }

  pageChanged(newPage: number): void {
    this.currentPage = newPage;
    this.loadManufacturers();
  }

  onSearchTermChange(): void {
    this.currentPage = 1;
    this.loadManufacturers();
  }

  changeSort(field: string): void {
    if (this.currentSortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortField = field;
      this.sortDirection = 'asc';
    }
    this.loadManufacturers();
  }

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  onItemsPerPageChange(event: any): void {
    this.currentPage = 1;
    this.loadManufacturers();
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadManufacturers();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadManufacturers();
    }
  }

  goToPage(page: number): void {
    if (page !== this.currentPage) {
      this.currentPage = page;
      this.loadManufacturers();
    }
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
}
