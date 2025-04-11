import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookDto } from '../models/book.dto';
import { CreateBookCommand } from '../models/create-book.command';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class BookService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Lekérdezi az összes könyvet a backend API-ról.
   * @returns Observable, ami BookDto tömböt fog kibocsátani.
   */
  getBooks(): Observable<BookDto[]> {
    return this.http.get<BookDto[]>(`${this.apiUrl}/books`);
  }

  createBook(command: CreateBookCommand): Observable<BookDto> {
    return this.http.post<BookDto>(`${this.apiUrl}/books`, command);
  }
  // --- Később ide jönnek a többi API hívások ---
  // getBookById(id: number): Observable<BookDto> { ... }
  // updateBook(id: number, command: UpdateBookCommand): Observable<BookDto> { ... }
  // deleteBook(id: number): Observable<void> { ... }
}