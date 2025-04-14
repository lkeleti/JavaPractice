import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthorService } from '../../services/author.service';
import { CreateAuthorCommand } from '../../models/create-author.command';
import { UpdateAuthorCommand } from 'src/app/models/update-author.command';
import { AuthorDto } from 'src/app/models/author.dto';

@Component({
  selector: 'app-author-form',
  templateUrl: './author-form.component.html',
  styleUrls: ['./author-form.component.scss']
})
export class AuthorFormComponent implements OnInit {

  authorForm!: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;
  isEditMode = false;
  authorId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private authorService: AuthorService,
    public router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.authorForm = this.fb.group({
      name: [null, [Validators.required, Validators.maxLength(255)]],
      birthYear: [null, [Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]], // Csak számok      
      nationality: [null, [Validators.required, Validators.maxLength(255)]]
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.authorId = +idParam; // '+' jellel számmá alakítjuk a stringet
      this.loadAuthorDataForEdit(); // Metódus az adatok betöltésére
    } else {
      // Ha nincs ID, akkor Create módban vagyunk
      this.isEditMode = false;
      this.authorId = null;
    }

  }
  loadAuthorDataForEdit(): void {
    if (this.authorId === null) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.authorService.findAuthorById(this.authorId)
      .subscribe({
        next: (authorData: AuthorDto) => {
          // Az űrlap feltöltése a kapott adatokkal
          // Figyelem: A patchValue csak azokat a mezőket tölti ki, amik léteznek
          // a DTO-ban ÉS a FormGroup-ban is (name, birthYear, nationality).
          this.authorForm.patchValue(authorData);
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching author for edit:', err);
          this.errorMessage = err.error?.message || 'Failed to load author data for editing.';
          this.isLoading = false;
          // Opcionális: Hiba esetén visszanavigálhatnánk a listára
          this.router.navigate(['/authors']);
        }
      });
  }

  // Getterek a könnyebb hivatkozáshoz a template-ben (opcionális, de hasznos)
  get name(): AbstractControl | null { return this.authorForm.get('name'); }
  get birthYear(): AbstractControl | null { return this.authorForm.get('birthYear'); }
  get nationality(): AbstractControl | null { return this.authorForm.get('nationality'); }

  onSubmit(): void {
    if (this.authorForm.invalid) {
      this.authorForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const commandData = this.authorForm.value;

    if (this.isEditMode && this.authorId !== null) {
      // ----- UPDATE -----
      this.authorService.updateAuthor(this.authorId, commandData as UpdateAuthorCommand)
        .subscribe({
          next: (updatedAuthor) => {
            this.isLoading = false;
            console.log('Author updated:', updatedAuthor);
            this.router.navigate(['/authors']); // Visszanavigálás a listára
          },
          error: (err) => {
            this.isLoading = false;
            console.error('Error updating author:', err);
            this.errorMessage = err.error?.message || 'Failed to update author. Please try again.';
          }
        });
    } else {
      // ----- CREATE -----
      this.authorService.createAuthor(commandData as CreateAuthorCommand)
        .subscribe({
          next: (createdAuthor) => {
            this.isLoading = false;
            console.log('Author created:', createdAuthor);
            this.router.navigate(['/authors']); // Visszanavigálás a listára
          },
          error: (err) => {
            this.isLoading = false;
            console.error('Error creating author:', err);
            this.errorMessage = err.error?.message || 'Failed to create author. Please try again.';
          }
        });
    }
  }
}