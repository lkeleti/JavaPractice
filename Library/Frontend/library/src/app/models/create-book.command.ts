export interface CreateBookCommand {
    isbn: string;
    title: string;
    publicationYear: number;
    authorId: number;
}