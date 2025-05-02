import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { PaymentMethodDto } from '../models/paymentMethod.dto';
import { Observable } from 'rxjs';
import { UpdatePaymentMethodCommand } from '../models/update-payment-method.command';
import { CreatePaymentMethodCommand } from '../models/create-payment-method.command';

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

  updatePaymentMethod(id: number, command: UpdatePaymentMethodCommand): Observable<PaymentMethodDto> {
    return this.http.put<PaymentMethodDto>(`${this.apiUrl}/paymentmethods/${id}`, command);
  }

  createPaymentMethod(command: CreatePaymentMethodCommand): Observable<PaymentMethodDto> {
    return this.http.post<PaymentMethodDto>(`${this.apiUrl}/paymentmethods`, command);
  }


  deletePaymentMethod(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/paymentmethods/${id}`);
  }

  unDeletePaymentMethod(id: number): Observable<PaymentMethodDto> {
    return this.http.delete<PaymentMethodDto>(
      `${this.apiUrl}/paymentmethods/undelete/${id}`
    );
  }

}
