package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateSellerCompanyProfileCommand;
import dev.lkeleti.invotraxapp.dto.SellerCompanyProfileDto;
import dev.lkeleti.invotraxapp.dto.UpdateSellerCompanyProfileCommand;
import dev.lkeleti.invotraxapp.exception.ResourceNotFoundException;
import dev.lkeleti.invotraxapp.model.Partner;
import dev.lkeleti.invotraxapp.model.SellerCompanyProfile;
import dev.lkeleti.invotraxapp.repository.PartnerRepository;
import dev.lkeleti.invotraxapp.repository.SellerCompanyProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class SellerCompanyProfileService {
    private final SellerCompanyProfileRepository sellerRepository;
    private final PartnerRepository partnerRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public SellerCompanyProfileDto getProfile() {
        SellerCompanyProfile profile = sellerRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("SELLER_NOT_FOUND"));
        return modelMapper.map(profile, SellerCompanyProfileDto.class);
    }

    @Transactional
    public SellerCompanyProfileDto createProfile(CreateSellerCompanyProfileCommand command) {
        if (sellerRepository.count() > 0) {
            throw new IllegalStateException("Már létezik saját cég profil.");
        }
        Partner partner = partnerRepository.findById(command.getPartnerId())
                .orElseThrow(() -> new EntityNotFoundException("Partner nem található."));

        SellerCompanyProfile profile = new SellerCompanyProfile();
        profile.setPartner(partner);
        profile.setHeadOfficeAddress(command.getHeadOfficeAddress());
        profile.setDefaultBranchAddress(command.getDefaultBranchAddress());
        profile.setCompanyRegistrationNumber(command.getCompanyRegistrationNumber());

        return modelMapper.map(sellerRepository.save(profile), SellerCompanyProfileDto.class);
    }

    @Transactional
    public SellerCompanyProfileDto updateProfile(UpdateSellerCompanyProfileCommand command) {
        SellerCompanyProfile profile = sellerRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nincs saját cég profil."));

        profile.setHeadOfficeAddress(command.getHeadOfficeAddress());
        profile.setDefaultBranchAddress(command.getDefaultBranchAddress());
        profile.setCompanyRegistrationNumber(command.getCompanyRegistrationNumber());

        return modelMapper.map(sellerRepository.save(profile), SellerCompanyProfileDto.class);
    }
}