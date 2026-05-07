package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.report.OpenItemDto;
import dev.lkeleti.ledgerflow.dto.report.PartnerLedgerRow;
import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.repository.AllocationRepository;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerLedgerService {

    private final InvoiceRepository invoiceRepository;
    private final AllocationRepository allocationRepository;

    public List<PartnerLedgerRow> getPartnerLedger(Long partnerId) {

        List<Invoice> invoices = invoiceRepository.findByPartnerId(partnerId);

        List<PartnerLedgerRow> rows = new ArrayList<>();

        for (Invoice inv : invoices) {

            // 1. számla (terhelés)
            rows.add(new PartnerLedgerRow(
                    inv.getDueDate(),
                    inv.getInvoiceNumber(),
                    "Számla",
                    inv.getGrossTotal(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));

            // 2. kiegyenlítések
            List<Allocation> allocations =
                    allocationRepository.findByInvoiceId(inv.getId());

            for (Allocation a : allocations) {

                rows.add(new PartnerLedgerRow(
                        a.getMoneyTransaction().getDate(),
                        "TX-" + a.getMoneyTransaction().getId(),
                        "Kiegyenlítés",
                        BigDecimal.ZERO,
                        a.getAmount(),
                        BigDecimal.ZERO
                ));
            }
        }

        // 3. rendezés
        rows.sort(Comparator.comparing(PartnerLedgerRow::getDate));

        // 4. futó egyenleg
        BigDecimal balance = BigDecimal.ZERO;

        List<PartnerLedgerRow> result = new ArrayList<>();

        for (PartnerLedgerRow r : rows) {

            balance = balance
                    .add(r.getDebit())
                    .subtract(r.getCredit());

            result.add(new PartnerLedgerRow(
                    r.getDate(),
                    r.getReference(),
                    r.getDescription(),
                    r.getDebit(),
                    r.getCredit(),
                    balance
            ));
        }

        return result;
    }

    public List<OpenItemDto> getOpenItems(Long partnerId) {

        List<Invoice> invoices = invoiceRepository.findByPartnerId(partnerId);

        return invoices.stream()
                .map(inv -> {

                    BigDecimal paid = allocationRepository
                            .findByInvoiceId(inv.getId())
                            .stream()
                            .map(Allocation::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal open = inv.getGrossTotal().subtract(paid);

                    return new OpenItemDto(
                            inv.getInvoiceNumber(),
                            inv.getDueDate(),
                            inv.getGrossTotal(),
                            paid,
                            open
                    );
                })
                .filter(i -> i.getOpenAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }
}