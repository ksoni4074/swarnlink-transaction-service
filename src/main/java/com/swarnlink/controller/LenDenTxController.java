package com.swarnlink.controller;

import com.swarnlink.config.JwtUserPrincipal;
import com.swarnlink.dtos.*;
import com.swarnlink.entity.enums.TransactionDirection;
import com.swarnlink.entity.enums.TransactionType;
import com.swarnlink.service.ReminderService;
import com.swarnlink.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class LenDenTxController {

    private final TransactionService transactionService;
    private final ReminderService reminderService;

    @PostMapping("/parties/create")
    public ResponseEntity<PartyResponse> createParty(@RequestBody CreatePartyRequest request,
                                             @AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(transactionService.createParty(request,principal.getUserId()));
    }

    @GetMapping("/parties")
    public ResponseEntity<List<PartyResponse>> getParties(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(transactionService.getAllParties(principal.getUserId()));
    }

    @GetMapping("/types")
    public ResponseEntity<List<TransactionType>> getTransactionTypes() {
        return ResponseEntity.ok(Arrays.stream(TransactionType.values()).toList());
    }

    @GetMapping("/directions")
    public ResponseEntity<List<TransactionDirection>> getTransactionDirections() {
        return ResponseEntity.ok(Arrays.stream(TransactionDirection.values()).toList());
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest request,@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(transactionService.createTransaction(request, principal.getUserId()));
    }

    @PostMapping("/update")
    public ResponseEntity<TransactionResponse> updateTransaction(@RequestBody UpdateTransactionRequest request,@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(transactionService.updateTransaction(request, principal.getUserId()));
    }

    @PostMapping("/{transactionId}/settle")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long transactionId, @AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(transactionService.settleTransaction(transactionId, principal.getUserId()));
    }

    @PostMapping("/{transactionId}/logs")
    public ResponseEntity<TransactionLogResponse> addTransactionLog(
            @PathVariable Long transactionId,
            @RequestBody CreateTransactionLogRequest request,
            @AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(transactionService.addTransactionLog(transactionId, request, principal.getUserId()));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam(defaultValue = "false") boolean settled,
            @RequestParam(required = false) String search) {

        List<TransactionResponse> out =
                transactionService.listTransactions(principal.getUserId(), settled, search);

        return ResponseEntity.ok(out);
    }


    @GetMapping("/{transactionId}/logs")
    public ResponseEntity<List<TransactionLogResponse>> getTransactionLogs(@PathVariable Long transactionId,@AuthenticationPrincipal JwtUserPrincipal principal) {
        return ResponseEntity.ok(transactionService.getLogsForTransaction(transactionId, principal.getUserId()));
    }

    @PostMapping("/{transactionId}/remind")
    public ResponseEntity<String> triggerReminder(@PathVariable Long transactionId,
                                                @AuthenticationPrincipal JwtUserPrincipal principal) {
        reminderService.sendReminder(transactionId,principal.getUserId(), principal.getFullName(),principal.getShopName(),principal.getPhoneNumber());
        return ResponseEntity.ok("Sent");
    }

    @PostMapping("/scheduler/test")
    public ResponseEntity<String> testScheduler(@AuthenticationPrincipal JwtUserPrincipal principal) {
        transactionService.remindDueTxns(principal.getUserId());
        return ResponseEntity.ok("Sent");
    }
}