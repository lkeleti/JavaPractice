package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.response.CompanyResponse;
import dev.lkeleti.ledgerflow.entity.*;
import dev.lkeleti.ledgerflow.entity.enums.MatchType;
import dev.lkeleti.ledgerflow.model.MatchingResult;
import dev.lkeleti.ledgerflow.repository.AllocationRepository;
import dev.lkeleti.ledgerflow.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MatchingService {

    private final InvoiceRepository invoiceRepository;
    private final AllocationRepository allocationRepository;
    private final CompanyService companyService;

    public MatchingResult match(BankStatementLine line) {

        CompanyResponse company = companyService.get();
        LocalDate closed = company.getClosedAccountingPeriod().plusDays(1);

        // 1. Banki sor dátuma lezárt időszakban?
        if (line.getDate().isBefore(closed)) {
            return new MatchingResult(MatchType.NONE, List.of(), BigDecimal.ZERO);
        }

        String text = normalize(
                (line.getPartnerNameRaw() == null ? "" : line.getPartnerNameRaw()) + " " +
                        (line.getDescription() == null ? "" : line.getDescription())
        );

        BigDecimal bankAmount = line.getAmount().abs();

        // 1. Nyitott számlák
        List<Invoice> openInvoices = invoiceRepository.findOpenInvoices();

        // 2. Partner szűrés
        List<Invoice> partnerMatches = openInvoices.stream()
                .filter(inv -> inv.getPartner() != null)
                .filter(inv -> text.contains(normalize(inv.getPartner().getName())))
                .toList();

        if (partnerMatches.isEmpty()) {
            return new MatchingResult(MatchType.NONE, List.of(), BigDecimal.ZERO);
        }

        // 3. Rendezzük (nagy -> kicsi)
        partnerMatches = partnerMatches.stream()
                .sorted((a, b) -> getOpenAmount(b).compareTo(getOpenAmount(a)))
                .toList();

        // 4. EXACT MATCH
        List<Invoice> exactMatches = partnerMatches.stream()
                .filter(inv -> getOpenAmount(inv).compareTo(bankAmount) == 0)
                .toList();

        if (exactMatches.size() == 1) {
            return new MatchingResult(MatchType.AUTO, exactMatches, BigDecimal.ONE);
        }

        if (exactMatches.size() > 1) {
            return new MatchingResult(MatchType.MULTIPLE, exactMatches, BigDecimal.valueOf(0.9));
        }

        // 5. SUBSET MATCH
        List<List<Invoice>> subsets = findMatchingSubsets(partnerMatches, bankAmount);

        if (subsets.size() == 1) {
            return new MatchingResult(
                    MatchType.AUTO,
                    subsets.getFirst(),
                    BigDecimal.valueOf(0.95)
            );
        }

        if (subsets.size() > 1) {
            List<Invoice> all = subsets.stream()
                    .flatMap(List::stream)
                    .distinct()
                    .toList();

            return new MatchingResult(
                    MatchType.MULTIPLE,
                    all,
                    BigDecimal.valueOf(0.7)
            );
        }

        // 6. fallback (partner egyezés van, de nincs összeg egyezés)
        return new MatchingResult(
                MatchType.MULTIPLE,
                partnerMatches,
                BigDecimal.valueOf(0.5)
        );
    }

    // =============================
    // SUBSET MATCHING
    // =============================

    private List<List<Invoice>> findMatchingSubsets(
            List<Invoice> invoices,
            BigDecimal targetAmount
    ) {
        List<List<Invoice>> results = new ArrayList<>();

        backtrack(invoices, targetAmount, 0,
                new ArrayList<>(), BigDecimal.ZERO, results);

        return results;
    }

    private void backtrack(
            List<Invoice> invoices,
            BigDecimal target,
            int index,
            List<Invoice> current,
            BigDecimal sum,
            List<List<Invoice>> results
    ) {

        if (sum.compareTo(target) == 0) {
            results.add(new ArrayList<>(current));
            return;
        }

        if (sum.compareTo(target) > 0 || index >= invoices.size()) {
            return;
        }

        // limit (nagyon fontos védelem!)
        if (current.size() >= 5) {
            return;
        }

        Invoice inv = invoices.get(index);
        BigDecimal open = getOpenAmount(inv);

        // include
        current.add(inv);
        backtrack(invoices, target, index + 1,
                current, sum.add(open), results);

        current.removeLast();

        // exclude
        backtrack(invoices, target, index + 1,
                current, sum, results);
    }

    // =============================
    // HELPER
    // =============================

    private BigDecimal getOpenAmount(Invoice invoice) {

        BigDecimal paid = allocationRepository.findByInvoiceId(invoice.getId())
                .stream()
                .map(Allocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return invoice.getGrossTotal().subtract(paid);
    }

    private String normalize(String input) {

        if (input == null) return "";

        String normalized = input.toLowerCase();

        normalized = normalized
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ö", "o")
                .replace("ő", "o")
                .replace("ú", "u")
                .replace("ü", "u")
                .replace("ű", "u");

        return normalized;
    }
}