import { Component, Input, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LoanService } from 'src/app/services/loan.service';
import { CheckoutBookCommand } from '../../models/checkout-book.command';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-checkout-modal',
  templateUrl: './checkout-modal.component.html',
  styleUrls: ['./checkout-modal.component.scss'],
})
export class CheckoutModalComponent implements OnInit {
  @Input() bookId!: number;
  @Input() bookTitle: string = '';
  @Input() authorName: string = '';
  @Input() bookIsbn: string = '';

  checkoutForm!: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;

  // Szükséges függőségek
  constructor(
    public activeModal: NgbActiveModal,
    private fb: FormBuilder,
    private loanService: LoanService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.checkoutForm = this.fb.group({
      borrowerName: [null, [Validators.required, Validators.maxLength(255)]],
    });
  }

  // Getter a könnyebb hivatkozáshoz
  get borrowerNameControl(): AbstractControl | null {
    return this.checkoutForm.get('borrowerName');
  }

  // Mentés / Checkout logika
  save(): void {
    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched(); // Mutassa a hibákat, ha nem érintette a user
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    const command: CheckoutBookCommand = {
      bookId: this.bookId,
      borrowerName: this.borrowerNameControl?.value,
    };

    this.loanService.checkoutBook(command).subscribe({
      next: (createdLoan) => {
        this.isLoading = false;
        const loanDateStr = createdLoan.loanDate;
        const dueDateStr = createdLoan.dueDate;
        const successMessage =
          `Book "${this.bookTitle}" checked out to ${createdLoan.borrowerName}. ` +
          `Loan date: ${loanDateStr}, Due date: ${dueDateStr}.`;

        this.toastr.success(successMessage, 'Checkout Successful');
        this.activeModal.close('success');
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error checking out book:', err);
        this.errorMessage =
          err.error?.message || 'Failed to checkout book. Please try again.';
        this.toastr.error(
          this.errorMessage ?? 'An unknown error occurred.',
          'Checkout Failed'
        );
      },
    });
  }

  dismiss(): void {
    this.activeModal.dismiss('cancel click');
  }
}
