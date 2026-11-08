package com.rescatta.backend.chat.repository;

import com.rescatta.backend.chat.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByReportId(Long reportId);
    Page<Conversation> findByCitizenUidOrderByLastMessageAtDescCreatedAtDesc(String citizenUid, Pageable pageable);
    Page<Conversation> findByOrganizationIdOrderByLastMessageAtDescCreatedAtDesc(Long organizationId, Pageable pageable);
}
