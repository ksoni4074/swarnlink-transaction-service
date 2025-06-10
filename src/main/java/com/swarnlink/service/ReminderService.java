package com.swarnlink.service;

import com.swarnlink.dtos.UserInfoDto;
import com.swarnlink.entity.Transaction;
import com.swarnlink.entity.enums.TransactionDirection;
import com.swarnlink.exceptions.BadRequestException;
import com.swarnlink.queue.WhatsAppReminderPublisher;
import com.swarnlink.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final WhatsAppReminderPublisher whatsAppReminderPublisher;
    private final TransactionRepository transactionRepository;

    @Async
    public void sendReminder(Transaction tx, UserInfoDto user) {
        processTx(user.fullName(), user.shopName(), user.phoneNumber(), tx);
    }

    public void sendReminder(Long transactionId, Long userId, String fullName, String shopName, String ownerPhoneNumber) {
        Transaction tx = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        processTx(fullName, shopName, ownerPhoneNumber, tx);
    }

    private void processTx(String fullName, String shopName, String ownerPhoneNumber, Transaction tx) {
        // Determine the recipient based on the direction
        String recipientPhone = tx.getDirection() == TransactionDirection.LEND
                ? tx.getParty().getMobileNumber()  // You lent → send to party
                : ownerPhoneNumber;                // You borrowed → send to yourself

        String message = getMessage(tx, fullName, shopName);

        whatsAppReminderPublisher.sendReminder(recipientPhone, message);
    }

    private String getMessage(Transaction tx, String fullName, String shopName) {
        String partyName = tx.getParty().getName();
        String amount = tx.getTotalAmount().stripTrailingZeros().toPlainString();
        String unit = tx.getUnit().name().toLowerCase(); // rupee / gram
        String type = tx.getType().name().toLowerCase(); // gold / silver / cash
        String date = tx.getCreatedAt().toString();
        String description = tx.getDescription() != null ? tx.getDescription() : "No additional notes";

        boolean isLender = tx.getDirection() == TransactionDirection.LEND;

        if (isLender) {
            // Owner lent money → reminder to party
            return String.format("""
            Hello %s,

            This is a gentle reminder from %s (%s).
            Item Details : %s %s of %s on %s.

            Description: %s

            Please settle it at your earliest convenience. Let us know if you have any questions.

            Thank you!
            """,
                    partyName,
                    fullName,
                    shopName,
                    amount,
                    unit,
                    type,
                    date,
                    description
            );
        } else {
            // Owner borrowed money → reminder to owner, from party
            return String.format("""
            Hello %s,

            This is a reminder from %s.
            You borrowed %s %s of %s from them on %s.

            Description: %s

            Please settle it as soon as possible if pending.

            Thank you!
            """,
                    fullName,
                    partyName,
                    amount,
                    unit,
                    type,
                    date,
                    description
            );
        }
    }
}
