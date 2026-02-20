package com.educonnect.repository;

import com.educonnect.domain.TeacherProfile;
import com.educonnect.domain.TeacherProfile.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, String> {

    Optional<TeacherProfile> findByUser_Id(String userId);

    List<TeacherProfile> findByVerificationStatus(VerificationStatus status);

    @Query("SELECT t FROM TeacherProfile t LEFT JOIN FETCH t.user")
    List<TeacherProfile> findAllWithUser();
}
