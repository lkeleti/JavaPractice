import { ManufacturerService } from './../../services/manufacturer.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, NgModel, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { ProductDto } from '../../models/product.dto';
import { ProductCategoryDto } from '../../models/product-category.dto';
import { ManufacturerDto } from '../../models/manufacturer.dto';
import { VatRateDto } from '../../models/vat-rate.dto';
import { ProductTypeDto } from '../../models/productType.dto';
import { ProductCategoryService } from '../../services/product-category.service';
import { VatRateService } from '../../services/vat-rate.service';
import { ProductTypeService } from '../../services/product-type.service';
import { CommonModule } from '@angular/common';
import { EntityLookupModalComponent } from '../../shared/entity-lookup-modal/entity-lookup-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { map } from 'rxjs';
import { BarcodeManagerModalComponent } from '../barcode-manager-modal/barcode-manager-modal.component';
import { BarcodeDto } from '../../models/barcode.dto';
import { SerialNumberManagerModalComponent } from '../serial-number-manager-modal/serial-number-manager-modal.component';
import { SerialNumberDto } from '../../models/serial-number.dto';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
})
export class ProductFormComponent implements OnInit {
  form!: FormGroup;
  id!: number | null;

  showSerialNumberModal = false;

  // Legördülő adatok
  categories: ProductCategoryDto[] = [];
  vatRates: VatRateDto[] = [];
  productTypes: ProductTypeDto[] = [];

  // Betöltött entitás (edit módban)
  productToEdit!: ProductDto;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private productCategoryService: ProductCategoryService,
    private vatRateService: VatRateService,
    private productTypeService: ProductTypeService,
    private manufacturerService: ManufacturerService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
  ) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'] ? +this.route.snapshot.params['id'] : null;

    this.initForm();
    this.loadSelectData();

    if (this.id) {
      this.productService.findProductById(this.id).subscribe({
        next: (product) => {
          this.productToEdit = product;
          this.form.patchValue(product);
        },
        error: () => {
          // hibakezelés vagy visszairányítás
        },
      });
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      sku: ['', Validators.required],
      description: [''],
      productType: [null, Validators.required],
      category: [null, Validators.required],
      manufacturer: [null, Validators.required],
      netPurchasePrice: [{ value: null, disabled: true }],
      grossPurchasePrice: [{ value: null, disabled: true }],
      netSellingPrice: [{ value: null, disabled: true }],
      grossSellingPrice: [null],
      warrantyPeriodMonths: [null],
      serialNumberRequired: [false],
      stockQuantity: [{ value: 0, disabled: true }],
      unit: ['', Validators.required],
      vatRate: [null, Validators.required],
      deleted: [false],
      barcodes: [[]],
      serialNumbers: [[]],
    });
  }

  private loadSelectData(): void {
    this.productCategoryService.getProductCategories().subscribe((cats) => (this.categories = cats));
    this.vatRateService.getVatRates().subscribe((rates) => (this.vatRates = rates));
    this.productTypeService.getProductTypes().subscribe((types) => (this.productTypes = types));
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const product: ProductDto = {
      ...this.form.getRawValue(),
      id: this.id ?? undefined,
    };

    /*const request$ = this.id
      ? this.productService.updateProduct(product.id, product)
      : this.productService.createProduct(product);

    request$.subscribe({
      next: () => this.router.navigate(['/products']),
      error: (err) => {
        // hibakezelés
        console.error(err);
      },
    });*/
  }

  openManufacturerLookup(): void {
    const modalRef = this.modalService.open(EntityLookupModalComponent<ManufacturerDto>, {
      size: 'lg',
      backdrop: 'static',
      keyboard: true
    });

    modalRef.componentInstance.title = 'Gyártó keresés';
    modalRef.componentInstance.fetchFunction = (page: number, size: number, search: string | undefined) =>
      this.manufacturerService.getManufacturers(page, size, search).pipe(map(res => res.content));

    modalRef.componentInstance.displayFn = (manufacturer: ManufacturerDto) => ({
      label: manufacturer.name,
      subLabel: manufacturer.website
    });

    modalRef.result
      .then((selectedId: number) => {
        if (selectedId) {
          this.manufacturerService.findManufacturerById(selectedId).subscribe({
            next: manufacturer => this.form.get('manufacturer')?.setValue(manufacturer)
          });
        }
      })
      .catch(() => { });
  }

  openBarcodeManager(): void {
    const modalRef = this.modalService.open(BarcodeManagerModalComponent, {
      size: 'lg',
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.initialBarcodes = this.form.value.barcodes;

    modalRef.result
      .then((updatedBarcodes: BarcodeDto[]) => {
        this.form.get('barcodes')?.setValue(updatedBarcodes);
      })
      .catch(() => { });
  }

  openSerialNumberManager(): void {
    const modalRef = this.modalService.open(SerialNumberManagerModalComponent, {
      size: 'lg',
      backdrop: 'static',
      keyboard: true
    });

    modalRef.componentInstance.initialserialNumbers = this.form.value.serialnumbers;

    modalRef.result
      .then((updatedSerialNumbers: SerialNumberDto[]) => {
        this.form.get('serialNumbers')?.setValue(updatedSerialNumbers);
      })
      .catch(() => { });
  }

  onCancel(): void {
    this.router.navigate(['/master-data/products']);
  }

}