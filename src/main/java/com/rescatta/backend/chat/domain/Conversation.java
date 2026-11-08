package com.rescatta.backend.chat.domain;

import com.rescatta.backend.organization.domain.Organization;
import com.rescatta.backend.report.domain.AnimalReport;
import com.rescatta.backend.shared.domain.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Hilo de chat entre el ciudadano que hizo un reporte y la organización (veterinaria/
 * refugio) que lo tomó. Se crea automáticamente en cuanto un reporte es APROBADO
 * (ver {@code ReportServiceImpl}), o bajo demanda si un admin escribe primero.
 */
@Entity
@Table(name = "conversations", uniqueConstraints = @UniqueConstraint(columnNames = "report_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private AnimalReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    /** UID de Firebase del ciudadano — dueño del reporte. */
    @Column(name = "citizen_uid", nullable = false, length = 128)
    private String citizenUid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ConversationStatus status = ConversationStatus.ABIERTA;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @Column(name = "last_message_preview", length = 200)
    private String lastMessagePreview;

    /** Mensajes sin leer por el ciudadano / por el equipo de la organización. */
    @Column(name = "unread_by_citizen", nullable = false)
    @Builder.Default
    private int unreadByCitizen = 0;

    @Column(name = "unread_by_org", nullable = false)
    @Builder.Default
    private int unreadByOrg = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
