import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { InvoiceTypeDto } from '../models/invoice-type.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InvoiceTypeService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) {}

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
  /*
    updateAuthor(id: number, command: UpdateAuthorCommand): Observable<InvoiceTypeDto> {
      return this.http.put<InvoiceTypeDto>(`${this.apiUrl}/authors/${id}`, command);
    }
  
    createAuthor(command: CreateAuthorCommand): Observable<InvoiceTypeDto> {
      return this.http.post<InvoiceTypeDto>(`${this.apiUrl}/authors`, command);
    }
  */

  deleteInvoiceType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/invoice_types/${id}`);
  }

  unDeleteInvoiceType(id: number): Observable<InvoiceTypeDto> {
    return this.http.delete<InvoiceTypeDto>(
      `${this.apiUrl}/invoice_types/undelete/${id}`
    );
  }
}
