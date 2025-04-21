package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateVatRateCommand;
import dev.lkeleti.invotraxapp.dto.UpdateVatRateCommand;
import dev.lkeleti.invotraxapp.dto.VatRateDto;
import dev.lkeleti.invotraxapp.model.VatRate;
import dev.lkeleti.invotraxapp.repository.VatRateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@AllArgsConstructor
@Service
public class VatRateService {
    private VatRateRepository vatRateRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<VatRateDto> getAllVatRates() {
        Type targetListType = new TypeToken<List<VatRateDto>>(){}.getType();
        return modelMapper.map(vatRateRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public List<VatRateDto> getAllActiveVatRates() {
        Type targetListType = new TypeToken<List<VatRateDto>>(){}.getType();
        return modelMapper.map(vatRateRepository.findByDeletedIsFalse(), targetListType);
    }

    @Transactional(readOnly = true)
    public VatRateDto getVatRateById(long id) {
        VatRate vatRate = vatRateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find vat rate")
        );
        return modelMapper.map(vatRate, VatRateDto.class);
    }

    @Transactional
    public VatRateDto updateVatRate(Long id, UpdateVatRateCommand command) {
        VatRate vatRate = vatRateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find vat rate")
        );

        vatRate.setRate(command.getRate());
        vatRate.setName(command.getName());
        vatRate.setDeleted(command.isDeleted());
        return modelMapper.map(vatRate, VatRateDto.class);
    }

    @Transactional
    public VatRateDto createVatRate(CreateVatRateCommand command) {
        VatRate vatRate = new VatRate();
        vatRate.setName(command.getName());
        vatRate.setRate(command.getRate());
        vatRate.setDeleted(false);
        vatRateRepository.save(vatRate);
        return modelMapper.map(vatRate,VatRateDto.class);
    }

    public void deleteVatRate(Long id) {
        VatRate vatRate = vatRateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find vat rate")
        );
        vatRate.setDeleted(true);
    }

    public VatRateDto unDeleteVatRate(Long id) {
        VatRate vatRate = vatRateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find vat rate")
        );
        vatRate.setDeleted(false);
        return modelMapper.map(vatRate, VatRateDto.class);
    }
}
