import { InvoiceTypeDto } from "./invoice-type.dto";

export interface InvoiceNumberSequenceDto {
    id: number;
    invoiceType: InvoiceTypeDto
    invoicePrefix: string;
    lastNumber: number;
}