import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductCategoryService } from '../../services/product-category.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { UpdateProductCategoryCommand } from '../../models/update-product-category-command';
import { CreateProductCategoryCommand } from '../../models/create-product_category-command';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-product-category-form',
  templateUrl: './product-category-form.component.html',
  styleUrl: './product-category-form.component.scss',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
})

export class ProductCategoryFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  productCategoryId?: number;
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productCategoryService: ProductCategoryService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
    });

    this.productCategoryId = Number(this.route.snapshot.paramMap.get('id'));
    this.isEditMode = !!this.productCategoryId;

    if (this.isEditMode) {
      this.loadCategory();
    }
  }

  loadCategory(): void {
    this.isLoading = true;
    this.productCategoryService.findProductCategoryById(this.productCategoryId!).subscribe({
      next: (data) => {
        this.form.patchValue(data);
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Nem sikerült betölteni a kategóriát.';
        this.isLoading = false;
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const updateProductCategoryCommand: UpdateProductCategoryCommand = this.form.value;
    const createProductCategoryCommand: CreateProductCategoryCommand = this.form.value;

    if (this.isEditMode && this.productCategoryId) {
      this.productCategoryService.updateProductCategory(this.productCategoryId, updateProductCategoryCommand).subscribe({
        next: () => this.router.navigate(['/master-data/product-categories']),
        error: () => alert('Nem sikerült frissíteni a terméktípust.')
      });
    } else {
      this.productCategoryService.createProductCategory(createProductCategoryCommand).subscribe({
        next: () => this.router.navigate(['/master-data/product-categories']),
        error: () => alert('Nem sikerült létrehozni a terméktípust.')
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/master-data/product-categories']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent): void {
    this.cancel();
  }
}
