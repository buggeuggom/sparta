package com.sparta.msa.lesson.domain.ai.entity;

import com.sparta.msa.lesson.global.constants.enums.ChatMessageType;
import com.sparta.msa.lesson.global.constants.enums.StatusType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    ChatConversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ChatMessageType role;  // USER, ASSISTANT, SYSTEM

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatusType status;  // ACTIVE, INACTIVE, DELETED

    @Column(nullable = false, columnDefinition = "TEXT")
    String message;

    @Column
    Integer promptTokens;

    @Column
    Integer completionTokens;

    @Column
    Integer totalTokens;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @Builder
    public ChatMessage(
            ChatConversation conversation,
            ChatMessageType role,
            StatusType status,
            String message,
            Integer promptTokens,
            Integer completionTokens,
            Integer totalTokens
    ) {
        this.conversation = conversation;
        this.role = role;
        this.status = status;
        this.message = message;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.totalTokens = totalTokens;
    }
}