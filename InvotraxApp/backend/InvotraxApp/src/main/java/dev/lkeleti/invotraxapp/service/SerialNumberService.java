package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.model.*;
import dev.lkeleti.invotraxapp.repository.InventoryItemRepository;
import dev.lkeleti.invotraxapp.repository.InvoiceItemRepository;
import dev.lkeleti.invotraxapp.repository.ProductRepository;
import dev.lkeleti.invotraxapp.repository.SerialNumberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class SerialNumberService {
    private SerialNumberRepository serialNumberRepository;
    private ProductRepository productRepository;
    private InventoryItemRepository inventoryItemRepository;
    private InvoiceItemRepository invoiceItemRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public SerialNumberDetailsDto getDetailsBySerial(String serial) {
        SerialNumber serialNumber = serialNumberRepository.findFullInfoBySerial(serial)
                .orElseThrow(() -> new EntityNotFoundException("Serial number not found: " + serial));
        Product product = serialNumber.getProduct();

        LocalDate receivedAt = serialNumber.getInventoryItem().getInventory().getReceivedAt();
        LocalDate soldAt = serialNumber.getInvoiceItem() != null ? serialNumber.getInvoiceItem().getInvoice().getIssuedAt() : null;
        int warrantyMonths = product.getWarrantyPeriodMonths() != null ? product.getWarrantyPeriodMonths() : 0;
        LocalDate warrantyEndDate = soldAt != null ? soldAt.plusMonths(warrantyMonths) : null;

        return new SerialNumberDetailsDto(
                serialNumber.getId(),
                serialNumber.getSerial(),
                serialNumber.getProduct().getName(),
                receivedAt,
                soldAt,
                warrantyMonths,
                warrantyEndDate,
                serialNumber.isUsed()
        );
    }

    @Transactional(readOnly = true)
    public List<SerialNumberDto> getAllSerialNumbers() {
        Type targetListType = new TypeToken<List<SerialNumberDto>>(){}.getType();
        return modelMapper.map(serialNumberRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public List<SerialNumberDto> getAllUsedSerialNumbers() {
        Type targetListType = new TypeToken<List<SerialNumberDto>>(){}.getType();
        return modelMapper.map(serialNumberRepository.findAllByUsedTrue(), targetListType);
    }

    @Transactional(readOnly = true)
    public List<SerialNumberDto> getAllNotUsedSerialNumbers() {
        Type targetListType = new TypeToken<List<SerialNumberDto>>(){}.getType();
        return modelMapper.map(serialNumberRepository.findAllByUsedFalse(), targetListType);
    }

    @Transactional(readOnly = true)
    public SerialNumberDto getSerialNumberById(long id) {
        return modelMapper.map(serialNumberRepository.findById(id), SerialNumberDto.class);
    }

    @Transactional
    public SerialNumberDto updateSerialNumber(Long id, UpdateSerialNumberCommand command) {
        boolean isUsed = false;
        SerialNumber serialNumber = serialNumberRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find serial number")
        );

        Product product = null;
        if (command.getProductId() != null) {
            product = productRepository.findById(command.getProductId()).orElseThrow(
                    () -> new EntityNotFoundException("Cannot find product")
            );
        }

        InventoryItem inventoryItem = null;
        if (command.getInventoryItemId() != null) {
            inventoryItem = inventoryItemRepository.findById(command.getInventoryItemId()).orElseThrow(
                    () -> new EntityNotFoundException("Cannot find inventory item")
            );
        }

        InvoiceItem invoiceItem = null;
        if (command.getInvoiceItemId() != null) {
            invoiceItem = invoiceItemRepository.findById(command.getInventoryItemId()).orElseThrow(
                    () -> new EntityNotFoundException("Cannot find invoice item")
            );
            isUsed = true;
        }
        serialNumber.setSerial(command.getSerial());
        serialNumber.setProduct(product);
        serialNumber.setInventoryItem(inventoryItem);
        serialNumber.setInvoiceItem(invoiceItem);
        serialNumber.setUsed(isUsed);
        return modelMapper.map(serialNumber, SerialNumberDto.class);
    }

    @Transactional
    public SerialNumberDto createSerialNumber(CreateSerialNumberCommand command) {
        SerialNumber serialNumber = new SerialNumber();
        boolean isUsed = false;

        Product product = null;
        if (command.getProductId() != null) {
            product = productRepository.findById(command.getProductId()).orElseThrow(
                    () -> new EntityNotFoundException("Cannot find product")
            );
        }

        InventoryItem inventoryItem = null;
        if (command.getInventoryItemId() != null) {
            inventoryItem = inventoryItemRepository.findById(command.getInventoryItemId()).orElseThrow(
                    () -> new EntityNotFoundException("Cannot find inventory item")
            );
        }

        InvoiceItem invoiceItem = null;
        if (command.getInvoiceItemId() != null) {
            invoiceItem = invoiceItemRepository.findById(command.getInventoryItemId()).orElseThrow(
                    () -> new EntityNotFoundException("Cannot find invoice item")
            );
            isUsed = true;
        }
        serialNumber.setSerial(command.getSerial());
        serialNumber.setProduct(product);
        serialNumber.setInventoryItem(inventoryItem);
        serialNumber.setInvoiceItem(invoiceItem);
        serialNumber.setUsed(isUsed);
        return modelMapper.map(serialNumberRepository.save(serialNumber), SerialNumberDto.class);
    }
}
