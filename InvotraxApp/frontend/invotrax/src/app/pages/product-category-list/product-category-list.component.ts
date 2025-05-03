import { Component, OnInit } from '@angular/core';
import { ProductCategoryService } from '../../services/product-category.service';
import { ProductCategoryDto } from '../../models/product-category.dto';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-category-list',
  templateUrl: './product-category-list.component.html',
  styleUrl: './product-category-list.component.scss',
  imports: [CommonModule, FormsModule, RouterModule],
})
export class ProductCategoryListComponent implements OnInit {
  categories: ProductCategoryDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  searchTerm = '';

  constructor(
    private categoryService: ProductCategoryService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getProductCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Nem sikerült betölteni a kategóriákat.';
        this.isLoading = false;
      },
    });
  }

  get filteredCategories(): ProductCategoryDto[] {
    const term = this.searchTerm.toLowerCase();
    return this.categories.filter((cat) =>
      cat.name.toLowerCase().includes(term)
    );
  }
}


