import { BarcodeDto } from "./barcode.dto";
import { SerialNumberDto } from "./serial-number.dto";

export interface UpdateProductCommand {
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
    deleted: boolean;
    barcodes: BarcodeDto[];
    serialNumbers: SerialNumberDto[];

}