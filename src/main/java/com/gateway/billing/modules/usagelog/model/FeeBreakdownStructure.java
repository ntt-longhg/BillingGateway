package com.gateway.billing.modules.usagelog.model;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;
@Data
public class FeeBreakdownStructure {
    private String strategy;
    private BigDecimal initialFeeApplied;
    private BigDecimal subsequentFeeApplied;
    private Map<String, Object> rawCalculationDetails;
}
