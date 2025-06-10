package com.swarnlink.dtos;

public record CreatePartyRequest(
    String name,
    String mobile,
    String address
) {}