package com.swarnlink.dtos;

import com.swarnlink.entity.enums.TransactionDirection;
import com.swarnlink.entity.enums.TransactionType;
import com.swarnlink.entity.enums.UnitType;

import java.math.BigDecimal;
import java.time.LocalDate;

// TransactionResponse.java
public record TransactionResponse(
    Long id,
    Long partyId,
    String partyName,
    TransactionType type,
    TransactionDirection direction,
    BigDecimal totalAmount,
    UnitType unit,
    boolean isSettled,
    LocalDate tentativeCloseDate,
    String description
) {}