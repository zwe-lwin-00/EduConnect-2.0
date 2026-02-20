package com.educonnect.repository;

import com.educonnect.domain.ContractSession;
import com.educonnect.domain.ContractSession.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractSessionRepository extends JpaRepository<ContractSession, String> {

    List<ContractSession> findByTeacher_Id(String teacherId);

    List<ContractSession> findByStudent_Id(String studentId);

    List<ContractSession> findByTeacher_IdAndStatus(String teacherId, ContractStatus status);

    List<ContractSession> findByStudent_IdAndStatus(String studentId, ContractStatus status);
}
