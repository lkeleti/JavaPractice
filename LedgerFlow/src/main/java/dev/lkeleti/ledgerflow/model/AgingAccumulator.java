package dev.lkeleti.ledgerflow.model;

import dev.lkeleti.ledgerflow.dto.report.AgingRow;
import dev.lkeleti.ledgerflow.entity.enums.AgingBucket;

import java.math.BigDecimal;

public class AgingAccumulator {

    private final String partnerName;

    private BigDecimal current = BigDecimal.ZERO;
    private BigDecimal d0_30 = BigDecimal.ZERO;
    private BigDecimal d31_60 = BigDecimal.ZERO;
    private BigDecimal d61_90 = BigDecimal.ZERO;
    private BigDecimal d90 = BigDecimal.ZERO;

    public AgingAccumulator(String partnerName) {
        this.partnerName = partnerName;
    }

    public void add(AgingBucket bucket, BigDecimal amount) {

        switch (bucket) {
            case CURRENT -> current = current.add(amount);
            case DAYS_0_30 -> d0_30 = d0_30.add(amount);
            case DAYS_31_60 -> d31_60 = d31_60.add(amount);
            case DAYS_61_90 -> d61_90 = d61_90.add(amount);
            case DAYS_90_PLUS -> d90 = d90.add(amount);
        }
    }

    public AgingRow toRow(Long partnerId) {

        BigDecimal total = current
                .add(d0_30)
                .add(d31_60)
                .add(d61_90)
                .add(d90);

        return new AgingRow(
                partnerId,
                partnerName,
                current,
                d0_30,
                d31_60,
                d61_90,
                d90,
                total
        );
    }
}