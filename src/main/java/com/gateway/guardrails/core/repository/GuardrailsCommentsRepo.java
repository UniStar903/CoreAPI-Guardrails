package com.gateway.guardrails.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gateway.guardrails.core.entity.Comment;

public interface GuardrailsCommentsRepo extends JpaRepository<Comment, Long> {
	List<Comment> findByPostId(Long post_id);
    Integer countByPostIdAndAuthorTypeAndAuthorId(Long post_id, String author_type, Long author_id);
}
