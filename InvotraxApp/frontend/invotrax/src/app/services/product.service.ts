import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// Definiálj egy Product interfészt/osztályt, ami tükrözi a backend DTO-t
import { ProductDto } from '../models/product.dto'; // Tegyük fel, hogy van ilyen

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = '/api/products'; // Backend API végpont (proxyval vagy teljes URL-lel)

  constructor(private http: HttpClient) { }

  getProducts(): Observable<ProductDto[]> {
    return this.http.get<ProductDto[]>(this.apiUrl);
  }

  // ... CRUD műveletek (getProductById, createProduct, updateProduct, deleteProduct) ...
}