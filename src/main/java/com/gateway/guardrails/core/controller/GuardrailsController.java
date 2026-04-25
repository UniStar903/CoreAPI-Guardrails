package com.gateway.guardrails.core.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GuardrailsController {
	
	@PostMapping("/posts")
	public String createNewPost() {
		return "new post created";
	}

	@PostMapping("/posts/{postId}/comments")
	public String addCommentsToPost(@PathVariable(required = true) int postId) {
		return postId+"added comments to post";
	}
	
	@PostMapping("/posts/{postId}/like")
	public String likedAPost(@PathVariable(required = true) int postId) {
		return postId+"liked a post";
	}
}
