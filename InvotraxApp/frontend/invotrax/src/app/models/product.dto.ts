import { Decimal } from 'decimal.js';
import { ProductTypeDto } from './productType.dto';
import { ProductCategoryDto } from './product-category.dto';
import { ManufacturerDto } from './manufacturer.dto';
import { SerialNumberDto } from './serial-number.dto';
import { BarcodeDto } from './barcode.dto';
import { VatRateDto } from './vat-rate.dto';

export interface ProductDto {
    id: number;
    name: string;
    sku: string;
    description: string;
    productType: ProductTypeDto;
    category: ProductCategoryDto;
    manufacturer: ManufacturerDto;
    netPurchasePrice: Decimal;
    grossPurchasePrice: Decimal;
    netSellingPrice: Decimal;
    grossSellingPrice: Decimal;
    warrantyPeriodMonths: number;
    serialNumberRequired: boolean;
    stockQuantity: number;
    barcodes: BarcodeDto[];
    serialNumbers: SerialNumberDto[]
    vatRate: VatRateDto;
    deleted: boolean;
}