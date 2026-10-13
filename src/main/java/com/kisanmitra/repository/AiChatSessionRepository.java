package com.kisanmitra.repository;

import com.kisanmitra.entity.AiChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long> {

    List<AiChatSession> findByUserEmailOrderByUpdatedAtDesc(String userEmail);

    Optional<AiChatSession> findByIdAndUserEmail(Long id, String userEmail);
}
