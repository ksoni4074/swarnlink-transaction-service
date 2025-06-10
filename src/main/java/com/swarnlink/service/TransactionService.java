package com.swarnlink.service;

import com.swarnlink.dtos.*;
import com.swarnlink.entity.Party;
import com.swarnlink.entity.Transaction;
import com.swarnlink.entity.TransactionLog;
import com.swarnlink.entity.enums.TransactionType;
import com.swarnlink.entity.enums.UnitType;
import com.swarnlink.exceptions.BadRequestException;
import com.swarnlink.feign.UserFeignClient;
import com.swarnlink.mapper.TransactionMapper;
import com.swarnlink.queue.WhatsAppReminderPublisher;
import com.swarnlink.repository.PartyRepository;
import com.swarnlink.repository.TransactionLogRepository;
import com.swarnlink.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final ReminderService reminderService;
    private final PartyRepository partyRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLogRepository logRepository;
    private final UserFeignClient userFeignClient;

    public PartyResponse createParty(CreatePartyRequest request, Long userId) {
        Party party = new Party();
        party.setUserId(userId);
        party.setName(request.name());
        party.setMobileNumber(request.mobile());
        party.setAddress(request.address());
        return TransactionMapper.toPartyResponse(partyRepository.save(party));
    }

    public List<PartyResponse> getAllParties(Long userId) {
        return partyRepository.findByUserId(userId).stream()
                .map(x -> TransactionMapper.toPartyResponse(x))
                .toList();
    }

    public TransactionResponse createTransaction(CreateTransactionRequest request, Long userId) {
        Party party = partyRepository.findByIdAndUserId(request.partyId(), userId)
                .orElseThrow(() -> new BadRequestException("Party not found"));

        boolean isValid =
                (request.type() == TransactionType.CASH && request.unit() == UnitType.RUPEE) ||
                        ((request.type() == TransactionType.GOLD || request.type() == TransactionType.SILVER)
                                && request.unit() == UnitType.GRAM);

        if (!isValid) {
            throw new BadRequestException("Invalid unit for transaction type: " +
                    request.type() + " should use " +
                    (request.type() == TransactionType.CASH ? "RUPEE" : "GRAM"));
        }

        Transaction tx = new Transaction();
        tx.setUserId(userId);
        tx.setParty(party);
        tx.setType(request.type());
        tx.setDirection(request.direction());
        tx.setUnit(request.unit());
        tx.setTotalAmount(request.amount());
        tx.setDescription(request.description());
        tx.setTentativeCloseDate(request.tentativeCloseDate());

        return TransactionMapper.toTransactionResponse(transactionRepository.save(tx));
    }

    public TransactionLogResponse addTransactionLog(Long transactionId, CreateTransactionLogRequest request, Long userId) {
        Transaction tx = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new BadRequestException("Transaction not found"));
        if(tx.isSettled()){
            throw new BadRequestException("Transaction is Already Settled.");
        }
        var totalTxAmount = tx.getTotalAmount();
        var totalReceivedTillNow = logRepository.sumLogsByTransaction(transactionId);
        var pending = totalTxAmount.subtract(totalReceivedTillNow);
        if(request.amount().doubleValue() > pending.doubleValue()){
            throw new BadRequestException("Pending Amount is Less than Provided "+ pending);
        }

        TransactionLog log = new TransactionLog();
        log.setTransaction(tx);
        log.setAmount(request.amount());
        log.setDescription(request.description());
        logRepository.save(log);

        var totalLogged = logRepository.sumLogsByTransaction(transactionId);
        if (totalLogged.compareTo(tx.getTotalAmount()) >= 0) {
            tx.setSettled(true);
            transactionRepository.save(tx);
        }

        return TransactionMapper.toLogResponse(log);
    }

    public List<TransactionResponse> getAllTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(x -> TransactionMapper.toTransactionResponse(x))
                .toList();
    }

    public List<TransactionLogResponse> getLogsForTransaction(Long transactionId, Long userId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new BadRequestException("Transaction not found"));
        return logRepository.findByTransactionId(transactionId).stream()
                .map(x -> TransactionMapper.toLogResponse(x))
                .toList();
    }

    public String remindDueTxns(Long userId) {
        processUnsettledTransactions(transactionRepository.findByUserId(userId),30);
        return "Sent";
    }


    public void processUnsettledTransactions(List<Transaction> dueTxs, int batchSize) {
        Map<Long, List<Transaction>> userTxMap = new HashMap<>();
        for (Transaction tx : dueTxs) {
            userTxMap.computeIfAbsent(tx.getUserId(), k -> new ArrayList<>()).add(tx);
        }

        Map<Long, UserInfoDto> userDetailsMap = userFeignClient.getUserInfo(userTxMap.keySet()).stream()
                .collect(Collectors.toMap(UserInfoDto::id, Function.identity()));

        for (Long userId : userDetailsMap.keySet()) {
            userTxMap.get(userId).forEach(tx  -> reminderService.sendReminder(tx,userDetailsMap.get(userId)));
        }


        /* List<Set<Long>> batches = createBatches(userTxMap.keySet(),batchSize);


        batches.forEach(userIdBatch -> {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

                var usersTask = scope.fork(() -> {
                    try {
                        return userFeignClient.getUserInfo(userIdBatch).stream()
                                .collect(Collectors.toMap(UserInfoDto::id, Function.identity()));
                    } catch (Exception e) {
                        log.error("Failed to fetch user info for userIds: {}", userIdBatch, e);
                        throw e;
                    }
                });

                var reminderTask = scope.fork(() -> {
                    try {
                        Map<Long, UserInfoDto> userDetailsMap = usersTask.get();

                        for (Long userId : userIdBatch) {
                            UserInfoDto user = userDetailsMap.get(userId);
                            if (user == null) continue;

                            List<Transaction> txList = userTxMap.getOrDefault(userId, List.of());
                            for (Transaction tx : txList) {
                                try {
                                    reminderService.sendReminder(tx, user);
                                } catch (Exception e) {
                                    log.error("Reminder failed for tx: {} and user: {}", tx.getId(), user.id(), e);
                                    throw e;
                                }
                            }
                        }
                        return null;
                    } catch (Exception e) {
                        log.error("Reminder task failed for userIdBatch: {}", userIdBatch, e);
                        throw e;
                    }
                });

                scope.join();              // Wait for both tasks
                scope.throwIfFailed();     // Propagate failure if any

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Reminder batch interrupted", e);
            } catch (Exception e) {
                log.error("Error while sending reminders", e);
            }
        });*/
    }

    private List<Set<Long>> createBatches(Set<Long> userIds, int batchSize) {
        List<Set<Long>> batches = new ArrayList<>();
        List<Long> idList = new ArrayList<>(userIds);

        for (int i = 0; i < idList.size(); i += batchSize) {
            Set<Long> batch = new HashSet<>(idList.subList(i, Math.min(i + batchSize, idList.size())));
            batches.add(batch);
        }

        return batches;
    }
}