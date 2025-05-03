export interface UpdateInvoiceNumberSequenceCommand {
    id: number;
    invoiceTypeId: number;
    invoicePrefix: string;
    lastNumber: number;
}