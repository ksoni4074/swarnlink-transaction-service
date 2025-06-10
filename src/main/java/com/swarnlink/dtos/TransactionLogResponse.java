package com.swarnlink.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

// TransactionLogResponse.java
public record TransactionLogResponse(
    Long id,
    Long transactionId,
    BigDecimal amount,
    LocalDate logDate,
    String description
) {}