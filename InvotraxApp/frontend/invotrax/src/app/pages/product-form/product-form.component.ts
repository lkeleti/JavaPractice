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
import { UpdateProductCommand } from '../../models/update-product-command';
import { CreateProductCommand } from '../../models/create-product-command';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
})
export class ProductFormComponent implements OnInit {
  form!: FormGroup;
  id!: number | null;

  // Legördülő adatok
  categories: ProductCategoryDto[] = [];
  vatRates: VatRateDto[] = [];
  productTypes: ProductTypeDto[] = [];

  // Betöltött entitás (edit módban)
  productToEdit!: ProductDto;

  isLoading = false;
  errorMessage: string | null = null;

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
    const idParam = this.route.snapshot.params['id'];
    this.id = idParam ? +idParam : null;

    this.initForm();
    this.loadSelectData();

    if (this.id) {
      this.isLoading = true;
      this.productService.findProductById(this.id).subscribe({
        next: (product) => {
          this.productToEdit = product;
          this.patchFormWithProductData(product);
          this.isLoading = false;
        },
        error: (err) => {
          console.error("Error fetching product for edit:", err);
          this.errorMessage = "Hiba történt a termék betöltése közben.";
          this.isLoading = false;
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

  private patchFormWithProductData(product: ProductDto): void {
    this.form.patchValue({
      name: product.name,
      sku: product.sku,
      description: product.description,
      productType: product.productType,
      category: product.category,
      manufacturer: product.manufacturer,
      netPurchasePrice: product.netPurchasePrice,
      grossPurchasePrice: product.grossPurchasePrice,
      netSellingPrice: product.netSellingPrice,
      grossSellingPrice: product.grossSellingPrice,
      warrantyPeriodMonths: product.warrantyPeriodMonths,
      serialNumberRequired: product.serialNumberRequired,
      stockQuantity: product.stockQuantity,
      unit: product.unit,
      vatRate: product.vatRate,
      deleted: product.deleted,
      barcodes: product.barcodes || [],
      serialNumbers: product.serialNumbers || []
    });
  }

  private loadSelectData(): void {
    this.productCategoryService.getProductCategories().subscribe((cats) => {
      this.categories = cats;
      if (this.id && this.productToEdit) { // Csak akkor próbáljon patchelni, ha van mit
        this.form.get('category')?.setValue(this.categories.find(c => c.id === this.productToEdit?.category.id) || null);
      }
    });
    this.vatRateService.getVatRates().subscribe((rates) => {
      this.vatRates = rates;
      if (this.id && this.productToEdit) {
        this.form.get('vatRate')?.setValue(this.vatRates.find(vr => vr.id === this.productToEdit?.vatRate.id) || null);
      }
    });
    this.productTypeService.getProductTypes().subscribe((types) => {
      this.productTypes = types;
      if (this.id && this.productToEdit) {
        this.form.get('productType')?.setValue(this.productTypes.find(pt => pt.id === this.productToEdit?.productType.id) || null);
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      console.warn('Form is invalid. Errors:', this.form.errors);
      // Kiírhatjuk az egyes mezők hibáit is
      Object.keys(this.form.controls).forEach(key => {
        const controlErrors = this.form.get(key)?.errors;
        if (controlErrors != null) {
          console.warn('Control \'' + key + '\' errors: ' + JSON.stringify(controlErrors));
        }
      });
      return;
    }
    this.isLoading = true;
    this.errorMessage = null;

    const formValues = this.form.getRawValue();

    // Közös payload rész
    const commonPayload = {
      name: formValues.name,
      sku: formValues.sku,
      description: formValues.description || '', // Biztosítjuk, hogy string legyen
      productTypeId: formValues.productType?.id,
      categoryId: formValues.category?.id,
      manufacturerId: formValues.manufacturer?.id,
      netSellingPrice: Number(formValues.netSellingPrice) || 0, // Konvertálás number-re és null/undefined -> 0
      grossSellingPrice: Number(formValues.grossSellingPrice) || 0, // Konvertálás number-re és null/undefined -> 0
      warrantyPeriodMonths: formValues.warrantyPeriodMonths === null || formValues.warrantyPeriodMonths === undefined ? 0 : Number(formValues.warrantyPeriodMonths),
      serialNumberRequired: formValues.serialNumberRequired,
      vatRateId: formValues.vatRate?.id,
      unit: formValues.unit,
      barcodes: formValues.barcodes || [],
      serialNumbers: formValues.serialNumbers || [],
    };

    if (this.id) {
      // Update művelet
      const updateCommand: UpdateProductCommand = {
        ...commonPayload,
        deleted: formValues.deleted, // Csak update-nél van
      };
      // Validáció, hogy a szükséges ID-k ne legyenek undefined
      if (updateCommand.productTypeId === undefined || updateCommand.categoryId === undefined || updateCommand.manufacturerId === undefined || updateCommand.vatRateId === undefined) {
        this.errorMessage = "Kérem válasszon terméktípust, kategóriát, gyártót és ÁFA kulcsot!";
        this.isLoading = false;
        return;
      }

      this.productService.updateProduct(this.id, updateCommand).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/master-data/products']);
        },
        error: (err) => {
          console.error('Error updating product:', err);
          this.errorMessage = err.error?.message || "Hiba történt a termék frissítése közben.";
          this.isLoading = false;
        },
      });
    } else {
      // Create művelet
      const createCommand: CreateProductCommand = {
        ...commonPayload
        // A 'deleted' mező itt nem kell
      };
      // Validáció, hogy a szükséges ID-k ne legyenek undefined
      if (createCommand.productTypeId === undefined || createCommand.categoryId === undefined || createCommand.manufacturerId === undefined || createCommand.vatRateId === undefined) {
        this.errorMessage = "Kérem válasszon terméktípust, kategóriát, gyártót és ÁFA kulcsot!";
        this.isLoading = false;
        return;
      }

      console.log(createCommand);

      this.productService.createProduct(createCommand).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/master-data/products']);
        },
        error: (err) => {
          console.error('Error creating product:', err);
          this.errorMessage = err.error?.message || "Hiba történt a termék létrehozása közben.";
          this.isLoading = false;
        },
      });
    }
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

    modalRef.componentInstance.initialserialNumbers = this.form.value.serialNumbers;

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
/*
{
  "name": "Intel Core i5-14400F 10-Core 2.5GHz LGA1700 Box",
  "sku": "BX8071514400F",
  "description": "",
  "productTypeId": 1,
  "categoryId": 2,
  "manufacturerId": 1,
  "netSellingPrice": 0,
  "grossSellingPrice": 0,
  "warrantyPeriodMonths": 36,
  "serialNumberRequired": false,
  "barcodes": [
    {
      "id": 0,
      "code": "5032037279147",
      "isGenerated": false
    }
  ],
  "serialNumbers": [],
  "unit": "db",
  "vatRateId": 1
}

*/