package com.educonnect.repository;

import com.educonnect.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, String> {

    List<Holiday> findByHolidayDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT h FROM Holiday h WHERE FUNCTION('YEAR', h.holidayDate) = :year")
    List<Holiday> findByHolidayDateYear(@Param("year") int year);
}
