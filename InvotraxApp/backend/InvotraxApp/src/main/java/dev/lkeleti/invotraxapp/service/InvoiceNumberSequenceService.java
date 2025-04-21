package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateInvoiceNumberSequenceCommand;
import dev.lkeleti.invotraxapp.dto.InvoiceNumberSequenceDto;
import dev.lkeleti.invotraxapp.dto.UpdateInvoiceNumberSequenceCommand;
import dev.lkeleti.invotraxapp.model.InvoiceNumberSequence;
import dev.lkeleti.invotraxapp.model.InvoiceType;
import dev.lkeleti.invotraxapp.repository.InvoiceNumberSequenceRepository;
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
public class InvoiceNumberSequenceService {
    private InvoiceNumberSequenceRepository invoiceNumberSequenceRepository;
    private ModelMapper modelMapper;
    private InvoiceTypeRepository invoiceTypeRepository;

    @Transactional(readOnly = true)
    public List<InvoiceNumberSequenceDto> getAllInvoiceNumberSequences() {
        Type targetListType = new TypeToken<List<InvoiceNumberSequenceDto>>(){}.getType();
        return modelMapper.map(invoiceNumberSequenceRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public InvoiceNumberSequenceDto getInvoiceNumberSequenceById(long id) {
        InvoiceNumberSequence invoiceNumberSequence = invoiceNumberSequenceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice number sequence")
        );
        return modelMapper.map(invoiceNumberSequence, InvoiceNumberSequenceDto.class);
    }

    @Transactional
    public InvoiceNumberSequenceDto updateInvoiceNumberSequence(Long id, UpdateInvoiceNumberSequenceCommand command) {
        InvoiceNumberSequence invoiceNumberSequence = invoiceNumberSequenceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice number sequence")
        );

        InvoiceType invoiceType = invoiceTypeRepository.findById(command.getInvoiceTypeId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice type")
        );

        invoiceNumberSequence.setInvoicePrefix(command.getInvoicePrefix());
        invoiceNumberSequence.setLastNumber(command.getLastNumber());
        invoiceNumberSequence.setInvoiceType(invoiceType);
        return modelMapper.map(invoiceNumberSequence, InvoiceNumberSequenceDto.class);
    }

    @Transactional
    public InvoiceNumberSequenceDto createInvoiceNumberSequence(CreateInvoiceNumberSequenceCommand command) {
        InvoiceNumberSequence invoiceNumberSequence = new InvoiceNumberSequence();

        InvoiceType invoiceType = invoiceTypeRepository.findById(command.getInvoiceTypeId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find invoice type")
        );

        invoiceNumberSequence.setInvoicePrefix(command.getInvoicePrefix());
        invoiceNumberSequence.setLastNumber(command.getLastNumber());
        invoiceNumberSequence.setInvoiceType(invoiceType);
        return modelMapper.map(invoiceNumberSequenceRepository.save(invoiceNumberSequence), InvoiceNumberSequenceDto.class);
    }
}
