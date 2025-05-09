package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.model.*;
import dev.lkeleti.invotraxapp.repository.*;
import dev.lkeleti.invotraxapp.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    private VatRateRepository vatRateRepository;
    private ProductCategoryRepository productCategoryRepository;
    private ManufacturerRepository manufacturerRepository;
    private ModelMapper modelMapper;
    private ProductTypeRepository productTypeRepository;
    private BarcodeRepository barcodeRepository;
    private SerialNumberRepository serialNumberRepository;

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        Type targetListType = new TypeToken<List<ProductDto>>(){}.getType();
        return modelMapper.map(productRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable, String searchTerm) {
        Page<Product> partners;
        if (searchTerm == null || searchTerm.isBlank()) {
            partners = productRepository.findAll(pageable);
        } else {
            partners = productRepository.searchByNameOrBarCodeOrSku(searchTerm, pageable);
        }

        return partners.map(partner -> modelMapper.map(partner, ProductDto.class));
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        return modelMapper.map(productRepository.findById(id), ProductDto.class);
    }

    @Transactional
    public ProductDto createProduct(CreateProductCommand command) {
        VatRate vatRate = vatRateRepository.findById(command.getVatRateId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find VAT Rate!")
        );

        ProductCategory productCategory = productCategoryRepository.findById(command.getCategoryId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Product Category!")
        );

        Manufacturer manufacturer = manufacturerRepository.findById(command.getManufacturerId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Manufacturer!")
        );

        ProductType productType = productTypeRepository.findById(command.getProductTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot find Product Type!"));

        Product product = new Product();
        product.setName(command.getName());
        product.setSku(command.getSku());
        product.setDescription(command.getDescription());
        product.setUnit(command.getUnit());
        product.setCategory(productCategory);
        product.setManufacturer(manufacturer);
        product.setProductType(productType);
        product.setNetPurchasePrice(command.getNetPurchasePrice());
        product.setGrossPurchasePrice(command.getGrossPurchasePrice());
        product.setNetSellingPrice(command.getNetSellingPrice());
        product.setGrossSellingPrice(command.getGrossSellingPrice());
        product.setWarrantyPeriodMonths(command.getWarrantyPeriodMonths());
        product.setSerialNumberRequired(command.isSerialNumberRequired());
        product.setStockQuantity(command.getStockQuantity());
        product.setVatRate(vatRate);

        if (command.getBarcodes() != null) {
            for (BarcodeDto barcodeDto : command.getBarcodes()) {
                Barcode barcode = new Barcode();
                barcode.setCode(barcodeDto.getCode());
                barcode.setIsGenerated(barcodeDto.getIsGenerated());
                barcode.setProduct(product);
                barcode = barcodeRepository.save(barcode);
                product.getBarcodes().add(barcode);
            }
        }

        if (command.getSerialNumbers() != null) {
            for (SerialNumberDto serialDto : command.getSerialNumbers()) {
                SerialNumber serial = new SerialNumber();
                serial.setSerial(serialDto.getSerial());
                serial.setUsed(serialDto.isUsed());
                serial.setProduct(product);
                serial = serialNumberRepository.save(serial);
                product.getSerialNumbers().add(serial);
            }
        }

        return modelMapper.map(productRepository.save(product), ProductDto.class);
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductCommand command) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Product!")
        );

        VatRate vatRate = vatRateRepository.findById(command.getVatRateId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find VAT Rate!")
        );

        ProductCategory productCategory = productCategoryRepository.findById(command.getCategoryId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Product Category!")
        );

        Manufacturer manufacturer = manufacturerRepository.findById(command.getManufacturerId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Manufacturer!")
        );

        ProductType productType = productTypeRepository.findById(command.getProductTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot find Product Type!"));

        product.setName(command.getName());
        product.setSku(command.getSku());
        product.setDescription(command.getDescription());
        product.setUnit(command.getUnit());
        product.setCategory(productCategory);
        product.setManufacturer(manufacturer);
        product.setNetPurchasePrice(command.getNetPurchasePrice());
        product.setGrossPurchasePrice(command.getGrossPurchasePrice());
        product.setNetSellingPrice(command.getNetSellingPrice());
        product.setGrossSellingPrice(command.getGrossSellingPrice());
        product.setWarrantyPeriodMonths(command.getWarrantyPeriodMonths());
        product.setSerialNumberRequired(command.isSerialNumberRequired());
        product.setStockQuantity(command.getStockQuantity());
        product.setVatRate(vatRate);
        product.setProductType(productType);
        product.setDeleted(command.isDeleted());

        List<Barcode> existingBarcodes = new ArrayList<>(product.getBarcodes());
        List<BarcodeDto> incomingBarcodes = command.getBarcodes();

        existingBarcodes.forEach(existing -> {
            boolean stillExists = incomingBarcodes.stream()
                    .anyMatch(dto -> dto.getId() != 0 && dto.getId().equals(existing.getId()));
            if (!stillExists) {
                barcodeRepository.delete(existing);
            }
        });

        for (BarcodeDto dto : incomingBarcodes) {
            if (dto.getId() != 0) {
                Barcode existing = existingBarcodes.stream()
                        .filter(b -> b.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    existing.setCode(dto.getCode());
                    existing.setIsGenerated(dto.getIsGenerated());
                }
            } else {
                Barcode newBarcode = new Barcode();
                newBarcode.setCode(dto.getCode());
                newBarcode.setIsGenerated(dto.getIsGenerated());
                newBarcode.setProduct(product);
                barcodeRepository.save(newBarcode);
            }
        }


        List<SerialNumber> existingSerialNumbers = new ArrayList<>(product.getSerialNumbers());
        List<SerialNumberDto> incomingSerialNumbers = command.getSerialNumbers();

        existingSerialNumbers.forEach(existing -> {
            boolean stillExists = incomingSerialNumbers.stream()
                    .anyMatch(dto -> dto.getId() != 0 && dto.getId().equals(existing.getId()));
            if (!stillExists) {
                serialNumberRepository.delete(existing);
            }
        });

        for (SerialNumberDto dto : incomingSerialNumbers) {
            if (dto.getId() != 0) {
                SerialNumber existing = existingSerialNumbers.stream()
                        .filter(s -> s.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    existing.setSerial(dto.getSerial());
                }
            } else {
                SerialNumber newSerialNumber = new SerialNumber();
                newSerialNumber.setSerial(dto.getSerial());
                newSerialNumber.setUsed(false);
                newSerialNumber.setProduct(product);
                serialNumberRepository.save(newSerialNumber);
            }
        }
        return modelMapper.map(productRepository.save(product), ProductDto.class);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Product!")
        );
        product.setDeleted(true);
    }

    @Transactional
    public void unDeleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Product!")
        );
        product.setDeleted(false);
    }

    @Transactional
    public List<ProductDto> getAllActiveProducts() {
        Type targetListType = new TypeToken<List<ProductDto>>(){}.getType();
        return modelMapper.map(productRepository.findByDeletedIsFalse(), targetListType);
    }
}
