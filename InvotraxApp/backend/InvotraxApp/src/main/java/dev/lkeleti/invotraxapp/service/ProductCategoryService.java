package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateProductCategoryCommand;
import dev.lkeleti.invotraxapp.dto.ProductCategoryDto;
import dev.lkeleti.invotraxapp.dto.UpdateProductCategoryCommand;
import dev.lkeleti.invotraxapp.model.ProductCategory;
import dev.lkeleti.invotraxapp.repository.ProductCategoryRepository;
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
public class ProductCategoryService {
    private ProductCategoryRepository productCategoryRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getAllProductCategories() {
        Type targetListType = new TypeToken<List<ProductCategoryDto>>(){}.getType();
        return modelMapper.map(productCategoryRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public ProductCategoryDto getProductCategoryById(Long id) {
        ProductCategory productCategory = productCategoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find product category")
        );
        return modelMapper.map(productCategory, ProductCategoryDto.class);
    }

    @Transactional
    public ProductCategoryDto createProductCategory(CreateProductCategoryCommand command) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName(command.getName());
        return modelMapper.map(productCategory, ProductCategoryDto.class);
    }

    @Transactional
    public void deleteProductCategory(Long id) {
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product category not found"));
        productCategoryRepository.delete(productCategory);
    }

    @Transactional
    public ProductCategoryDto updateProductCategory(long id, UpdateProductCategoryCommand command) {
        ProductCategory productCategory = productCategoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find product category")
        );

        productCategory.setName(command.getName());
        return modelMapper.map(productCategory, ProductCategoryDto.class);
    }
}
