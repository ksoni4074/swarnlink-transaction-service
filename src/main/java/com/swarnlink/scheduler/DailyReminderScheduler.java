package com.swarnlink.scheduler;

import com.swarnlink.config.ReminderProperties;
import com.swarnlink.dtos.UserInfoDto;
import com.swarnlink.entity.Transaction;
import com.swarnlink.feign.UserFeignClient;
import com.swarnlink.repository.TransactionRepository;
import com.swarnlink.service.ReminderService;
import com.swarnlink.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyReminderScheduler {


  private final TransactionRepository transactionRepository;
  private final ReminderProperties reminderProperties;
  private final TransactionService transactionService;

  @Scheduled(cron = "0 0 9 * * ?") // daily at 9 AM
  public void sendDailyReminders() {
    LocalDate dueDate = LocalDate.now().plusDays(reminderProperties.getDaysBeforeClose());

    List<Transaction> dueTxs = transactionRepository.findUnsettledTransactionsDueBy(dueDate);
    if (dueTxs.isEmpty()) {
      log.info("No due transactions found for reminders.");
      return;
    }

    transactionService.processUnsettledTransactions(dueTxs,reminderProperties.getBatch());

    log.info("Daily reminder job completed.");

  }

}