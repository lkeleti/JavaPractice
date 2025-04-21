package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateProductTypeCommand;
import dev.lkeleti.invotraxapp.dto.ProductTypeDto;
import dev.lkeleti.invotraxapp.dto.UpdateProductTypeCommand;
import dev.lkeleti.invotraxapp.model.ProductType;
import dev.lkeleti.invotraxapp.repository.ProductTypeRepository;
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
public class ProductTypeService {
    private ProductTypeRepository productTypeRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ProductTypeDto> getAllProductTypes() {
        Type targetListType = new TypeToken<List<ProductTypeDto>>(){}.getType();
        return modelMapper.map(productTypeRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public ProductTypeDto getProductTypeById(long id) {
        ProductType productType = productTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find product type code")
        );
        return modelMapper.map(productType, ProductTypeDto.class);
    }

    @Transactional
    public ProductTypeDto updateProductType(Long id, UpdateProductTypeCommand command) {
        ProductType productType = productTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find product type code")
        );
        productType.setName(command.getName());
        productType.setManagesStock(command.isManagesStock());
        return modelMapper.map(productType, ProductTypeDto.class);
    }

    @Transactional
    public ProductTypeDto createProductType(CreateProductTypeCommand command) {
        ProductType productType = new ProductType();
        productType.setName(command.getName());
        productType.setManagesStock(command.isManagesStock());
        return modelMapper.map(productTypeRepository.save(productType), ProductTypeDto.class);
    }
}
