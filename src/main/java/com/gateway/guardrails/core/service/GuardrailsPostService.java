package com.gateway.guardrails.core.service;

import com.gateway.guardrails.core.dto.CreatePostRequest;
import com.gateway.guardrails.core.entity.Post;
import com.gateway.guardrails.core.repository.GuardrailsPostRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuardrailsPostService {

	@Autowired
    private GuardrailsPostRepo postRepository;

    @Transactional
    public Post createPost(CreatePostRequest request) {
        Post post = new Post();
        post.setAuthor_type(request.getAuthorType());
        post.setAuthor_id(request.getAuthorId());
        post.setContent(request.getContent());
        post.setCreated_at(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        log.info("Created post with id: {}", savedPost.getId());
        return savedPost;
    }

    public Post getPostById(Long id) {
        return postRepository
        		.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }
}