package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.BarcodeDto;
import dev.lkeleti.invotraxapp.dto.CreateBarcodeCommand;
import dev.lkeleti.invotraxapp.service.BarcodeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barcodes")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class BarcodeController {
    private BarcodeService barcodeService;

    @GetMapping
    public List<BarcodeDto> getAllBarcodes() {
        return barcodeService.getAllBarcodes();
    }

    @GetMapping("/{code}")
    public BarcodeDto getBarcodeByCode(@PathVariable String code) {
        return barcodeService.getBarcodeByCode(code);
    }

    @PutMapping
    public String generateBarcode() {
        return barcodeService.generateBarcode();
    }

    @PostMapping
    public BarcodeDto createBarcode(@RequestBody CreateBarcodeCommand command) {
        return barcodeService.createBarcode(command);
    }

    @DeleteMapping("/{id}")
    public void deleteBarcode(@PathVariable Long id) {
        barcodeService.deleteBarcode(id);
    }
}
