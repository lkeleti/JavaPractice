package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateInvoiceCommand;
import dev.lkeleti.invotraxapp.dto.InvoiceDto;
import dev.lkeleti.invotraxapp.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class InvoiceController {

    private InvoiceService invoiceService;

    @GetMapping
    public List<InvoiceDto> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public InvoiceDto getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PostMapping
    public InvoiceDto createInvoice(@RequestBody CreateInvoiceCommand createInvoiceCommand) {
        return invoiceService.createInvoice(createInvoiceCommand);
    }

}
