package com.rescatta.backend.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /** UID de Firebase de quien envía (ciudadano o miembro del equipo admin). */
    @Column(name = "sender_uid", nullable = false, length = 128)
    private String senderUid;

    /** true si lo envió la veterinaria/refugio, false si lo envió el ciudadano. */
    @Column(name = "sender_is_org", nullable = false)
    private boolean senderIsOrg;

    @Column(name = "sender_name", length = 150)
    private String senderName;

    @Column(nullable = false, length = 2000)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
