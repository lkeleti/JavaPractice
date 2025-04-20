package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.repository.VatRateRepository;
import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.model.Manufacturer;
import dev.lkeleti.invotraxapp.model.Product;
import dev.lkeleti.invotraxapp.model.ProductCategory;
import dev.lkeleti.invotraxapp.model.VatRate;
import dev.lkeleti.invotraxapp.repository.ManufacturerRepository;
import dev.lkeleti.invotraxapp.repository.ProductCategoryRepository;
import dev.lkeleti.invotraxapp.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
        Product product = new Product(
                command.getName(),
                command.getSku(),
                command.getDescription(),
                productCategory,
                manufacturer,
                command.getNetPrice(),
                command.getGrossPrice(),
                command.getWarrantyPeriodMonths(),
                command.isSerialNumberRequired(),
                new ArrayList<>(),
                new ArrayList<>(),
                command.getStockQuantity(),
                vatRate
        );
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
        product.setCategory(productCategory);
        product.setManufacturer(manufacturer);
        product.setNetPrice(command.getNetPrice());
        product.setGrossPrice(command.getGrossPrice());
        product.setWarrantyPeriodMonths(command.getWarrantyPeriodMonths());
        product.setSerialNumberRequired(command.isSerialNumberRequired());
        product.setStockQuantity(command.getStockQuantity());
        product.setVatRate(vatRate);
        product.setDeleted(command.isDeleted());
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
