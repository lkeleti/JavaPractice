import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { VatRateDto } from '../models/vat-rate.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class VatRateService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) {}

  /**
   * Lekérdezi az összes ÁFA kódot a backend API-ról.
   * @returns Observable, ami VatRateDto tömböt fog kibocsátani.
   */
  getVatRates(): Observable<VatRateDto[]> {
    return this.http.get<VatRateDto[]>(`${this.apiUrl}/vatrates`);
  }

  findVatRateById(id: number): Observable<VatRateDto> {
    return this.http.get<VatRateDto>(`${this.apiUrl}/vatrates/${id}`);
  }
  /*
        updateAuthor(id: number, command: UpdateAuthorCommand): Observable<VatRateDto> {
          return this.http.put<VatRateDto>(`${this.apiUrl}/authors/${id}`, command);
        }
      
        createAuthor(command: CreateAuthorCommand): Observable<VatRateDto> {
          return this.http.post<VatRateDto>(`${this.apiUrl}/authors`, command);
        }
      */

  deleteVatRate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/vatrates/${id}`);
  }

  unDeleteVatRate(id: number): Observable<VatRateDto> {
    return this.http.delete<VatRateDto>(
      `${this.apiUrl}/vatrates/undelete/${id}`
    );
  }
}
