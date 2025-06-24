import { InvoiceTypeListComponent } from './pages/invoice-type-list/invoice-type-list.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InvoicingComponent } from './pages/invoicing/invoicing.component';
import { ProductListComponent } from './pages/product-list/product-list.component';
import { PartnerListComponent } from './pages/partner-list/partner-list.component';
import { QueryPageComponent } from './pages/query-page/query-page.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { ManufacturerListComponent } from './pages/manufacturer-list/manufacturer-list.component';
import { ManufacturerFormComponent } from './pages/manufacturer-form/manufacturer-form.component';
import { PaymentMethodListComponent } from './pages/payment-method-list/payment-method-list.component';
import { ZipCodeListComponent } from './pages/zip-code-list/zip-code-list.component';
import { ZipCodeFormComponent } from './pages/zip-code-form/zip-code-form.component';
import { InvoiceTypeFormComponent } from './pages/invoice-type-form/invoice-type-form.component';
import { VatRateListComponent } from './pages/vat-rate-list/vat-rate-list.component';
import { VatRateFormComponent } from './pages/vat-rate-form/vat-rate-form.component';
import { PartnerFormComponent } from './pages/partner-form/partner-form.component';
import { PaymentMethodFormComponent } from './pages/payment-method-form/payment-method-form.component';
import { ProductTypeFormComponent } from './pages/product-type-form/product-type-form.component';
import { ProductTypeListComponent } from './pages/product-type-list/product-type-list.component';
import { ProductCategoryListComponent } from './pages/product-category-list/product-category-list.component';
import { ProductCategoryFormComponent } from './pages/product-category-form/product-category-form.component';
import { InvoiceNumberSequenceListComponent } from './pages/invoice-number-sequence-list/invoice-number-sequence-list.component';
import { InvoiceNumberSequenceFormComponent } from './pages/invoice-number-sequence-form/invoice-number-sequence-form.component';
import { ProductFormComponent } from './pages/product-form/product-form.component';
import { InventoryListComponent } from './pages/inventory-list/inventory-list.component';
import { InventoryFormComponent } from './pages/inventory-form/inventory-form.component';

export const routes: Routes = [
  // Alapértelmezett átirányítás a számlázásra
  { path: '', redirectTo: '/invoicing', pathMatch: 'full' },
  {
    path: 'invoicing',
    component: InvoicingComponent,
    title: 'Számlázás - InvotraxApp',
  }, // title hozzáadása jó gyakorlat
  {
    path: 'inventories',
    component: InventoryListComponent,
    title: 'Bevételezések',
  },
  {
    path: 'inventories/new',
    component: InventoryFormComponent,
    title: 'Új Bevételezés',
  },
  {
    path: 'inventories/edit/:id',
    component: InventoryFormComponent,
    title: 'Bevételezés Szerkesztése',
  }, // :id paraméter
  {
    path: 'master-data',
    title: 'Törzsadatok',
    children: [
      { path: '', redirectTo: 'products', pathMatch: 'full' }, // Alapértelmezett törzsadat
      { path: 'products', component: ProductListComponent, title: 'Termékek' },
      { path: 'products/new', component: ProductFormComponent, title: 'Új termékek' },
      { path: 'products/edit/:id', component: ProductFormComponent, title: 'Termék szerkesztése' },
      { path: 'partners', component: PartnerListComponent, title: 'Partnerek' },
      { path: 'partners/new', component: PartnerFormComponent, title: 'Új partnerek' },
      { path: 'partners/edit/:id', component: PartnerFormComponent, title: 'Partner szerkesztése' },
      { path: 'seller-profile', component: PartnerFormComponent, data: { mode: 'seller' }, title: 'Eladó adatai' },
      { path: 'manufacturers', component: ManufacturerListComponent, title: 'Gyártók' },
      { path: 'manufacturers/new', component: ManufacturerFormComponent, title: 'Új gyártó' },
      { path: 'manufacturers/edit/:id', component: ManufacturerFormComponent, title: 'Gyártó szerkesztése' },
      { path: 'zip-codes', component: ZipCodeListComponent, title: 'Irányítószámok' },
      { path: 'zip-codes/new', component: ZipCodeFormComponent, title: 'Új irányítószám' },
      { path: 'zip-codes/edit/:id', component: ZipCodeFormComponent, title: 'Irányítószám szerkesztése' },
      { path: 'invoice-types', component: InvoiceTypeListComponent, title: 'Számla típusok' },
      { path: 'invoice-types/new', component: InvoiceTypeFormComponent, title: 'Új számlatípus' },
      { path: 'invoice-types/edit/:id', component: InvoiceTypeFormComponent, title: 'Számlatípus szerkesztése' },
      { path: 'vat-rates', component: VatRateListComponent, title: 'Áfa kódok' },
      { path: 'vat-rates/new', component: VatRateFormComponent, title: 'Új ÁFA kulcs' },
      { path: 'vat-rates/edit/:id', component: VatRateFormComponent, title: 'ÁFA kulcs szerkesztése' },
      { path: 'payment-methods', component: PaymentMethodListComponent, title: 'Fizetési módok' },
      { path: 'payment-methods/new', component: PaymentMethodFormComponent, title: 'Új fizetési mód' },
      { path: 'payment-methods/edit/:id', component: PaymentMethodFormComponent, title: 'Fizetési mód szerkesztése' },
      { path: 'product-types', component: ProductTypeListComponent, title: 'Terméktípusok' },
      { path: 'product-types/new', component: ProductTypeFormComponent, title: 'Új terméktípus' },
      { path: 'product-types/edit/:id', component: ProductTypeFormComponent, title: 'Terméktípus szerkesztése' },
      { path: 'product-categories', component: ProductCategoryListComponent, title: 'Termék kategóriák' },
      { path: 'product-categories/new', component: ProductCategoryFormComponent, title: 'Új termék kategória' },
      { path: 'product-categories/edit/:id', component: ProductCategoryFormComponent, title: 'Termék kategória szerkesztése' },
      { path: 'invoice-number-sequences', component: InvoiceNumberSequenceListComponent, title: 'Számla sorszámok kategóriák' },
      { path: 'invoice-number-sequences/new', component: InvoiceNumberSequenceFormComponent, title: 'Új számla sorszám' },
      { path: 'invoice-number-sequences/edit/:id', component: InvoiceNumberSequenceFormComponent, title: 'Számla sorszám szerkesztése' },
      //{ path: 'payment-methods/new', component: PaymentMethodFormComponent, title: 'Új gyártó' },
      // ... további törzsadat route-ok (categories, manufacturers, stb.)
    ],
  },
  { path: 'queries', component: QueryPageComponent, title: 'Lekérdezések' },
  // Hibakezelés - Mindig a végén legyen
  { path: '**', component: NotFoundComponent, title: 'Oldal nem található' }, // Wildcard route 404-re
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
