import { PartnerDto } from "./partner.dto";

export interface SellerCompanyProfileDto {
    id: number;
    partner: PartnerDto;
    headOfficeAddress: string;
    defaultBranchAddress: string;
    companyRegistrationNumber: string;
}
