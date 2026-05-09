package dev.lkeleti.ledgerflow.mapper;

import dev.lkeleti.ledgerflow.dto.response.PartnerResponse;
import dev.lkeleti.ledgerflow.entity.Partner;
import org.springframework.stereotype.Component;

@Component
public class PartnerMapper {
    public PartnerResponse toResponse(Partner partner) {

        PartnerResponse response = new PartnerResponse();

        response.setId(partner.getId());
        response.setName(partner.getName());
        response.setEmail(partner.getEmail());
        response.setPhone(partner.getPhone());
        response.setNote(partner.getNote());

        response.setPrivatePerson(partner.isPrivatePerson());

        response.setPostalCode(partner.getPostalCode());
        response.setCity(partner.getCity());
        response.setDistrict(partner.getDistrict());
        response.setStreetName(partner.getName());
        response.setStreetType(partner.getStreetType());
        response.setHouseNumber(partner.getHouseNumber());
        response.setBuilding(partner.getBuilding());
        response.setStaircase(partner.getStaircase());
        response.setFloor(partner.getFloor());
        response.setDoor(partner.getDoor());
        response.setPlotNumber(partner.getPlotNumber());

        response.setTaxNumber(partner.getTaxNumber());

        response.setCustomerAccountId(
                partner.getCustomerAccount() != null
                        ? partner.getCustomerAccount().getId()
                        : null
        );

        response.setSupplierAccountId(
                partner.getSupplierAccount() != null
                        ? partner.getSupplierAccount().getId()
                        : null
        );

        response.setBankAccountNumber(partner.getBankAccountNumber());
        response.setIban(partner.getIban());
        response.setSwift(partner.getSwift());

        response.setPaymentMethodId(
                partner.getPaymentMethod() != null
                        ? partner.getPaymentMethod().getId()
                        : null
        );

        response.setPaymentDeadlineDays(partner.getPaymentDeadlineDays());
        response.setDeleted(partner.isDeleted());
        response.setCreatedAt(partner.getCreatedAt());

        return response;
    }

}
