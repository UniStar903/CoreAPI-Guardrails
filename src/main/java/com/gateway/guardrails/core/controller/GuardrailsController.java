package com.gateway.guardrails.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gateway.guardrails.core.dto.CreateCommentRequest;
import com.gateway.guardrails.core.entity.Comment;
import com.gateway.guardrails.core.repository.GuardrailsPostRepo;
import com.gateway.guardrails.core.service.GuardrailViralityService;
import com.gateway.guardrails.core.service.GuardrailsCommentService;
import com.gateway.guardrails.core.service.GuardrailsPostService;

@RestController
@RequestMapping("/api")
public class GuardrailsController {
	
	@Autowired
	private GuardrailsCommentService commentService;
	@Autowired
	private GuardrailsPostService postService;
	@Autowired
    private GuardrailViralityService viralityService;
	
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
	
	@GetMapping("/posts/{postId}/comments")
	public ResponseEntity<List<Comment>> getComments(@PathVariable(required = true) long postId){
		return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
	}
	
	@PostMapping("/posts/{postId}/like")
	public ResponseEntity<Map<String, String>> likedAPost(@PathVariable(required = true) long postId,@RequestBody Map<String, Object> request) {        
//		String authorType = (String) request.get("authorType");
//		Long authorId = ((Number) request.get("authorId")).longValue();
		viralityService.updateViralityScore(postId, "HUMAN_LIKE");
		return ResponseEntity.ok(Map.of("message", "Post liked successfully"));
    }
}
