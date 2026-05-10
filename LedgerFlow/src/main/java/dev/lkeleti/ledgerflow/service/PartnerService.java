package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.PartnerCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.PartnerFilterRequest;
import dev.lkeleti.ledgerflow.dto.request.PartnerUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.PartnerResponse;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.entity.Partner;
import dev.lkeleti.ledgerflow.entity.PaymentMethod;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.PartnerMapper;
import dev.lkeleti.ledgerflow.repository.GLAccountRepository;
import dev.lkeleti.ledgerflow.repository.PartnerRepository;
import dev.lkeleti.ledgerflow.repository.PaymentMethodRepository;
import dev.lkeleti.ledgerflow.service.helper.PartnerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final GLAccountRepository glAccountRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PartnerMapper partnerMapper;

    @Transactional(readOnly = true)
    public Page<PartnerResponse> list(PartnerFilterRequest filter, Pageable pageable) {

        Page<Partner> page = partnerRepository.findAll(
                PartnerSpecification.filter(filter),
                pageable
        );

        return page.map(partnerMapper::toResponse);
    }


    @Transactional(readOnly = true)
    public PartnerResponse getById(Long id) {

        return partnerRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .map(partnerMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.PARTNER_NOT_FOUND));
    }

    @Transactional
    public PartnerResponse create(PartnerCreateRequest request) {

        Partner partner = new Partner();

        validateAndModify(request, partner);

        return partnerMapper.toResponse(partnerRepository.save(partner));
    }

    @Transactional
    public PartnerResponse update(Long id, PartnerUpdateRequest request) {

        Partner partner = partnerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ErrorMessage.PARTNER_NOT_FOUND)
        );

        validateAndModify(request, partner);

        return partnerMapper.toResponse(partnerRepository.save(partner));
    }

    @Transactional
    public void delete(Long id) {

        Partner partner = partnerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ErrorMessage.PARTNER_NOT_FOUND)
        );
        partner.setDeleted(true);
        partnerRepository.save(partner);
    }

    @Transactional
    public PartnerResponse restore(Long id) {

        Partner partner = partnerRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ErrorMessage.PARTNER_NOT_FOUND)
        );
        partner.setDeleted(false);
        return partnerMapper.toResponse(partnerRepository.save(partner));
    }

    // =========================
    // Validate
    // =========================

    private void validateAndModify(PartnerCreateRequest request, Partner partner) {

        partner.setName(request.getName());
        partner.setPrivatePerson(request.isPrivatePerson());

        partner.setPostalCode(request.getPostalCode());
        partner.setCity(request.getCity());
        partner.setDistrict(request.getDistrict());
        partner.setStreetName(request.getStreetName());
        partner.setStreetType(request.getStreetType());
        partner.setHouseNumber(request.getHouseNumber());
        partner.setBuilding(request.getBuilding());
        partner.setStaircase(request.getStaircase());
        partner.setFloor(request.getFloor());
        partner.setDoor(request.getDoor());
        partner.setPlotNumber(request.getPlotNumber());

        partner.setTaxNumber(request.getTaxNumber());

        if (request.getCustomerAccountId() != null) {

            GLAccount account = glAccountRepository.findById(
                    request.getCustomerAccountId()
            ).orElseThrow(() ->
                    new NotFoundException(ErrorMessage.CUSTOMER_ACCOUNT_NOT_FOUND));
            partner.setCustomerAccount(account);
        }

        if (request.getSupplierAccountId() != null) {

            GLAccount account = glAccountRepository.findById(
                    request.getSupplierAccountId()
            ).orElseThrow(() ->
                    new NotFoundException(ErrorMessage.SUPPLIER_ACCOUNT_NOT_FOUND));

            partner.setSupplierAccount(account);
        }

        if (request.getPaymentMethodId() != null) {

            PaymentMethod paymentMethod =
                    paymentMethodRepository.findById(
                            request.getPaymentMethodId()
                    ).orElseThrow(() ->
                            new NotFoundException(ErrorMessage.PAYMENT_METHOD_NOT_FOUND));

            partner.setPaymentMethod(paymentMethod);
        }

        partner.setBankAccountNumber(request.getBankAccountNumber());
        partner.setIban(request.getIban());
        partner.setSwift(request.getSwift());

        partner.setPaymentDeadlineDays(
                Optional.ofNullable(request.getPaymentDeadlineDays()).orElse(0)
        );

        partner.setEmail(request.getEmail());
        partner.setPhone(request.getPhone());

        partner.setNote(request.getNote());
    }
}