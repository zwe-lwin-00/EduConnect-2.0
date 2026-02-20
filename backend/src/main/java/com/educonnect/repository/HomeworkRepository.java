package com.educonnect.repository;

import com.educonnect.domain.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, String> {

    List<Homework> findByStudent_Id(String studentId);

    List<Homework> findByTeacher_IdAndStudent_Id(String teacherId, String studentId);

    List<Homework> findByTeacher_Id(String teacherId);
}
