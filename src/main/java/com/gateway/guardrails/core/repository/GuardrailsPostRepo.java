package com.gateway.guardrails.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gateway.guardrails.core.entity.Post;


public interface GuardrailsPostRepo extends JpaRepository<Post, Long> {

}
