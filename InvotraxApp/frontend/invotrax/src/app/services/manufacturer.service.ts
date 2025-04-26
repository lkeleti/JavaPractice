import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { ManufacturerDto } from '../models/manufacturer.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManufacturerService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  /**
 * Lekérdezi az összes authort a backend API-ról.
 * @returns Observable, ami AuthorDto tömböt fog kibocsátani.
 */
  getManufacturers(): Observable<ManufacturerDto[]> {
    return this.http.get<ManufacturerDto[]>(`${this.apiUrl}/manufacturers`);
  }

  findManufacturerById(id: number): Observable<ManufacturerDto> {
    return this.http.get<ManufacturerDto>(`${this.apiUrl}/manufacturers/${id}`);
  }
  /*
    updateAuthor(id: number, command: UpdateAuthorCommand): Observable<ManufacturerDto> {
      return this.http.put<ManufacturerDto>(`${this.apiUrl}/authors/${id}`, command);
    }
  
    createAuthor(command: CreateAuthorCommand): Observable<ManufacturerDto> {
      return this.http.post<ManufacturerDto>(`${this.apiUrl}/authors`, command);
    }
  */
  deleteManufacturer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/manufacturers/${id}`);
  }
}
