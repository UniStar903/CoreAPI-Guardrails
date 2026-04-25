package com.gateway.guardrails.core.dto;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String authorType;
    private Long authorId;
    private String content;
    private Integer depthLevel;
}