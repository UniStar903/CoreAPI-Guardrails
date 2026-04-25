package com.gateway.guardrails.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gateway.guardrails.core.entity.Comment;

public interface GuardrailsCommentsRepo extends JpaRepository<Comment, Long> {

}
