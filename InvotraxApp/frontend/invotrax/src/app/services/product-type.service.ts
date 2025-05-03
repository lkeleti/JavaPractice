import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductTypeDto } from '../models/productType.dto';
import { UpdateProductTypeCommand } from '../models/update-product-type.command';
import { CreateProductTypeCommand } from '../models/create-product-type-command';

@Injectable({
    providedIn: 'root',
})
export class ProductTypeService {
    private apiUrl = environment.apiUrl;
    constructor(private http: HttpClient) { }

    /**
     * Lekérdezi az összes termék típust a backend API-ról.
     * @returns Observable, ami VatRateDto tömböt fog kibocsátani.
     */
    getProductTypes(): Observable<ProductTypeDto[]> {
        return this.http.get<ProductTypeDto[]>(`${this.apiUrl}/product-types`);
    }

    findProductTypeById(id: number): Observable<ProductTypeDto> {
        return this.http.get<ProductTypeDto>(`${this.apiUrl}/product-types/${id}`);
    }

    updateProductType(id: number, command: UpdateProductTypeCommand): Observable<ProductTypeDto> {
        return this.http.put<ProductTypeDto>(`${this.apiUrl}/product-types/${id}`, command);
    }

    createProductType(command: CreateProductTypeCommand): Observable<ProductTypeDto> {
        return this.http.post<ProductTypeDto>(`${this.apiUrl}/product-types`, command);
    }

}
