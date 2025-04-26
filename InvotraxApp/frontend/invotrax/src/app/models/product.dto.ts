import { Decimal } from 'decimal.js';
import { ProductTypeDto } from './productType.dto';

export interface ProductDto {
    id: number;
    name: string;
    sku: string;
    productType: ProductTypeDto;
    stockQuantity?: number;
    netSellingPrice: Decimal;
}