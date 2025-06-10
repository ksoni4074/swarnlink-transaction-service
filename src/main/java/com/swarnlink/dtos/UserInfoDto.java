package com.swarnlink.dtos;

public record UserInfoDto(
    Long id,
    String fullName,
    String shopName,
    String phoneNumber
) {}