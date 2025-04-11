import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthorService } from '../../services/author.service';
import { CreateAuthorCommand } from '../../models/create-author.command';

@Component({
  selector: 'app-author-form',
  templateUrl: './author-form.component.html',
  styleUrls: ['./author-form.component.scss']
})
export class AuthorFormComponent implements OnInit {

  authorForm!: FormGroup; 
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authorService: AuthorService,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.authorForm = this.fb.group({
      name: [null, [Validators.required, Validators.maxLength(255)]],
      birthYear: [null, [Validators.required, Validators.min(1), Validators.pattern("^[0-9]*$")]], // Csak számok      
      nationality: [null, [Validators.required, Validators.maxLength(255)]]
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

    const command: CreateAuthorCommand = this.authorForm.value;

    this.authorService.createAuthor(command)
      .subscribe({
        next: (createdAuthor) => {
          this.isLoading = false;
          console.log('Author created:', createdAuthor);
          this.router.navigate(['/authors']);
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Error creating author:', err);
          this.errorMessage = err.error?.message || 'Failed to create author. Please try again.';
        }
      });
  }
}