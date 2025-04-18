import { Component, OnInit } from '@angular/core';
import { AuthorDto } from '../../models/author.dto';
import { AuthorService } from '../../services/author.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-author-list',
  templateUrl: './author-list.component.html',
  styleUrls: ['./author-list.component.scss']
})
export class AuthorListComponent implements OnInit {

  authors: AuthorDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private authorService: AuthorService,
    private modalService: NgbModal
  ) { }
  searchTerm: string = '';

  ngOnInit(): void {
    this.loadAuthors();
  }

  loadAuthors(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.authorService.getAuthors()
      .subscribe({
        next: (data) => {
          this.authors = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching authors:', err);
          this.errorMessage = 'Failed to load author. Please try again later.';
          this.isLoading = false;
        }
        // complete: () => { console.log('Author loading completed.'); } // Opcionális: lefut a next vagy error után
      });
  }

  openDeleteConfirmation(author: AuthorDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Confirm Deletion';
    modalRef.componentInstance.message = `Are you sure you want to delete author "${author.name}" (ID: ${author.id})? This action cannot be undone.`;
    modalRef.componentInstance.confirmText = 'Delete';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Deletion confirmed for author:', author.id);
          this.deleteAuthor(author.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Deletion dismissed:', reason);
      }
    );
  }

  deleteAuthor(id: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.authorService.deleteAuthor(id) // Feltételezve, hogy van ilyen metódus a service-ben
      .subscribe({
        next: () => {
          console.log('Author deleted successfully:', id);
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadAuthors();
        },
        error: (err) => {
          console.error('Error deleting author:', err);
          this.errorMessage = err.error?.message || 'Failed to delete author.';
          this.isLoading = false;
        }
      });
  }

  get filteredAuthors(): AuthorDto[] {
    if (!this.searchTerm) return this.authors;
    const term = this.searchTerm.toLowerCase();
    return this.authors.filter(
      (author) =>
        author.name.toLowerCase().includes(term) ||
        author.birthYear?.toString().toLowerCase().includes(term) ||
        author.nationality?.toLowerCase().includes(term)
    );
  }
}