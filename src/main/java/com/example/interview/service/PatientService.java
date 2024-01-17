package com.example.interview.service;

import com.example.interview.domain.PatientRequest;
import com.example.interview.entity.PatientEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public interface PatientService {
     void addPatient(String name, Double temperature, Double pulse, LocalDate dateOfBirth);
     void deletePatient(String name);

     void updatePatient(String oldName, PatientRequest patientRequest);

     List<PatientEntity> showAllActivePatients();
}
