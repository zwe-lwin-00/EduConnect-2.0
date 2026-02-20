package com.educonnect.repository;

import com.educonnect.domain.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, String> {

    List<AttendanceLog> findByContractIdAndSessionDate(String contractId, LocalDate sessionDate);

    List<AttendanceLog> findBySessionDate(LocalDate sessionDate);

    List<AttendanceLog> findBySessionDateBetween(LocalDate start, LocalDate end);

    List<AttendanceLog> findByContract_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(String teacherId, LocalDate start, LocalDate end);

    List<AttendanceLog> findByContract_Student_IdAndSessionDateBetweenOrderBySessionDateDesc(String studentId, LocalDate start, LocalDate end);

    List<AttendanceLog> findByContract_Student_Id(String studentId);
}
