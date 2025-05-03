import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductCategoryDto } from '../models/product-category.dto';
import { UpdateProductCategoryCommand } from '../models/update-product-category-command';
import { CreateProductCategoryCommand } from '../models/create-product_category-command';


@Injectable({
    providedIn: 'root',
})
export class ProductCategoryService {
    private apiUrl = environment.apiUrl;
    constructor(private http: HttpClient) { }

    /**
     * Lekérdezi az összes termék típust a backend API-ról.
     * @returns Observable, ami VatRateDto tömböt fog kibocsátani.
     */
    getProductCategories(): Observable<ProductCategoryDto[]> {
        return this.http.get<ProductCategoryDto[]>(`${this.apiUrl}/productcategories`);
    }

    findProductCategoryById(id: number): Observable<ProductCategoryDto> {
        return this.http.get<ProductCategoryDto>(`${this.apiUrl}/productcategories/${id}`);
    }

    updateProductCategory(id: number, command: UpdateProductCategoryCommand): Observable<ProductCategoryDto> {
        return this.http.put<ProductCategoryDto>(`${this.apiUrl}/productcategories/${id}`, command);
    }

    createProductCategory(command: CreateProductCategoryCommand): Observable<ProductCategoryDto> {
        return this.http.post<ProductCategoryDto>(`${this.apiUrl}/productcategories`, command);
    }

}
