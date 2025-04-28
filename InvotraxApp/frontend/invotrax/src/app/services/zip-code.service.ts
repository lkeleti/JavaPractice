import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { ZipCodeDto } from '../models/zip-code.dto';
import { PaginatedZipCodesResponse } from '../models/paginated-response.dto';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ZipCodeService {
    private apiUrl = environment.apiUrl;
    constructor(private http: HttpClient) { }

    /**
   * Lekérdezi az összes irányítószámot a backend API-ról.
   * @returns Observable, ami ZipCodeDto tömböt fog kibocsátani.
   */
    getZipCodes(
        page: number,       // Page number (ngx-pagination uses 1-based, Spring uses 0-based)
        size: number,       // Items per page
        searchTerm?: string, // Optional search term
        sort?: string        // Optional sort parameter (e.g., 'name,asc')
    ): Observable<PaginatedZipCodesResponse> {

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

        return this.http.get<PaginatedZipCodesResponse>(`${this.apiUrl}/zipcodes`, { params });
    }

    findZipCodeById(id: number): Observable<ZipCodeDto> {
        return this.http.get<ZipCodeDto>(`${this.apiUrl}/zipcodes/${id}`);
    }
    /*
      updateZipCpde(id: number, command: UpdateZipCodeCommand): Observable<ZipCodeDto> {
        return this.http.put<ZipCodeDto>(`${this.apiUrl}/zipcodes/${id}`, command);
      }
    
      createZipCode(command: CreateZipCodeCommand): Observable<ZipCodeDto> {
        return this.http.post<ZipCodeDto>(`${this.apiUrl}/zipcodes`, command);
      }
    */
    deleteZipCode(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/zipcodes/${id}`);
    }

    unDeleteZipCode(id: number): Observable<ZipCodeDto> {
        return this.http.delete<ZipCodeDto>(`${this.apiUrl}/zipcodes/undelete/${id}`);
    }
}
