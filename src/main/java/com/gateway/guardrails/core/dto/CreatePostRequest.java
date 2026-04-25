package com.gateway.guardrails.core.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String authorType;
    private Long authorId;
    private String content;
}