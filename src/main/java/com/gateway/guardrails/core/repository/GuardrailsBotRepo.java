package com.gateway.guardrails.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gateway.guardrails.core.entity.Bot;


public interface GuardrailsBotRepo extends JpaRepository<Bot, Long> {

}
