package com.swarnlink.repository;

import com.swarnlink.entity.TransactionLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {

    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM TransactionLog l WHERE l.transaction.id = :transactionId")
    BigDecimal sumLogsByTransaction(@Param("transactionId") Long transactionId);

    List<TransactionLog> findByTransactionId(Long transactionId);
}