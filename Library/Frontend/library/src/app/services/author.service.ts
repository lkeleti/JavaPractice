import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthorDto } from '../models/author.dto';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class AuthorService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Lekérdezi az összes authort a backend API-ról.
   * @returns Observable, ami AuthorDto tömböt fog kibocsátani.
   */
  getAuthors(): Observable<AuthorDto[]> {
    return this.http.get<AuthorDto[]>(`${this.apiUrl}/authors`);
  }  
}