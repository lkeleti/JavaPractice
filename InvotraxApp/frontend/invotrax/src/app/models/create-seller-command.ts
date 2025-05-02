import { CreatePartnerCommand } from "./create-partner.command";

export interface CreateSellerCommand {
    partnerId: number;
    headOfficeAddress: string;
    defaultBranchAddress: string;
    companyRegistrationNumber: string;
}
