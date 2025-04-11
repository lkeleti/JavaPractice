import { BookDto } from "./book.dto"; // Importáld a másikat

export interface LoanDto {
  id: number;
  borrowerName: string;
  loanDate: string;
  dueDate: string;
  returnDate?: string;
  book: BookDto;
 }