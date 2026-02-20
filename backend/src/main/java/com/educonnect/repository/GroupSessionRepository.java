package com.educonnect.repository;

import com.educonnect.domain.GroupSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GroupSessionRepository extends JpaRepository<GroupSession, String> {

    List<GroupSession> findByGroupClassId(String groupClassId);

    List<GroupSession> findBySessionDate(LocalDate sessionDate);

    List<GroupSession> findByGroupClass_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(String teacherId, LocalDate start, LocalDate end);

    List<GroupSession> findByGroupClass_IdAndSessionDateBetweenOrderBySessionDateDesc(String groupClassId, LocalDate start, LocalDate end);
}
