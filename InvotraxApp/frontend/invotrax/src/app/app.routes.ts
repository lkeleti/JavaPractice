import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InvoicingComponent } from './pages/invoicing/invoicing.component';
import { InventoryReceiptListComponent } from './pages/inventory-receipt-list/inventory-receipt-list.component';
import { InventoryReceiptFormComponent } from './pages/inventory-receipt-form/inventory-receipt-form.component';
import { ProductListComponent } from './pages/product-list/product-list.component';
import { PartnerListComponent } from './pages/partner-list/partner-list.component';
import { QueryPageComponent } from './pages/query-page/query-page.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { ManufacturerListComponent } from './pages/manufacturer-list/manufacturer-list.component';
import { ManufacturerFormComponent } from './pages/manufacturer-form/manufacturer-form.component';
import { PaymentMethodListComponent } from './pages/payment-method-list/payment-method-list.component';
import { ZipCodeListComponent } from './pages/zip-code-list/zip-code-list.component';
import { ZipCodeFormComponent } from './pages/zip-code-form/zip-code-form.component';

export const routes: Routes = [
    // Alapértelmezett átirányítás a számlázásra
    { path: '', redirectTo: '/invoicing', pathMatch: 'full' },
    { path: 'invoicing', component: InvoicingComponent, title: 'Számlázás - InvotraxApp' }, // title hozzáadása jó gyakorlat
    { path: 'inventory-receipts', component: InventoryReceiptListComponent, title: 'Bevételezések' },
    { path: 'inventory-receipts/new', component: InventoryReceiptFormComponent, title: 'Új Bevételezés' },
    { path: 'inventory-receipts/edit/:id', component: InventoryReceiptFormComponent, title: 'Bevételezés Szerkesztése' }, // :id paraméter
    {
        path: 'master-data',
        title: 'Törzsadatok',
        children: [
            { path: '', redirectTo: 'products', pathMatch: 'full' }, // Alapértelmezett törzsadat
            { path: 'products', component: ProductListComponent, title: 'Termékek' },
            { path: 'partners', component: PartnerListComponent, title: 'Partnerek' },
            { path: 'manufacturers', component: ManufacturerListComponent, title: 'Gyártók' },
            { path: 'manufacturers/new', component: ManufacturerFormComponent, title: 'Új gyártó' },
            { path: 'zip-codes', component: ZipCodeListComponent, title: 'Irányítószámok' },
            { path: 'zip-codes/new', component: ZipCodeFormComponent, title: 'Új irányítószám' },
            { path: 'payment-methods', component: PaymentMethodListComponent, title: 'Gyártók' },
            //{ path: 'payment-methods/new', component: PaymentMethodFormComponent, title: 'Új gyártó' },
            // ... további törzsadat route-ok (categories, manufacturers, stb.)
        ]
    },
    { path: 'queries', component: QueryPageComponent, title: 'Lekérdezések' },
    // Hibakezelés - Mindig a végén legyen
    { path: '**', component: NotFoundComponent, title: 'Oldal nem található' } // Wildcard route 404-re
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }