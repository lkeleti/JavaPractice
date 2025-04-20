package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateInvoiceCommand;
import dev.lkeleti.invotraxapp.dto.InvoiceDto;
import dev.lkeleti.invotraxapp.model.*;
import dev.lkeleti.invotraxapp.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvoiceService {
    private InvoiceRepository invoiceRepository;
    private PartnerRepository partnerRepository;
    private InvoiceNumberSequenceRepository sequenceRepository;
    private ProductRepository productRepository;
    private InvoiceTypeRepository invoiceTypeRepository;
    private PaymentMethodRepository paymentMethodRepository;
    private PdfInvoiceGeneratorService pdfInvoiceGeneratorService;
    private ModelMapper modelMapper;
    private final ZipCodeRepository zipCodeRepository;


    @Transactional(readOnly = true)
    public List<InvoiceDto> getAllInvoices() {
        Type targetListType = new TypeToken<List<InvoiceDto>>(){}.getType();
        return modelMapper.map(invoiceRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
        InvoiceDto invoiceDto = modelMapper.map(invoice, InvoiceDto.class);
        invoiceDto.setPdfContentBase64(getUnprotectedInvoicePdfBase64(invoice));
        return invoiceDto;
    }

    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceCommand createInvoiceCommand) {
        byte[] invoicePdf;
        Partner seller =partnerRepository.findById(createInvoiceCommand.getSellerId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Seller!")
        );

        Partner buyer =partnerRepository.findById(createInvoiceCommand.getBuyerId()).orElseThrow(
                () -> new EntityNotFoundException("Cannot find Buyer!")
        );

        Invoice invoice = new Invoice();
        invoice.setSeller(seller);
        invoice.setBuyer(buyer);
        invoice.setIssuedAt(createInvoiceCommand.getIssuedAt());
        invoice.setFulfillmentAt(createInvoiceCommand.getFulfillmentAt());
        invoice.setDueDate(createInvoiceCommand.getDueDate());

        InvoiceType invoiceType = invoiceTypeRepository.findById(createInvoiceCommand.getInvoiceTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Invoice type not found"));
        PaymentMethod paymentMethod = paymentMethodRepository.findByCode(createInvoiceCommand.getPaymentMethodCode())
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found"));

        ZipCode zipCodeSeller = zipCodeRepository.findById(createInvoiceCommand.getSellerId())
                .orElseThrow(() -> new EntityNotFoundException("Seller zip code not found"));

        ZipCode zipCodeBuyer = zipCodeRepository.findById(createInvoiceCommand.getBuyerId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer zip code not found"));

        invoice.setInvoiceType(invoiceType);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setNetTotal(createInvoiceCommand.getNetTotal());
        invoice.setGrossTotal(createInvoiceCommand.getGrossTotal());
        invoice.setInvoiceNumber(generateInvoiceNumber(createInvoiceCommand.getInvoiceTypeId()));
        invoice.setItems(getInvoiceItems(createInvoiceCommand));

        String pdfPassword = UUID.randomUUID().toString().replace("-", "");
        invoicePdf = pdfInvoiceGeneratorService.generateInvoicePdf(invoice, zipCodeSeller, zipCodeBuyer, pdfPassword);

        String fileName = invoice.getInvoiceNumber() + ".pdf";
        saveInvoicePdfToFile(fileName, invoicePdf);
        invoice.setPdfPath(fileName);
        invoice.setPdfPassword(pdfPassword);

        InvoiceDto invoiceDto = modelMapper.map(invoiceRepository.save(invoice), InvoiceDto.class);
        byte[] decryptedPdf = pdfInvoiceGeneratorService.decryptPdfToMemory(invoicePdf, pdfPassword);
        invoiceDto.setPdfContentBase64(Base64.getEncoder().encodeToString(decryptedPdf));
        return invoiceDto;
    }

    private List<InvoiceItem> getInvoiceItems(CreateInvoiceCommand createInvoiceCommand) {
        return createInvoiceCommand.getItems().stream()
                .map(itemDto -> {
                    Product product = productRepository.findById(itemDto.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                    if (product.getProductType().isManagesStock()) {
                        int remainingStock = product.getStockQuantity() - itemDto.getQuantity();
                        if (remainingStock < 0) {
                            throw new IllegalStateException("Not enough stock for product: " + product.getName());
                        }
                        product.setStockQuantity(remainingStock);
                    }

                    InvoiceItem item = new InvoiceItem();
                    item.setProduct(product);
                    item.setQuantity(itemDto.getQuantity());
                    item.setUnit(itemDto.getUnit());
                    item.setUnitPrice(itemDto.getUnitPrice());
                    item.setDiscountPercent(itemDto.getDiscountPercent());
                    item.setNetAmount(itemDto.getNetAmount());
                    item.setGrossAmount(itemDto.getGrossAmount());
                    item.setVatRate(itemDto.getVatRate());

                    return item;
                })
                .toList();
    }

    public String generateInvoiceNumber(Long invoiceTypeId) {
        InvoiceType invoiceType = invoiceTypeRepository.findById(invoiceTypeId)
                .orElseThrow(() -> new EntityNotFoundException("InvoiceType not found"));

        InvoiceNumberSequence sequence = sequenceRepository.findByInvoiceType(invoiceType);
        if (sequence == null) {
            sequence = new InvoiceNumberSequence();
            sequence.setInvoiceType(invoiceType);
            sequence.setLastNumber(0);
        }

        int newNumber = sequence.getLastNumber() + 1;
        sequence.setLastNumber(newNumber);
        sequenceRepository.save(sequence);
        String prefix = sequence.getInvoicePrefix();

        return String.format("%s%06d", prefix, newNumber);
    }

    public void saveInvoicePdfToFile(String fileName, byte[] pdfBytes) {
        String basePath = "/app/invoices";
        Path filePath = Paths.get(basePath, fileName);

        try {
            // Mappa létrehozása, ha nem létezik
            Files.createDirectories(filePath.getParent());
            // Fájl írása
            Files.write(filePath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot save invoice to server",e);
        }
    }

    public String getUnprotectedInvoicePdfBase64(Invoice invoice) {
        byte[] encryptedPdf;
        try {
            encryptedPdf = Files.readAllBytes(Paths.get("/app/invoices" + invoice.getPdfPath()));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read encrypted pdf", e);
        }
        byte[] decryptedPdf = pdfInvoiceGeneratorService.decryptPdfToMemory(encryptedPdf, invoice.getPdfPassword());
        return Base64.getEncoder().encodeToString(decryptedPdf);
    }
}
