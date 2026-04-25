package com.gateway.guardrails.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gateway.guardrails.core.entity.User;


public interface GuardrailsUserRepo extends JpaRepository<User, Long> {

}
