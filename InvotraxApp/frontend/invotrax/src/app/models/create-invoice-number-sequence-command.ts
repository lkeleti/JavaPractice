export interface CreateInvoiceNumberSequenceCommand {
    id: number;
    invoiceTypeId: number;
    invoicePrefix: string;
    lastNumber: number;
}