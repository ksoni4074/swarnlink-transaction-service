package com.swarnlink.repository;

import com.swarnlink.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIdAndUserId(Long transactionId,Long userId);

    List<Transaction> findByUserId(Long userId);

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.isSettled = false
      AND t.tentativeCloseDate IS NOT NULL
      AND t.tentativeCloseDate <= :date
""")
    List<Transaction> findUnsettledTransactionsDueBy(LocalDate date);

    List<Transaction> findByUserIdAndIsSettled(Long userId, Boolean isSettled);

    @Query("""
      SELECT t FROM Transaction t
      WHERE t.userId = :userId
        AND t.isSettled = :settled
        AND (
              LOWER(t.party.name)        LIKE LOWER(CONCAT('%', :q, '%'))
           OR t.party.mobileNumber       LIKE        CONCAT('%', :q, '%')
        )
    """)
    List<Transaction> searchByParty(Long userId,
                                    boolean settled,
                                    String q);

}