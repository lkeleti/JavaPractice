export interface CreatePartnerCommand {
    name: string;
    zipCodeId: number;

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
    preferredPaymentMethodId: number;
    createdAt: Date;
    balance: number;
    defaultPaymentDeadline: number;
    bankName: string;
    bankNumber: string;
    iban: string;
    deleted: boolean;
}