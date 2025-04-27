import { ManufacturerDto } from "./manufacturer.dto";

// Generic interface for Spring Data Page<> like structure
export interface Page<T> {
    content: T[];          // The list of items for the current page
    pageable: {
        pageNumber: number;  // Current page number (usually 0-based)
        pageSize: number;    // Items per page
        sort: {
            sorted: boolean;
            unsorted: boolean;
            empty: boolean;
        };
        offset: number;
        paged: boolean;
        unpaged: boolean;
    };
    totalPages: number;    // Total number of pages
    totalElements: number; // Total number of items across all pages
    last: boolean;         // Is this the last page?
    first: boolean;        // Is this the first page?
    size: number;          // Items per page (same as pageSize)
    number: number;        // Current page number (0-based, same as pageNumber)
    sort: {
        sorted: boolean;
        unsorted: boolean;
        empty: boolean;
    };
    numberOfElements: number; // Number of items on the current page
    empty: boolean;        // Is the current page empty?
}

// Specific type for Manufacturers
export type PaginatedManufacturersResponse = Page<ManufacturerDto>;

