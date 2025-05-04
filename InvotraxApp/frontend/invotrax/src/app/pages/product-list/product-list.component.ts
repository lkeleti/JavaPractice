import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Component, OnInit } from '@angular/core';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { CommonModule } from '@angular/common';
import { PaginationControlsComponent } from '../../shared/pagination-controls/pagination-controls.component';
import { ProductDto } from '../../models/product.dto';
import { ProductService } from '../../services/product.service';
import { PaginatedProductsResponse } from '../../models/paginated-response.dto';


@Component({
  selector: 'app-partner-list',
  imports: [
    FormsModule,
    RouterModule,
    CommonModule,
    PaginationControlsComponent
  ],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent implements OnInit {
  isLoading = false;
  errorMessage: string | null = null;

  products: ProductDto[] = [];
  totalItems = 0;
  currentPage = 1;
  pageSizeOptions: number[] = [10, 25, 50, 100];
  itemsPerPage: number = 10;
  searchTerm = '';
  currentSortField = 'name'; // alapértelmezett mező
  sortDirection = 'asc'; // alapértelmezett irány



  constructor(
    private productService: ProductService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = null;

    const sortParam = `${this.currentSortField},${this.sortDirection}`;

    this.productService.getProducts
      (
        this.currentPage,
        this.itemsPerPage,
        this.searchTerm,
        sortParam
      ).subscribe({
        next: (response: PaginatedProductsResponse) => {
          this.products = response.content;
          this.totalItems = response.page.totalElements;
          this.itemsPerPage = response.page.size;
          this.currentPage = response.page.number + 1;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Hiba a termékek betöltésekor', error);
          this.errorMessage = 'Hiba történt a termékek betöltése közben.';
          this.isLoading = false;
        },
      });
  }

  pageChanged(newPage: number): void {
    this.currentPage = newPage;
    this.loadProducts();
  }

  onSearchTermChange(): void {
    this.currentPage = 1;
    this.loadProducts();
  }

  changeSort(field: string): void {
    if (this.currentSortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.currentSortField = field;
      this.sortDirection = 'asc';
    }
    this.loadProducts();
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
    this.loadProducts();
  }

  goToPage(page: number | string): void {
    if (typeof page === 'number' && page !== this.currentPage) {
      this.currentPage = page;
      this.loadProducts();
    }
  }

  openDeleteConfirmation(product: ProductDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés megerősítése';
    modalRef.componentInstance.message = `Biztosan törölni akarja a terméket "${product.name}" SKU: ${product.sku})? Ez a művelet csak logikai törlés, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Törlés';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for partner:', product.id);
          this.deleteProduct(product.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deleteProduct(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.productService.deleteProduct(id)
      .subscribe({
        next: () => {
          console.log('Partner deleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadProducts();
        },
        error: (err) => {
          console.error('Error deleting product:', err);
          this.errorMessage = err.error?.message || 'Failed to delete product.';
          this.isLoading = false;
        }
      });
  }

  openUnDeleteConfirmation(product: ProductDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Törlés visszavonásának megerősítése';
    modalRef.componentInstance.message = `Biztosan aktívvá akarja tenni a terméket "${product.name}" SKU: ${product.sku})? Ez a művelet csak logikai, visszavonható.`;
    modalRef.componentInstance.confirmText = 'Aktivál';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Undeletion confirmed for partner:', product.id);
          this.unDeleteProduct(product.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Undeletion dismissed:', reason);
      }
    );
  }

  unDeleteProduct(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.productService.unDeleteProduct(id)
      .subscribe({
        next: () => {
          console.log('Product undeleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadProducts();
        },
        error: (err) => {
          console.error('Error deleting partner:', err);
          this.errorMessage = err.error?.message || 'Failed to delete partner.';
          this.isLoading = false;
        }
      });
  }
}