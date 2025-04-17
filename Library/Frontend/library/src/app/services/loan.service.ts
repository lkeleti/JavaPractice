import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoanDto } from '../models/loan.dto';
import { environment } from '../../environments/environment';
import { CheckoutBookCommand } from '../models/checkout-book.command';

@Injectable({
  providedIn: 'root',
})
export class LoanService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Lekérdezi az összes kölcsönzést a backend API-ról.
   * @returns Observable, ami LoanDto tömböt fog kibocsátani.
   */
  getLoans(): Observable<LoanDto[]> {
    return this.http.get<LoanDto[]>(`${this.apiUrl}/loans`);
  }

  getActiveLoans(): Observable<LoanDto[]> {
    return this.http.get<LoanDto[]>(`${this.apiUrl}/loans/active`);
  }

  getOverdueLoans(): Observable<LoanDto[]> {
    return this.http.get<LoanDto[]>(`${this.apiUrl}/loans/overdue`);
  }

  checkoutBook(command: CheckoutBookCommand): Observable<LoanDto> {
    return this.http.post<LoanDto>(`${this.apiUrl}/loans`, command);
  }

  returnBook(bookId: number): Observable<LoanDto> {
    return this.http.put<LoanDto>(
      `${this.apiUrl}/loans/book/${bookId}/return`,
      null
    );
  }

  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/loans/${id}`);
  }
}
