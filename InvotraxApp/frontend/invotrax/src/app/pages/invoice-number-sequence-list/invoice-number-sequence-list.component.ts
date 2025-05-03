import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { InvoiceNumberSequenceDto } from '../../models/invoice-number-sequence-dto';
import { InvoiceNumberSequenceService } from '../../services/invoice-number-sequence.service';

@Component({
  selector: 'app-invoice-number-sequence-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './invoice-number-sequence-list.component.html',
  styleUrl: './invoice-number-sequence-list.component.scss',
})

export class InvoiceNumberSequenceListComponent implements OnInit {
  sequences: InvoiceNumberSequenceDto[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  searchTerm: string = '';

  constructor(private invoiceNumberSequenceService: InvoiceNumberSequenceService) { }

  ngOnInit(): void {
    this.loadSequences();
  }

  loadSequences(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.invoiceNumberSequenceService.getInvoiceNumberSequence().subscribe({
      next: (data) => {
        this.sequences = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching sequences:', err);
        this.errorMessage =
          'Nem sikerült betölteni a sorszám szabályokat. Kérem próbálja meg később.';
        this.isLoading = false;
      },
    });
  }

  get filteredSequences(): InvoiceNumberSequenceDto[] {
    if (!this.searchTerm) return this.sequences;
    const term = this.searchTerm.toLowerCase();
    return this.sequences.filter(seq =>
      seq.invoicePrefix.toLowerCase().includes(term) ||
      seq.invoiceType.name.toLowerCase().includes(term)
    );
  }
}
