import { BarcodeDto } from "./barcode.dto";
import { SerialNumberDto } from "./serial-number.dto";

export interface CreateProductCommand {
    name: string;
    sku: string;
    description: string;
    productTypeId: number;
    categoryId: number;
    manufacturerId: number;
    netSellingPrice: number;
    grossSellingPrice: number;
    warrantyPeriodMonths: number;
    serialNumberRequired: boolean;
    vatRateId: number;
    unit: string;
    barcodes: BarcodeDto[];
    serialNumbers: SerialNumberDto[];
}