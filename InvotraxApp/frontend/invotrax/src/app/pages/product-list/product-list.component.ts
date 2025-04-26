import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { ProductDto } from '../../models/product.dto';
import { CommonModule } from '@angular/common'; // NgFor, AsyncPipe stb.
import { RouterModule } from '@angular/router'; // RouterLink-hez
import { Observable } from 'rxjs';

@Component({
  selector: 'app-product-list',
  standalone: true, // Használjunk standalone komponenst (Angular 14+)
  imports: [CommonModule, RouterModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products$: Observable<ProductDto[]> | undefined; // Observable a szolgáltatásból

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.products$ = this.productService.getProducts();
  }

  deleteProduct(id: number): void {
    // TODO: Törlés implementálása (confirmation + service hívás)
    console.log('Delete product with id:', id);
  }
}