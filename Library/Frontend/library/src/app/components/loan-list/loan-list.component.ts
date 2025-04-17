import { LoanDto } from './../../models/loan.dto';
import { Component, OnInit } from '@angular/core';
import { LoanService } from '../../services/loan.service';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss'],
})
export class LoanListComponent implements OnInit {
  loans: LoanDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  radioState = 1;
  searchTerm: string = '';

  constructor(
    private loanService: LoanService,
    private modalService: NgbModal,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadLoans();
  }

  get filteredLoans(): LoanDto[] {
    if (!this.searchTerm) return this.loans;
    const term = this.searchTerm.toLowerCase();
    return this.loans.filter(
      (loan) =>
        loan.book.title.toLowerCase().includes(term) ||
        loan.borrowerName.toLowerCase().includes(term)
    );
  }

  openReturnConfirmation(loan: LoanDto): void {
    // Megnyitjuk a modális ablakot a ConfirmationDialogComponent tartalmával
    const modalRef = this.modalService.open(ConfirmationDialogComponent);

    // Átadjuk a szükséges adatokat a modális komponensnek (@Input)
    modalRef.componentInstance.title = 'Confirm Return book';
    modalRef.componentInstance.message = `Are you sure you want to return book "${loan.book.title}" (ID: ${loan.id})? This action cannot be undone.`;
    modalRef.componentInstance.confirmText = 'Return';

    // Kezeljük a modális ablak bezárásának eredményét (Promise-t ad vissza)
    modalRef.result.then(
      (result) => {
        // Ez az ág fut le, ha a modált a .close() metódussal zárták be (Confirm gomb)
        if (result === true) {
          console.log('Return confirmed for book:', loan.book.id);
          this.returnBook(loan.book.id); // Meghívjuk a tényleges törlő metódust
        }
      },
      (reason) => {
        // Ez az ág fut le, ha a modált a .dismiss() metódussal zárták be (Cancel, 'x', Esc)
        console.log('Return dismissed:', reason);
      }
    );
  }

  returnBook(bookId: number): void {
    this.isLoading = true; // Opcionális: jelezhetjük a törlés folyamatát
    this.errorMessage = null;

    this.loanService
      .returnBook(bookId) // Feltételezve, hogy van ilyen metódus a service-ben
      .subscribe({
        next: () => {
          console.log('Book returned successfully:', bookId);
          this.toastr.success(
            `Book returned successfully: ${bookId}`,
            'Returned successful'
          );
          this.isLoading = false;
          // Frissítjük a listát a törlés után
          this.loadLoans();
        },
        error: (err) => {
          console.error('Error returning book:', err);
          this.toastr.error(
            err.error?.message || 'Failed to return book.',
            'Returned failed'
          );
          this.errorMessage = err.error?.message || 'Failed to return book.';
          this.isLoading = false;
        },
      });
  }

  radio(selected: number) {
    this.radioState = selected;
    this.loadLoans();
  }

  loadLoans(): void {
    switch (this.radioState) {
      case 1:
        this.loadLoanData(() => this.loanService.getLoans());
        break;
      case 2:
        this.loadLoanData(() => this.loanService.getActiveLoans());
        break;
      case 3:
        this.loadLoanData(() => this.loanService.getOverdueLoans());
        break;
    }
  }

  private loadLoanData(apiCall: () => Observable<LoanDto[]>) {
    this.isLoading = true;
    this.errorMessage = null;

    apiCall().subscribe({
      next: (data) => {
        this.loans = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching loans:', err);
        this.errorMessage = 'Failed to load loans. Please try again later.';
        this.isLoading = false;
      },
    });
  }
}
