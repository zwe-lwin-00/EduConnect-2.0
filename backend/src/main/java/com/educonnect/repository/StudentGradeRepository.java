package com.educonnect.repository;

import com.educonnect.domain.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, String> {

    List<StudentGrade> findByStudentId(String studentId);

    List<StudentGrade> findByStudent_Id(String studentId);

    List<StudentGrade> findByTeacher_Id(String teacherId);
}
