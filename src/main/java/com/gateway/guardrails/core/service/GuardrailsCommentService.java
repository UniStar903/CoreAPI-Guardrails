package com.gateway.guardrails.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gateway.guardrails.core.dto.CreateCommentRequest;
import com.gateway.guardrails.core.entity.Comment;
import com.gateway.guardrails.core.repository.GuardrailsCommentsRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuardrailsCommentService {

	@Autowired
    private GuardrailsCommentsRepo commentRepository;
	@Autowired
    private GuardrailService guardrailService;
	@Autowired
    private GuardrailViralityService viralityService;
	@Autowired
    private GuardrailNotificationService notificationService;
	
	private static final String AUTHOR_TYPE_BOT = "BOT";
//    private static final String AUTHOR_TYPE_USER = "USER";

    @Transactional
    public Comment createComment(Long postId, CreateCommentRequest request) {
        String authorType = request.getAuthorType();
        Long authorId = request.getAuthorId();
        Integer depthLevel = request.getDepthLevel() != null ? request.getDepthLevel() : 0;

        if (!guardrailService.checkVerticalCap(depthLevel))
            throw new RuntimeException("429: Vertical cap exceeded - comment thread cannot exceed 20 levels");

        if (AUTHOR_TYPE_BOT.equals(authorType)) {
            if (!guardrailService.checkHorizontalCap(postId))
                throw new RuntimeException("429: Horizontal cap exceeded - post cannot have more than 100 bot replies");

            if (!guardrailService.checkCooldownCap(authorId, authorId))
                throw new RuntimeException("429: Cooldown cap active - bot cannot interact with same human within 10 minutes");
        }

        Comment comment = new Comment();
        comment.setPost_id(postId);
        comment.setAuthor_type(authorType);
        comment.setAuthor_id(authorId);
        comment.setContent(request.getContent());
        comment.setDepth_level(depthLevel);
        comment.setCreated_at(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Created comment {} on post {}", savedComment.getId(), postId);

        String interactionType = AUTHOR_TYPE_BOT.equals(authorType) ? "BOT_REPLY" : "HUMAN_COMMENT";
        viralityService.updateViralityScore(postId, interactionType);

        if (AUTHOR_TYPE_BOT.equals(authorType))
            notificationService.handleNotification(authorId, "Bot " + authorId + " replied to your post");

        return savedComment;
    }

    @Transactional
    public void likePost(Long postId, String authorType, Long authorId) {
        viralityService.updateViralityScore(postId, "HUMAN_LIKE");
        log.info("User {} liked post {}", authorId, postId);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}