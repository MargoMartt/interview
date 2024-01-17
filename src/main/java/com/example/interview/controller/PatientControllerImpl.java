package com.example.interview.controller;

import com.example.interview.domain.PatientRequest;
import com.example.interview.entity.PatientEntity;
import com.example.interview.exception.ForbiddenException;
import com.example.interview.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PatientControllerImpl implements PatientController {
    private final PatientService patientService;

    @Override
    public ResponseEntity<String> addPatient(PatientRequest patientRequest) {
        try {
            patientService.addPatient(patientRequest.getName(), patientRequest.getTemperature(),
                    patientRequest.getPulse(), patientRequest.getDateOfBirth());
            return ResponseEntity.ok("Patient was added");
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> setPatientDeleted(String name) {
        try {
            patientService.deletePatient(name);
            return ResponseEntity.ok("Patient was deleted");
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updatePatient(String oldName, PatientRequest patientRequest) {
        System.out.println(oldName);
        try {
            patientService.updatePatient(oldName, patientRequest);
            return ResponseEntity.ok("Patient was updated");
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<PatientEntity>> showAllActivePatients() {
        List<PatientEntity> allActivePatients = patientService.showAllActivePatients();
        return ResponseEntity.ok(allActivePatients);
    }
}
