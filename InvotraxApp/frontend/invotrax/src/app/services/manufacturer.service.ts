import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ManufacturerDto } from '../models/manufacturer.dto';
import { PaginatedManufacturersResponse } from '../models/paginated-response.dto'; // Import the new interface
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ManufacturerService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  /**
 * Lekérdezi az összes gyártót a backend API-ról.
 * @returns Observable, ami ManufacturerDto tömböt fog kibocsátani.
 */
  getManufacturers(
    page: number,       // Page number (ngx-pagination uses 1-based, Spring uses 0-based)
    size: number,       // Items per page
    searchTerm?: string, // Optional search term
    sort?: string        // Optional sort parameter (e.g., 'name,asc')
  ): Observable<PaginatedManufacturersResponse> {

    // Adjust page number for Spring Boot (0-based index)
    const zeroBasedPage = page - 1;

    let params = new HttpParams()
      .set('page', zeroBasedPage.toString())
      .set('size', size.toString());

    if (searchTerm) {
      params = params.set('searchTerm', searchTerm); // Use the param name expected by backend
    }
    if (sort) {
      params = params.set('sort', sort);
    }

    return this.http.get<PaginatedManufacturersResponse>(`${this.apiUrl}/manufacturers`, { params });
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
