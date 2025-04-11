import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { BookListComponent } from './components/book-list/book-list.component';
import { AuthorListComponent } from './components/author-list/author-list.component';
import { LoanListComponent } from './components/loan-list/loan-list.component';
import { AuthorFormComponent } from './components/author-form/author-form.component';
import { BookFormComponent } from './components/book-form/book-form.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    BookListComponent,
    AuthorListComponent,
    LoanListComponent,
    AuthorFormComponent,
    BookFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule 
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
