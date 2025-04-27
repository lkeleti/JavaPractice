import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ManufacturerService } from './../../services/manufacturer.service';
import { Component, OnInit } from '@angular/core';
import { ManufacturerDto } from '../../models/manufacturer.dto';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { CommonModule } from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination'; // <-- Import NgxPaginationModule
import { Subject } from 'rxjs'; // <-- Import Subject
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators'; // <-- Import operators
import { PaginatedManufacturersResponse } from '../../models/paginated-response.dto';

@Component({
  selector: 'app-manufacturer-list',
  imports: [
    FormsModule,
    RouterModule,
    CommonModule,
    NgxPaginationModule
  ],
  templateUrl: './manufacturer-list.component.html',
  styleUrl: './manufacturer-list.component.scss'
})
export class ManufacturerListComponent implements OnInit {
  isLoading = false;
  errorMessage: string | null = null;

  manufacturers: ManufacturerDto[] = [];
  totalItems: number = 0;
  currentPage: number = 1;
  itemsPerPage: number = 10;
  searchTerm: string = '';
  sort: string = '';
  private searchSubject = new Subject<string>();

  constructor(
    private manufacturerService: ManufacturerService,
    private modalService: NgbModal
  ) { }


  ngOnInit(): void {
    this.loadManufacturers();
    this.searchSubject.pipe(
      debounceTime(500), // Wait for 500ms pause in events
      distinctUntilChanged(), // Only emit if value changed
      // No need for switchMap here if loadManufacturers handles the search term
      // switchMap(term => this.manufacturerService.getManufacturers(1, this.itemsPerPage, term))
    ).subscribe(term => {
      // Reset to first page on new search
      this.currentPage = 1;
      this.loadManufacturers(); // Reload data with the new search term
    });
  }

  // Method to trigger search subject update
  onSearchTermChange(term: string): void {
    this.searchSubject.next(term);
  }

  loadManufacturers(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.manufacturerService.getManufacturers(
      this.currentPage,
      this.itemsPerPage,
      this.searchTerm // Pass the current search term
      // Add sorting parameter if needed: this.currentSort
    ).subscribe({
      next: (response: PaginatedManufacturersResponse) => {
        this.manufacturers = response.content; // Get items for the current page
        this.totalItems = response.totalElements; // Get total count for pagination controls
        // this.currentPage = response.number + 1; // Sync page number if needed (careful with loops)
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching manufacturers:', err);
        const defaultMsg = 'Nem sikerült betölteni a gyártókat. Kérem próbálja meg később.';
        this.errorMessage = err.error?.message || err.message || defaultMsg;
        this.isLoading = false;
      }
    });
  }

  // Called by the pagination controls component
  pageChanged(event: number): void {
    this.currentPage = event;
    this.loadManufacturers();
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
