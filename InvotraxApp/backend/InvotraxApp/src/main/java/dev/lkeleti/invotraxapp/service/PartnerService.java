package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreatePartnerCommand;
import dev.lkeleti.invotraxapp.dto.PartnerDto;
import dev.lkeleti.invotraxapp.dto.UpdatePartnerCommand;
import dev.lkeleti.invotraxapp.model.Partner;
import dev.lkeleti.invotraxapp.model.PaymentMethod;
import dev.lkeleti.invotraxapp.model.ZipCode;
import dev.lkeleti.invotraxapp.repository.PartnerRepository;
import dev.lkeleti.invotraxapp.repository.PaymentMethodRepository;
import dev.lkeleti.invotraxapp.repository.ZipCodeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class PartnerService {
    private PartnerRepository partnerRepository;
    private ZipCodeRepository zipCodeRepository;
    private ModelMapper modelMapper;
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional(readOnly = true)
    public Page<PartnerDto> getAllPartners(Pageable pageable, String searchTerm) {
        Page<Partner> partners;
        if (searchTerm == null || searchTerm.isBlank()) {
            partners = partnerRepository.findAllExcludingSeller(pageable);
        } else {
            partners = partnerRepository.searchByNameOrTaxNumberExcludingSeller(searchTerm, pageable);
        }

        return partners.map(partner -> modelMapper.map(partner, PartnerDto.class));
    }


    @Transactional(readOnly = true)
    public List<PartnerDto> getAllActivePartners() {
        Type targetListType = new TypeToken<List<PartnerDto>>(){}.getType();
        return modelMapper.map(partnerRepository.findByDeletedIsFalse(), targetListType);
    }

    @Transactional(readOnly = true)
    public PartnerDto getPartnerByTaxNumber(String taxNumber) {
        Partner partner = partnerRepository.findByTaxNumber(taxNumber).orElseThrow(
                () -> new EntityNotFoundException("Cannot find partner")
        );
        return modelMapper.map(partner, PartnerDto.class);
    }

    @Transactional(readOnly = true)
    public PartnerDto getPartnerById(long id) {
        Partner partner = partnerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find partner")
        );
        return modelMapper.map(partner, PartnerDto.class);
    }

    @Transactional
    public PartnerDto updatePartner(Long id, UpdatePartnerCommand command) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found with id: " + id));

        ZipCode zipCode = zipCodeRepository.findById(command.getZipCodeId())
                .orElseThrow(() -> new EntityNotFoundException("ZipCode not found with id: " + command.getZipCodeId()));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(command.getPreferredPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("PAyment method not found with id: " + command.getPreferredPaymentMethodId()));

        partner.setName(command.getName());
        partner.setZipCode(zipCode);
        partner.setStreetName(command.getStreetName());
        partner.setStreetType(command.getStreetType());
        partner.setHouseNumber(command.getHouseNumber());
        partner.setBuilding(command.getBuilding());
        partner.setStaircase(command.getStaircase());
        partner.setFloor(command.getFloor());
        partner.setDoor(command.getDoor());
        partner.setLandRegistryNumber(command.getLandRegistryNumber());
        partner.setPrivate(command.isPrivate());
        partner.setTaxNumber(command.getTaxNumber());
        partner.setEmail(command.getEmail());
        partner.setPhoneNumber(command.getPhoneNumber());
        partner.setPreferredPaymentMethod(paymentMethod);
        partner.setBalance(command.getBalance());
        partner.setDefaultPaymentDeadline(command.getDefaultPaymentDeadline());
        partner.setBankName(command.getBankName());
        partner.setBankNumber(command.getBankNumber());
        partner.setIban(command.getIban());
        partner.setDeleted(command.isDeleted());

        return modelMapper.map(partnerRepository.save(partner), PartnerDto.class);
    }


    @Transactional
    public PartnerDto createPartner(CreatePartnerCommand command) {
        ZipCode zipCode = zipCodeRepository.findById(command.getZipCodeId())
                .orElseThrow(() -> new EntityNotFoundException("ZipCode not found with id: " + command.getZipCodeId()));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(command.getPreferredPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("PAyment method not found with id: " + command.getPreferredPaymentMethodId()));

        Partner partner = new Partner();
        partner.setName(command.getName());
        partner.setZipCode(zipCode);
        partner.setStreetName(command.getStreetName());
        partner.setStreetType(command.getStreetType());
        partner.setHouseNumber(command.getHouseNumber());
        partner.setBuilding(command.getBuilding());
        partner.setStaircase(command.getStaircase());
        partner.setFloor(command.getFloor());
        partner.setDoor(command.getDoor());
        partner.setLandRegistryNumber(command.getLandRegistryNumber());
        partner.setPrivate(command.isPrivate());
        partner.setTaxNumber(command.getTaxNumber());
        partner.setEmail(command.getEmail());
        partner.setPhoneNumber(command.getPhoneNumber());
        partner.setPreferredPaymentMethod(paymentMethod);
        partner.setBalance(command.getBalance());
        partner.setDefaultPaymentDeadline(command.getDefaultPaymentDeadline());
        partner.setBankName(command.getBankName());
        partner.setBankNumber(command.getBankNumber());
        partner.setIban(command.getIban());

        partner.setCreatedAt(LocalDateTime.now());
        partner.setDeleted(false);

        return modelMapper.map(partnerRepository.save(partner), PartnerDto.class);
    }


    @Transactional
    public void deletePartner(Long id) {
        Partner partner = partnerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find partner")
        );

        partner.setDeleted(true);
    }

    @Transactional
    public PartnerDto unDeletePartner(Long id) {
        Partner partner = partnerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find partner")
        );
        partner.setDeleted(false);
        return modelMapper.map(partner, PartnerDto.class);
    }
}
