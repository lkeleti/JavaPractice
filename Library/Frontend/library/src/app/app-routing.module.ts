import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';


import { BookListComponent } from './components/book-list/book-list.component';
import { AuthorListComponent } from './components/author-list/author-list.component';
import { LoanListComponent } from './components/loan-list/loan-list.component';
import { AuthorFormComponent } from './components/author-form/author-form.component';
import { BookFormComponent } from './components/book-form/book-form.component';

const routes: Routes = [
  // Alapértelmezett útvonal: Ha a felhasználó a gyökér URL-re megy (''),
  // átirányítjuk a '/books' útvonalra.
  { path: '', redirectTo: '/books', pathMatch: 'full' },

  // Könyvek útvonal: A '/books' URL esetén a BookListComponent töltődik be.
  { path: 'books', component: BookListComponent },

  { path: 'books/new', component: BookFormComponent },

  // Szerzők útvonal: A '/authors' URL esetén az AuthorListComponent töltődik be.
  { path: 'authors', component: AuthorListComponent },

  { path: 'authors/new', component: AuthorFormComponent },

  // Kölcsönzések útvonal: A '/loans' URL esetén a LoanListComponent töltődik be.
  { path: 'loans', component: LoanListComponent },

  // TODO: Később ide jöhetnek pl. az edit/create útvonalak is, pl.:
  // { path: 'books/new', component: BookFormComponent },
  // { path: 'books/:id/edit', component: BookFormComponent },

  // Wildcard útvonal: Ha egyik fenti útvonal sem illeszkedik, ide irányíthatunk (pl. 404 oldal)
  // { path: '**', component: PageNotFoundComponent } // Ehhez létre kellene hozni a komponenst
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
