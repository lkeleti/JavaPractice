import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookDto } from '../models/book.dto';
import { CreateBookCommand } from '../models/create-book.command';
import { environment } from '../../environments/environment';
import { UpdateBookCommand } from '../models/update-book.command';


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

  findBookById(id: number): Observable<BookDto> {
    return this.http.get<BookDto>(`${this.apiUrl}/books/${id}`);
  }

  createBook(command: CreateBookCommand): Observable<BookDto> {
    return this.http.post<BookDto>(`${this.apiUrl}/books`, command);
  }

  updateBook(id: number, command: UpdateBookCommand): Observable<BookDto> {
    return this.http.put<BookDto>(`${this.apiUrl}/books/${id}`, command);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/books/${id}`);
  }
}