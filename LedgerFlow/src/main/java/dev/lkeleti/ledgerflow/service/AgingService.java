package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.AgingRow;
import dev.lkeleti.ledgerflow.model.AgingAccumulator;
import dev.lkeleti.ledgerflow.entity.enums.AgingBucket;
import dev.lkeleti.ledgerflow.entity.Allocation;
import dev.lkeleti.ledgerflow.entity.Invoice;
import dev.lkeleti.ledgerflow.repository.AllocationRepository;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AgingService {

    private final InvoiceRepository invoiceRepository;
    private final AllocationRepository allocationRepository;

    public List<AgingRow> generateAging(LocalDate asOfDate) {

        List<Invoice> invoices = invoiceRepository.findAll();

        // partnerId -> aggregator
        Map<Long, AgingAccumulator> map = new HashMap<>();

        for (Invoice inv : invoices) {

            if (inv.getPartner() == null) continue;

            BigDecimal paid = allocationRepository
                    .findByInvoiceId(inv.getId())
                    .stream()
                    .map(Allocation::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal open = inv.getGrossTotal().subtract(paid);

            if (open.compareTo(BigDecimal.ZERO) <= 0) continue;

            long days = ChronoUnit.DAYS.between(inv.getDueDate(), asOfDate);

            AgingBucket bucket = resolveBucket(days);

            Long partnerId = inv.getPartner().getId();

            AgingAccumulator acc = map.computeIfAbsent(
                    partnerId,
                    k -> new AgingAccumulator(inv.getPartner().getName())
            );

            acc.add(bucket, open);
        }

        return map.entrySet().stream()
                .map(e -> e.getValue().toRow(e.getKey()))
                .toList();
    }

    private AgingBucket resolveBucket(long days) {

        if (days < 0) return AgingBucket.CURRENT;
        if (days <= 30) return AgingBucket.DAYS_0_30;
        if (days <= 60) return AgingBucket.DAYS_31_60;
        if (days <= 90) return AgingBucket.DAYS_61_90;
        return AgingBucket.DAYS_90_PLUS;
    }
}

