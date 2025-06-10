package com.swarnlink.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swarnlink.dtos.WhatsAppNotificationRequest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WhatsAppReminderPublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${cloud.aws.sqs.reminder-queue-name}")
    private String queueName;

    public void sendReminder(String phone, String message) {
        WhatsAppNotificationRequest req = new WhatsAppNotificationRequest(phone, message);
        sqsTemplate.send(to -> to
                .queue(queueName)
                .payload(req));
    }
}