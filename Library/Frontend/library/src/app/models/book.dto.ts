import { AuthorDto } from "./author.dto"; // Importáld a másikat

export interface BookDto {
  id: number;
  isbn: string;
  title: string;
  publicationYear?: number;
   author: AuthorDto;
 }