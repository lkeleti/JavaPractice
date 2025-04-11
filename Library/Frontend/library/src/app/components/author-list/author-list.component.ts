import { Component, OnInit } from '@angular/core';
import { AuthorDto } from '../../models/author.dto';
import { AuthorService } from '../../services/author.service';

@Component({
  selector: 'app-author-list',
  templateUrl: './author-list.component.html',
  styleUrls: ['./author-list.component.scss']
})
export class AuthorListComponent implements OnInit {

  authors: AuthorDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private authorService: AuthorService) { }

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
}