package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateInvoiceTypeCommand;
import dev.lkeleti.invotraxapp.dto.InvoiceTypeDto;
import dev.lkeleti.invotraxapp.dto.UpdateInvoiceTypeCommand;
import dev.lkeleti.invotraxapp.model.InvoiceType;
import dev.lkeleti.invotraxapp.repository.InvoiceTypeRepository;
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
public class InvoiceTypeService {
    private InvoiceTypeRepository invoiceTypeRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<InvoiceTypeDto> getAllInvoiceTypes() {
        Type targetListType = new TypeToken<List<InvoiceTypeDto>>(){}.getType();
        return modelMapper.map(invoiceTypeRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public List<InvoiceTypeDto> getAllActiveInvoiceTypes() {
        Type targetListType = new TypeToken<List<InvoiceTypeDto>>(){}.getType();
        return modelMapper.map(invoiceTypeRepository.findByDeletedIsFalse(), targetListType);
    }

    @Transactional(readOnly = true)
    public InvoiceTypeDto getInvoiceTypeById(long id) {
        InvoiceType invoiceType = invoiceTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice type")
        );
        return modelMapper.map(invoiceType, InvoiceTypeDto.class);
    }

    @Transactional
    public InvoiceTypeDto updateInvoiceType(Long id, UpdateInvoiceTypeCommand command) {
        InvoiceType invoiceType = invoiceTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice type")
        );
        invoiceType.setName(command.getName());
        invoiceType.setDeleted(command.isDeleted());
        return modelMapper.map(invoiceType, InvoiceTypeDto.class);
    }

    @Transactional
    public InvoiceTypeDto createInvoiceType(CreateInvoiceTypeCommand command) {
        InvoiceType invoiceType = new InvoiceType();
        invoiceType.setName(command.getName());
        invoiceType.setDeleted(command.isDeleted());
        return modelMapper.map(invoiceTypeRepository.save(invoiceType), InvoiceTypeDto.class);
    }

    @Transactional
    public void deleteInvoiceType(Long id) {
        InvoiceType invoiceType = invoiceTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice type")
        );
        invoiceType.setDeleted(true);
    }

    @Transactional
    public InvoiceTypeDto unDeleteInvoiceType(Long id) {
        InvoiceType invoiceType = invoiceTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice type")
        );
        invoiceType.setDeleted(false);
        return modelMapper.map(invoiceType, InvoiceTypeDto.class);
    }
}
