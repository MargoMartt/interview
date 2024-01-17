package com.example.interview.repository;

import com.example.interview.domain.PatientStatus;
import com.example.interview.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Integer> {
    Boolean existsByName(String name);

    PatientEntity findByName(String name);

    List<PatientEntity> findAllByStatusOrderByName(PatientStatus status);
}
