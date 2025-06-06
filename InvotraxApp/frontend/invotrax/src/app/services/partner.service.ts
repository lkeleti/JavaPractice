import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PartnerDto } from '../models/partner.dto';
import { Observable } from 'rxjs';
import { PaginatedPartnersResponse } from '../models/paginated-response.dto';
import { UpdatePartnerCommand } from '../models/update-partner.command';
import { CreatePartnerCommand } from '../models/create-partner.command';

@Injectable({
    providedIn: 'root',
})
export class PartnerService {
    private apiUrl = environment.apiUrl;
    constructor(private http: HttpClient) { }

    /**
     * Lekérdezi az összes partnert a backend API-ról.
     * @returns Observable, ami InvoiceTypeDto tömböt fog kibocsátani.
     */
    getPartners(
        page: number,       // Page number (ngx-pagination uses 1-based, Spring uses 0-based)
        size: number,       // Items per page
        searchTerm?: string, // Optional search term
        sort?: string        // Optional sort parameter (e.g., 'name,asc')
    ): Observable<PaginatedPartnersResponse> {

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

        return this.http.get<PaginatedPartnersResponse>(`${this.apiUrl}/partners`, { params });
    }


    findPartnerById(id: number): Observable<PartnerDto> {
        return this.http.get<PartnerDto>(`${this.apiUrl}/partners/${id}`);
    }

    updatePartner(id: number, command: UpdatePartnerCommand): Observable<PartnerDto> {
        return this.http.put<PartnerDto>(`${this.apiUrl}/partners/${id}`, command);
    }

    createPartner(command: CreatePartnerCommand): Observable<PartnerDto> {
        return this.http.post<PartnerDto>(`${this.apiUrl}/partners`, command);
    }


    deletePartner(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/partners/${id}`);
    }

    unDeletePartner(id: number): Observable<PartnerDto> {
        return this.http.delete<PartnerDto>(
            `${this.apiUrl}/partners/undelete/${id}`
        );
    }
}
