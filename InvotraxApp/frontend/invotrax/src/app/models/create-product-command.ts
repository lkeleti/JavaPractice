import Decimal from "decimal.js";

export interface CreateProductCommand {
    name: string;
    sku: string;
    description: string;
    productTypeId: number;
    categoryId: number;
    manufacturerId: number;
    netSellingPrice: Decimal;
    grossSellingPrice: Decimal;
    warrantyPeriodMonths: number;
    stockQuantity: number;
    serialNumberRequired: boolean;
    vatRateId: number;
    //Barcodes
    //SerialNumbers
}