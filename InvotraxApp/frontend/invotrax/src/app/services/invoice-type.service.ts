import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { InvoiceTypeDto } from '../models/invoice-type.dto';
import { Observable } from 'rxjs';
import { UpdateInvoiceTypeCommand } from '../models/update-invoice-type-command';
import { CreateInvoiceTypeCommand } from '../models/create-invoice-type-command';

@Injectable({
  providedIn: 'root',
})
export class InvoiceTypeService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  /**
   * Lekérdezi az összes számla típust a backend API-ról.
   * @returns Observable, ami InvoiceTypeDto tömböt fog kibocsátani.
   */
  getInvoiceTypes(): Observable<InvoiceTypeDto[]> {
    return this.http.get<InvoiceTypeDto[]>(`${this.apiUrl}/invoice_types`);
  }

  findInvoiceTypeById(id: number): Observable<InvoiceTypeDto> {
    return this.http.get<InvoiceTypeDto>(`${this.apiUrl}/invoice_types/${id}`);
  }

  updateInvoiceType(id: number, command: UpdateInvoiceTypeCommand): Observable<InvoiceTypeDto> {
    return this.http.put<InvoiceTypeDto>(`${this.apiUrl}/invoice_types/${id}`, command);
  }

  createInvoiceType(command: CreateInvoiceTypeCommand): Observable<InvoiceTypeDto> {
    return this.http.post<InvoiceTypeDto>(`${this.apiUrl}/invoice_types`, command);
  }


  deleteInvoiceType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/invoice_types/${id}`);
  }

  unDeleteInvoiceType(id: number): Observable<InvoiceTypeDto> {
    return this.http.delete<InvoiceTypeDto>(
      `${this.apiUrl}/invoice_types/undelete/${id}`
    );
  }
}
