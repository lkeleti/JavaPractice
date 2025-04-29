import { PartnerDto } from '../../models/partner.dto';

export function getPartnerAddress(partner: PartnerDto): string {
    return `${partner.zipCode.zip} ${partner.zipCode.city}, ` +
        (partner.streetName ? partner.streetName + " " : "") +
        (partner.streetType ? partner.streetType + " " : "") +
        (partner.building ? partner.building + " " : "") +
        (partner.staircase ? partner.staircase + " " : "") +
        (partner.houseNumber ? partner.houseNumber + ". " : "") +
        (partner.floor ? partner.floor + "/ " : "") +
        (partner.door ? partner.door + "." : "") +
        (partner.landRegistryNumber ? partner.landRegistryNumber + " hrsz." : "");
}
