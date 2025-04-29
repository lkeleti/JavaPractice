import { ZipCodeDto } from "./zip-code.dto";

export interface PartnerDto {
    id: number;
    name: string;
    zipCode: ZipCodeDto;

    streetName: string;
    streetType: string;
    houseNumber: string;
    building: string;
    staircase: string;
    floor: string;
    door: string;
    landRegistryNumber: string;

    isPrivate: boolean;
    taxNumber: string;
    email: string;
    phoneNumber: string;
    preferredPaymentMethod: string;
    createdAt: Date;
    balance: number;
    defaultPaymentDeadline: number;
    bankName: string;
    bankNumber: string;
    iban: string;
    deleted: boolean;
}