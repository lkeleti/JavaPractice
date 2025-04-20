package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.ProductDto;
import dev.lkeleti.invotraxapp.dto.UpdateProductCommand;
import dev.lkeleti.invotraxapp.dto.CreateProductCommand;
import dev.lkeleti.invotraxapp.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class ProductController {
    private ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/active")
    public List<ProductDto> getAllActiveProducts() {
        return productService.getAllActiveProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody CreateProductCommand command) {
        return productService.createProduct(command);
    }

    @PutMapping("/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @RequestBody UpdateProductCommand command) {
        return productService.updateProduct(id, command);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @DeleteMapping("/undelete/{id}")
    public void unDeleteProduct(@PathVariable Long id) {
        productService.unDeleteProduct(id);
    }
}

