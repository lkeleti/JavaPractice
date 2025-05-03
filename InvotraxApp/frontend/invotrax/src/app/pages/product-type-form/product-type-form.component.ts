import { UpdateProductTypeCommand } from './../../models/update-product-type.command';
import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductTypeService } from '../../services/product-type.service';
import { CreateProductTypeCommand } from '../../models/create-product-type-command';

@Component({
  selector: 'app-product-type-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './product-type-form.component.html'
})
export class ProductTypeFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  productTypeId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productTypeService: ProductTypeService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      managesStock: [true, Validators.required]
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.productTypeId = +idParam;
      this.loadProductType(this.productTypeId);
    }
  }

  loadProductType(id: number): void {
    this.productTypeService.findProductTypeById(id).subscribe({
      next: (type) => this.form.patchValue(type),
      error: () => alert('Nem sikerült betölteni a terméktípus adatait.')
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const updateProductTypeCommand: UpdateProductTypeCommand = this.form.value;
    const createProductTypeCommand: CreateProductTypeCommand = this.form.value;

    if (this.isEditMode && this.productTypeId) {
      this.productTypeService.updateProductType(this.productTypeId, updateProductTypeCommand).subscribe({
        next: () => this.router.navigate(['/master-data/product-types']),
        error: () => alert('Nem sikerült frissíteni a terméktípust.')
      });
    } else {
      this.productTypeService.createProductType(createProductTypeCommand).subscribe({
        next: () => this.router.navigate(['/master-data/product-types']),
        error: () => alert('Nem sikerült létrehozni a terméktípust.')
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/master-data/product-types']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent): void {
    this.cancel();
  }
}
