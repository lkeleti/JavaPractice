import { ManufacturerDto } from "./manufacturer.dto";

export interface Page<T> {
    content: T[];
    page: {
        size: number;
        number: number;
        totalElements: number;
        totalPages: number;
    };
}

// Specific type for Manufacturers
export type PaginatedManufacturersResponse = Page<ManufacturerDto>;

