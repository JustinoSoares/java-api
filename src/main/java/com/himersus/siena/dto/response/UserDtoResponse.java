package com.himersus.siena.dto.response;

import java.util.UUID;

public record UserDtoResponse(
    UUID id,
    String name,
    String email
) {}
