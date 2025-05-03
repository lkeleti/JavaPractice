import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InvoiceNumberSequenceDto } from '../models/invoice-number-sequence-dto';
import { UpdateInvoiceNumberSequenceCommand } from '../models/update-invoice-number-sequence-command';
import { CreateInvoiceNumberSequenceCommand } from '../models/create-invoice-number-sequence-command';

@Injectable({
    providedIn: 'root',
})
export class InvoiceNumberSequenceService {
    private apiUrl = environment.apiUrl;
    constructor(private http: HttpClient) { }

    /**
     * Lekérdezi az összes számla típust a backend API-ról.
     * @returns Observable, ami InvoiceTypeDto tömböt fog kibocsátani.
     */
    getInvoiceNumberSequence(): Observable<InvoiceNumberSequenceDto[]> {
        return this.http.get<InvoiceNumberSequenceDto[]>(`${this.apiUrl}/invoice-number-sequences`);
    }

    findInvoiceNumberSequenceById(id: number): Observable<InvoiceNumberSequenceDto> {
        return this.http.get<InvoiceNumberSequenceDto>(`${this.apiUrl}/invoice-number-sequences/${id}`);
    }

    updateInvoiceNumberSequence(id: number, command: UpdateInvoiceNumberSequenceCommand): Observable<InvoiceNumberSequenceDto> {
        return this.http.put<InvoiceNumberSequenceDto>(`${this.apiUrl}/invoice-number-sequences/${id}`, command);
    }

    createInvoiceNumberSequence(command: CreateInvoiceNumberSequenceCommand): Observable<InvoiceNumberSequenceDto> {
        return this.http.post<InvoiceNumberSequenceDto>(`${this.apiUrl}/invoice-number-sequences`, command);
    }
}
