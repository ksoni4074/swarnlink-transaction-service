package com.swarnlink.dtos;

public record WhatsAppNotificationRequest(
    String phoneNumber, // E.g., +918765432100
    String message
) {}