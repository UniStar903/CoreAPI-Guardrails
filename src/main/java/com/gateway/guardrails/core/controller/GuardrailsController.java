package com.gateway.guardrails.core.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gateway.guardrails.core.dto.CreateCommentRequest;
import com.gateway.guardrails.core.entity.Comment;
import com.gateway.guardrails.core.service.GuardrailsCommentService;

@RestController
@RequestMapping("/api")
public class GuardrailsController {
	
	@Autowired
	private GuardrailsCommentService commentService;
	
	@PostMapping("/posts")
	public String createNewPost() {
		return "new post created";
	}

	@PostMapping("/posts/{postId}/comments")
	public String addCommentsToPost(@PathVariable(required = true) long postId, @RequestBody CreateCommentRequest request) {
        
        try {
            Comment comment = commentService.createComment(postId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment).toString();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.startsWith("429:")) {
                return ResponseEntity
                		.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(
                        		Map.of(
                        				"error",
                        				message.substring(4).trim()
                        				)
                        		).toString();
            }
            throw e;
        }
    }
	
	@PostMapping("/posts/{postId}/like")
	public String likedAPost(@PathVariable(required = true) int postId) {
		return postId+"liked a post";
	}
}
