package com.swarnlink.mapper;

import com.swarnlink.dtos.PartyResponse;
import com.swarnlink.dtos.TransactionLogResponse;
import com.swarnlink.dtos.TransactionResponse;
import com.swarnlink.entity.Party;
import com.swarnlink.entity.Transaction;
import com.swarnlink.entity.TransactionLog;

public final class TransactionMapper {

    private TransactionMapper() {
        // Prevent instantiation
    }

    public static PartyResponse toPartyResponse(Party party) {
        return new PartyResponse(
                party.getId(),
                party.getName(),
                party.getMobileNumber(),
                party.getAddress()
        );
    }

    public static TransactionResponse toTransactionResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getParty().getId(),
                tx.getParty().getName(),
                tx.getType(),
                tx.getDirection(),
                tx.getTotalAmount(),
                tx.getUnit(),
                tx.isSettled(),
                tx.getTentativeCloseDate(),
                tx.getDescription()
        );
    }

    public static TransactionLogResponse toLogResponse(TransactionLog log) {
        return new TransactionLogResponse(
                log.getId(),
                log.getTransaction().getId(),
                log.getAmount(),
                log.getLogDate(),
                log.getDescription()
        );
    }
}