package com.educonnect.repository;

import com.educonnect.domain.TeacherAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailability, String> {

    List<TeacherAvailability> findByTeacher_Id(String teacherId);
}
