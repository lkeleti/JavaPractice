import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { PaymentMethodDto } from '../models/paymentMethod.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentMethodService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  /**
  * Lekérdezi az összes fizetési módot a backend API-ról.
  * @returns Observable, ami PaymentMethodDto tömböt fog kibocsátani.
  */
  getPaymentMethods(): Observable<PaymentMethodDto[]> {
    return this.http.get<PaymentMethodDto[]>(`${this.apiUrl}/paymentmethods`);
  }

  findPaymentMethodById(id: number): Observable<PaymentMethodDto> {
    return this.http.get<PaymentMethodDto>(`${this.apiUrl}/paymentmethods/${id}`);
  }
  /*
    updateAuthor(id: number, command: UpdateAuthorCommand): Observable<ManufacturerDto> {
      return this.http.put<ManufacturerDto>(`${this.apiUrl}/authors/${id}`, command);
    }
  
    createAuthor(command: CreateAuthorCommand): Observable<ManufacturerDto> {
      return this.http.post<ManufacturerDto>(`${this.apiUrl}/authors`, command);
    }
  */

}
