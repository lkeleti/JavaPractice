package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.SerialNumberDetailsDto;
import dev.lkeleti.invotraxapp.model.Product;
import dev.lkeleti.invotraxapp.model.SerialNumber;
import dev.lkeleti.invotraxapp.repository.SerialNumberRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
    import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@AllArgsConstructor
@Server
public class SerialNumberService {
    private SerialNumberRepository serialNumberRepository;

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
}
