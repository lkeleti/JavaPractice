import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { BookService } from '../../services/book.service';
import { AuthorDto } from '../../models/author.dto';
import { AuthorService } from '../../services/author.service';
import { CreateBookCommand } from '../../models/create-book.command';
import { UpdateBookCommand } from 'src/app/models/update-book.command';
import { BookDto } from 'src/app/models/book.dto';


@Component({
  selector: 'app-book-form',
  templateUrl: './book-form.component.html',
  styleUrls: ['./book-form.component.scss']
})
export class BookFormComponent implements OnInit {
  bookForm!: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;
  allAuthors: AuthorDto[] = [];
  isEditMode = false;
  bookId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    private authorService: AuthorService,
    public router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.bookForm = this.fb.group({
      isbn: [null, [Validators.required, Validators.maxLength(20)]],
      title: [null, [Validators.required, Validators.maxLength(255)]],
      publicationYear: [null, [Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]], // Csak számok
      authorId: [null, [Validators.required]]
    });
    this.loadAuthors();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.bookId = +idParam; // '+' jellel számmá alakítjuk a stringet
      this.loadBookDataForEdit(); // Metódus az adatok betöltésére
    } else {
      // Ha nincs ID, akkor Create módban vagyunk
      this.isEditMode = false;
      this.bookId = null;
    }
  }
  loadBookDataForEdit(): void {
    if (this.bookId === null) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.bookService.findBookById(this.bookId)
      .subscribe({
        next: (bookData: BookDto) => {
          // Az űrlap feltöltése a kapott adatokkal
          // Figyelem: A patchValue csak azokat a mezőket tölti ki, amik léteznek
          // a DTO-ban ÉS a FormGroup-ban is (isbn, title, publicationYear).
          this.bookForm.patchValue({
            isbn: bookData.isbn,
            title: bookData.title,
            publicationYear: bookData.publicationYear
          });
          if (bookData.author && bookData.author.id) {
            this.bookForm.get('authorId')?.setValue(bookData.author.id);
          }
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching book for edit:', err);
          this.errorMessage = err.error?.message || 'Failed to load book data for editing.';
          this.isLoading = false;
          // Opcionális: Hiba esetén visszanavigálhatnánk a listára
          this.router.navigate(['/books']);
        }
      });
  }



  // Getterek a könnyebb hivatkozáshoz a template-ben (opcionális, de hasznos)
  get isbn(): AbstractControl | null { return this.bookForm.get('isbn'); }
  get title(): AbstractControl | null { return this.bookForm.get('title'); }
  get publicationYear(): AbstractControl | null { return this.bookForm.get('publicationYear'); }
  get authorIdControl(): AbstractControl | null { return this.bookForm.get('authorId'); }


  onSubmit(): void {
    if (this.bookForm.invalid) {
      this.bookForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const commandData = this.bookForm.value;

    if (this.isEditMode && this.bookId !== null) {
      // ----- UPDATE -----
      this.bookService.updateBook(this.bookId, commandData as UpdateBookCommand)
        .subscribe({
          next: (updatedbook) => {
            this.isLoading = false;
            console.log('Book updated:', updatedbook);
            this.router.navigate(['/books']); // Visszanavigálás a listára
          },
          error: (err) => {
            this.isLoading = false;
            console.error('Error updating book:', err);
            this.errorMessage = err.error?.message || 'Failed to update book. Please try again.';
          }
        });
    } else {
      // ----- CREATE -----
      this.bookService.createBook(commandData as CreateBookCommand)
        .subscribe({
          next: (createdBook) => {
            this.isLoading = false;
            console.log('Book created:', createdBook);
            this.router.navigate(['/books']); // Visszanavigálás a listára
          },
          error: (err) => {
            this.isLoading = false;
            console.error('Error creating book:', err);
            this.errorMessage = err.error?.message || 'Failed to create book. Please try again.';
          }
        });
    }
  }

  loadAuthors(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.authorService.getAuthors()
      .subscribe({
        next: (data) => {
          this.allAuthors = data;
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
}