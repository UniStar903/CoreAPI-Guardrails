package com.gateway.guardrails.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gateway.guardrails.core.entity.Comment;

public interface GuardrailsCommentsRepo extends JpaRepository<Comment, Long> {
	List<Comment> findByPostId(Long postId);
    Integer countByPostIdAndAuthorTypeAndAuthorId(Long postId, String authorType, Long authorId);
}
