import { Component, OnInit } from '@angular/core';
import { BookDto } from '../../models/book.dto';
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {
  books: BookDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private bookService: BookService) { }

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.bookService.getBooks()
      .subscribe({
        next: (data) => { 
          this.books = data;
          this.isLoading = false;
        },
        error: (err) => { 
          console.error('Error fetching books:', err);
          this.errorMessage = 'Failed to load books. Please try again later.';
          this.isLoading = false;
        }
        // complete: () => { console.log('Book loading completed.'); } // Opcionális: lefut a next vagy error után
      });
  }
}
