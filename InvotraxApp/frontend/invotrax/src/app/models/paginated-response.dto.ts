import { ManufacturerDto } from "./manufacturer.dto";
import { PartnerDto } from "./partner.dto";
import { ProductDto } from "./product.dto";
import { ZipCodeDto } from "./zip-code.dto";

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
export type PaginatedZipCodesResponse = Page<ZipCodeDto>;
export type PaginatedPartnersResponse = Page<PartnerDto>;
export type PaginatedProductsResponse = Page<ProductDto>;

