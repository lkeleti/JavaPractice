package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.UpdateZipCodeCommand;
import dev.lkeleti.invotraxapp.dto.CreateZipCodeCommand;
import dev.lkeleti.invotraxapp.dto.ZipCodeDto;
import dev.lkeleti.invotraxapp.model.ZipCode;
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
import java.util.List;

@AllArgsConstructor
@Service
public class ZipCodeService {
    private ZipCodeRepository zipCodeRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ZipCodeDto> getAllZipCodes() {
        Type targetListType = new TypeToken<List<ZipCodeDto>>(){}.getType();
        return modelMapper.map(zipCodeRepository.findAll(), targetListType);
    }
    public Page<ZipCodeDto> getAllZipCodes(Pageable pageable, String searchTerm) {
        Page<ZipCode> zipCodes;
        if (searchTerm == null || searchTerm.isBlank()) {
            zipCodes = zipCodeRepository.findAll(pageable);
        } else {
            zipCodes = zipCodeRepository.searchByZipOrCity(searchTerm, pageable);
        }

        return zipCodes.map(zipCode -> modelMapper.map(zipCode, ZipCodeDto.class));
    }


    @Transactional(readOnly = true)
    public List<ZipCodeDto> getAllActiveZipCodes() {
        Type targetListType = new TypeToken<List<ZipCodeDto>>(){}.getType();
        return modelMapper.map(zipCodeRepository.findByDeletedIsFalse(), targetListType);
    }

    @Transactional(readOnly = true)
    public List<ZipCodeDto> getAllActiveZipCodesByZip(String zip) {
        Type targetListType = new TypeToken<List<ZipCodeDto>>(){}.getType();
        return modelMapper.map(zipCodeRepository.findByZipAndDeletedFalse(zip), targetListType);
    }

    @Transactional(readOnly = true)
    public ZipCodeDto getZipCodeById(long id) {
        ZipCode zipCode = zipCodeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find zip code")
        );
        return modelMapper.map(zipCode, ZipCodeDto.class);
    }

    @Transactional
    public ZipCodeDto updateZipCode(Long id, UpdateZipCodeCommand command) {
        ZipCode zipCode = zipCodeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find zip code")
        );
        zipCode.setZip(command.getZip());
        zipCode.setCity(command.getCity());
        zipCode.setDeleted(command.isDeleted());
        return modelMapper.map(zipCode, ZipCodeDto.class);
    }

    @Transactional
    public ZipCodeDto createZipCode(CreateZipCodeCommand command) {
        ZipCode zipCode = new ZipCode();

        zipCode.setZip(command.getZip());
        zipCode.setCity(command.getCity());
        zipCode.setDeleted(false);
        return modelMapper.map(zipCodeRepository.save(zipCode), ZipCodeDto.class);
    }

    @Transactional
    public void deleteZipCode(Long id) {
        ZipCode zipCode = zipCodeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find zip code")
        );

        zipCode.setDeleted(true);
    }

    @Transactional
    public ZipCodeDto unDeleteZipCode(Long id) {
        ZipCode zipCode = zipCodeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find zip code")
        );
        zipCode.setDeleted(false);
        return modelMapper.map(zipCode, ZipCodeDto.class);
    }
}
