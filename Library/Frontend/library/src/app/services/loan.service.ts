import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoanDto } from '../models/loan.dto';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Lekérdezi az összes kölcsönzést a backend API-ról.
   * @returns Observable, ami LoanDto tömböt fog kibocsátani.
   */
  getLoans(): Observable<LoanDto[]> {
    return this.http.get<LoanDto[]>(`${this.apiUrl}/loans`);
  }  
}