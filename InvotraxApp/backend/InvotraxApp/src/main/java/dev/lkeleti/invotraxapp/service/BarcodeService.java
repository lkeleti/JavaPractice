package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.BarcodeDto;
import dev.lkeleti.invotraxapp.dto.CreateBarcodeCommand;
import dev.lkeleti.invotraxapp.model.Barcode;
import dev.lkeleti.invotraxapp.model.Product;
import dev.lkeleti.invotraxapp.repository.BarcodeRepository;
import dev.lkeleti.invotraxapp.repository.ProductRepository;
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
public class BarcodeService {
    private BarcodeRepository barcodeRepository;
    private ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<BarcodeDto> getAllBarcodes() {
        Type targetListType = new TypeToken<List<BarcodeDto>>(){}.getType();
        return modelMapper.map(barcodeRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public BarcodeDto getBarcodeByCode(String code) {
        if (code.length() == 12) {
            code = "0" + code;
        }
        return modelMapper.map(barcodeRepository.findByCode(code), BarcodeDto.class);
    }

    @Transactional
    public BarcodeDto createBarcode(CreateBarcodeCommand command) {
        if (command.getCode().length() == 12) {
            command.setCode("0" + command.getCode());
        }

        if (!isValidEAN13Barcode(command.getCode())) {
            throw new IllegalStateException("Invalid EAN code");
        }

        Product product = productRepository.findById(command.getProductId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find product")
        );

        Barcode barcode = new Barcode();
        barcode.setCode(command.getCode());
        barcode.setIsGenerated(command.getIsGenerated());
        barcode.setProduct(product);
        return modelMapper.map(barcode, BarcodeDto.class);
    }

    @Transactional
    public void deleteBarcode(Long id) {
        barcodeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public String generateBarcode() {
        String prefix = "9999";
        String maxBarcode = barcodeRepository.findHighestGeneratedBarcode().orElse("9999100000007");
        long lastNumber = Long.parseLong(maxBarcode);
        long newNumber = lastNumber - 9999000000000L + 1;

        while (!isValidEAN13Barcode(prefix + newNumber)) {
            newNumber++;
        }
        return prefix + newNumber;
    }

    private boolean isValidEAN13Barcode(String barcode) {
        if (barcode.length() != 13 || !barcode.matches("\\d+")) {
            return false;
        }
        return passesEAN13Checksum(barcode);
    }

    private boolean passesEAN13Checksum(String barcode) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(barcode.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checksum = (10 - (sum % 10)) % 10;
        return checksum == Character.getNumericValue(barcode.charAt(12));
    }
}
