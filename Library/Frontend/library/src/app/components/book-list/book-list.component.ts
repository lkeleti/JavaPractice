import { Component, OnInit } from '@angular/core';
import { BookDto } from '../../models/book.dto';
import { BookService } from '../../services/book.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {
  books: BookDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private bookService: BookService,
    private modalService: NgbModal
  ) { }

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

  openDeleteConfirmation(book: BookDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Confirm Deletion';
    modalRef.componentInstance.message = `Are you sure you want to delete book "${book.title}" (ID: ${book.id})? This action cannot be undone.`;
    modalRef.componentInstance.confirmText = 'Delete';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for book:', book.id);
          this.deleteBook(book.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deleteBook(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.bookService.deleteBook(id) // Feltételezve, hogy van ilyen metódus a service-ben
      .subscribe({
        next: () => {
          console.log('Book deleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadBooks();
        },
        error: (err) => {
          console.error('Error deleting book:', err);
          this.errorMessage = err.error?.message || 'Failed to delete book.';
          this.isLoading = false;
        }
      });
  }
}
