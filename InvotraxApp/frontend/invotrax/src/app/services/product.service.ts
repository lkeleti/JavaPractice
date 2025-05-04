import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
// Definiálj egy Product interfészt/osztályt, ami tükrözi a backend DTO-t
import { ProductDto } from '../models/product.dto'; // Tegyük fel, hogy van ilyen
import { environment } from '../environments/environment.development';
import { PaginatedProductsResponse } from '../models/paginated-response.dto';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  /**
   * Lekérdezi az összes partnert a backend API-ról.
   * @returns Observable, ami InvoiceTypeDto tömböt fog kibocsátani.
   */
  getProducts(
    page: number,       // Page number (ngx-pagination uses 1-based, Spring uses 0-based)
    size: number,       // Items per page
    searchTerm?: string, // Optional search term
    sort?: string        // Optional sort parameter (e.g., 'name,asc')
  ): Observable<PaginatedProductsResponse> {

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

    return this.http.get<PaginatedProductsResponse>(`${this.apiUrl}/products`, { params });
  }


  findProductById(id: number): Observable<ProductDto> {
    return this.http.get<ProductDto>(`${this.apiUrl}/products/${id}`);
  }

  /*updatePartner(id: number, command: UpdatePartnerCommand): Observable<ProductDto> {
      return this.http.put<ProductDto>(`${this.apiUrl}/products/${id}`, command);
  }

  createPartner(command: CreatePartnerCommand): Observable<ProductDto> {
      return this.http.post<ProductDto>(`${this.apiUrl}/products`, command);
  }*/


  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${id}`);
  }

  unDeleteProduct(id: number): Observable<ProductDto> {
    return this.http.delete<ProductDto>(
      `${this.apiUrl}/products/undelete/${id}`
    );
  }
}
