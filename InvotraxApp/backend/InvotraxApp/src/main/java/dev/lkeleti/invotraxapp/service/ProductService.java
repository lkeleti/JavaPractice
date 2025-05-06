package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.model.*;
import dev.lkeleti.invotraxapp.repository.VatRateRepository;
import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.repository.ManufacturerRepository;
import dev.lkeleti.invotraxapp.repository.ProductCategoryRepository;
import dev.lkeleti.invotraxapp.repository.ProductRepository;
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

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    private VatRateRepository vatRateRepository;
    private ProductCategoryRepository productCategoryRepository;
    private ManufacturerRepository manufacturerRepository;
    private ModelMapper modelMapper;

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
        Product product = new Product();
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
        product.setBarcodes(new ArrayList<>());
        product.setSerialNumbers(new ArrayList<>());
        product.setStockQuantity(command.getStockQuantity());
        product.setVatRate(vatRate);

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

        return modelMapper.map(product, ProductDto.class);
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
