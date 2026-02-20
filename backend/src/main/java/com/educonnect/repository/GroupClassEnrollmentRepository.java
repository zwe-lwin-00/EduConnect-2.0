package com.educonnect.repository;

import com.educonnect.domain.GroupClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupClassEnrollmentRepository extends JpaRepository<GroupClassEnrollment, String> {

    List<GroupClassEnrollment> findByGroupClassId(String groupClassId);

    Optional<GroupClassEnrollment> findByGroupClassIdAndStudentId(String groupClassId, String studentId);

    boolean existsByGroupClassIdAndStudentId(String groupClassId, String studentId);

    List<GroupClassEnrollment> findByStudent_Id(String studentId);
}
