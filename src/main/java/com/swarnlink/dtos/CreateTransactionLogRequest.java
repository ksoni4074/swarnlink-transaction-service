package com.swarnlink.dtos;

import java.math.BigDecimal;

public record CreateTransactionLogRequest(
    BigDecimal amount,
    String description
) {}