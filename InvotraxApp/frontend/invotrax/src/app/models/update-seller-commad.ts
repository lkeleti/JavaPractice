import { UpdatePartnerCommand } from "./update-partner.command";

export interface UpdateSellerCommand {
    partnerId: number;
    headOfficeAddress: string;
    defaultBranchAddress: string;
    companyRegistrationNumber: string;
}