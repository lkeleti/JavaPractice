import { Component, OnInit } from '@angular/core';
import { ProductTypeDto } from '../../models/productType.dto';
import { ProductTypeService } from '../../services/product-type.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-product-type-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './product-type-list.component.html',
  styleUrl: './product-type-list.component.scss',
})

export class ProductTypeListComponent implements OnInit {
  productTypes: ProductTypeDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  searchTerm: string = '';

  constructor(
    private productTypeService: ProductTypeService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    this.loadProductTypes();
  }

  loadProductTypes(): void {
    this.isLoading = true;
    this.productTypeService.getProductTypes().subscribe({
      next: (data) => {
        this.productTypes = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching product types:', err);
        this.errorMessage =
          'Nem sikerült betölteni a terméktípusokat. Kérem próbálja meg később.';
        this.isLoading = false;
      },
    });
  }

  get filteredProductTypes(): ProductTypeDto[] {
    if (!this.searchTerm) return this.productTypes;
    const term = this.searchTerm.toLowerCase();
    return this.productTypes.filter((pt) =>
      pt.name.toLowerCase().includes(term)
    );
  }
}
