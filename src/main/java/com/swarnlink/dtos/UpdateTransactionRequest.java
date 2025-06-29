package com.swarnlink.dtos;

import com.swarnlink.entity.enums.TransactionDirection;
import com.swarnlink.entity.enums.TransactionType;
import com.swarnlink.entity.enums.UnitType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
        Long transactionId,
    BigDecimal amount,
    String description,
    LocalDate tentativeCloseDate
) {}