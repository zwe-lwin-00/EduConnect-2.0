package com.educonnect.repository;

import com.educonnect.domain.GroupSessionAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GroupSessionAttendanceRepository extends JpaRepository<GroupSessionAttendance, String> {

    List<GroupSessionAttendance> findByStudent_IdAndGroupSession_SessionDateBetweenOrderByGroupSession_SessionDateDesc(
            String studentId, LocalDate start, LocalDate end);
}
