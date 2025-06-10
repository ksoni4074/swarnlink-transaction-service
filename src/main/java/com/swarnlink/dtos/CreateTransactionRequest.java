package com.swarnlink.dtos;

import com.swarnlink.entity.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
    Long partyId,
    TransactionType type,
    TransactionDirection direction,
    UnitType unit,
    BigDecimal amount,
    String description,
    LocalDate tentativeCloseDate
) {}