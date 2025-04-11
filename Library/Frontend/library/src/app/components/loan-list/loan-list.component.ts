import { Component,OnInit } from '@angular/core';
import { LoanDto } from '../../models/loan.dto';
import { LoanService } from '../../services/loan.service';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent  implements OnInit {
  loans: LoanDto[] = [];
    isLoading = false;
    errorMessage: string | null = null;
  
    constructor(private loanService: LoanService) { }
  
    ngOnInit(): void {
      this.loadLoans();
    }
  
    loadLoans(): void {
      this.isLoading = true;
      this.errorMessage = null;
  
      this.loanService.getLoans()
        .subscribe({
          next: (data) => { 
            this.loans = data;
            this.isLoading = false;
          },
          error: (err) => { 
            console.error('Error fetching loans:', err);
            this.errorMessage = 'Failed to load loans. Please try again later.';
            this.isLoading = false;
          }
          // complete: () => { console.log('Loan loading completed.'); } // Opcionális: lefut a next vagy error után
        });
    }
  }
  