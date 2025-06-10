package com.swarnlink.dtos;

// PartyResponse.java
public record PartyResponse(
    Long id,
    String name,
    String mobileNumber,
    String address
) {}