import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { BookService } from '../../services/book.service';
import { AuthorDto } from '../../models/author.dto';
import { AuthorService } from '../../services/author.service';
import { CreateBookCommand } from '../../models/create-book.command';


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

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    private authorService: AuthorService,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.bookForm = this.fb.group({
      isbn: [null, [Validators.required, Validators.maxLength(20)]],
      title: [null, [Validators.required, Validators.maxLength(255)]],
      publicationYear: [null, [Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]], // Csak számok
      authorId: [null, [Validators.required]]
    });
    this.loadAuthors();
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

    const command: CreateBookCommand = this.bookForm.value;

    this.bookService.createBook(command)
      .subscribe({
        next: (createdBook) => {
          this.isLoading = false;
          console.log('Book created:', createdBook);
          this.router.navigate(['/books']);
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Error creating book:', err);
          this.errorMessage = err.error?.message || 'Failed to create book. Please try again.';
        }
      });

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