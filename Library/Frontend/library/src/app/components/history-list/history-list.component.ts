import { Component, OnInit } from '@angular/core';
import { LoanService } from 'src/app/services/loan.service';
import { LoanDto } from 'src/app/models/loan.dto';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-history-list',
  templateUrl: './history-list.component.html',
  styleUrls: ['./history-list.component.scss']
})
export class HistoryListComponent implements OnInit {
  loans: LoanDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  searchTerm: string = '';
  bookId: number = -1;

  constructor(
    private loanService: LoanService,
    private route: ActivatedRoute,
  ) { }
  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.bookId = +idParam; // '+' jellel számmá alakítjuk a stringet
      this.loadHistory(); // Metódus az adatok betöltésére
    } else {
      // Ha nincs ID, akkor Create módban vagyunk
      this.bookId = -1;
    }
  }

  loadHistory() {
    this.isLoading = true;
    this.errorMessage = null;

    this.loanService.getHistory(this.bookId).subscribe({
      next: (data) => {
        this.loans = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching history:', err);
        this.errorMessage = 'Failed to load book history. Please try again later.';
        this.isLoading = false;
      },
    });
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

}
