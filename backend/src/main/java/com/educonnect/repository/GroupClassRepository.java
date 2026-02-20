package com.educonnect.repository;

import com.educonnect.domain.GroupClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupClassRepository extends JpaRepository<GroupClass, String> {

    List<GroupClass> findByTeacher_Id(String teacherId);

    List<GroupClass> findByTeacher_IdAndActiveTrue(String teacherId);
}
