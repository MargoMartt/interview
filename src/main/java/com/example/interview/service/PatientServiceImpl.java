package com.example.interview.service;

import com.example.interview.domain.PatientRequest;
import com.example.interview.domain.PatientStatus;
import com.example.interview.entity.PatientEntity;
import com.example.interview.exception.ForbiddenException;
import com.example.interview.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    @Override
    public void addPatient(String name, Double temperature, Double pulse, LocalDate dateOfBirth) {
        boolean isUniqName = patientRepository.existsByName(name);
        if (isUniqName) {
            log.error("Patient with same name already exists : {}", name);
            throw ForbiddenException.notUniqName(name);
        }
        PatientEntity patient = new PatientEntity();
        patient.setName(name);
        patient.setTemperature(temperature);
        patient.setPulse(pulse);
        patient.setDateOfBirth(dateOfBirth);
        patient.setStatus(PatientStatus.ACTIVE);
        patientRepository.save(patient);
    }

    @Override
    public void deletePatient(String name) {
        PatientEntity patient = patientRepository.findByName(name);

        if (patient == null) {
            log.error("There is no patient with such name : {}", name);
            throw ForbiddenException.patientNotFound(name);
        }
        if (patient.getStatus() == PatientStatus.DELETED) {
            log.error("The patient has already been deleted");
            throw ForbiddenException.alreadyDeleted(name);
        }

        patient.setStatus(PatientStatus.DELETED);
        patientRepository.save(patient);
    }

    @Override
    public void updatePatient(String oldName, PatientRequest patientRequest) {

        PatientEntity patient = patientRepository.findByName(oldName);
        String newName = patientRequest.getName();
        Double temperature = patientRequest.getTemperature();
        Double pulse = patientRequest.getPulse();
        LocalDate dateOfBirth = patientRequest.getDateOfBirth();

        if (patient == null) {
            log.error("There is no patient with such name : {}", oldName);
            throw ForbiddenException.patientNotFound(oldName);
        }

        boolean isUniqName = patientRepository.existsByName(newName);

        if (isUniqName) {
            log.error("Patient with same name already exists : {}", newName);
            throw ForbiddenException.notUniqName(newName);
        }

        if (patient.getStatus() == PatientStatus.DELETED) {
            log.error("The patient has already been deleted");
            throw ForbiddenException.alreadyDeleted(oldName);
        }
        if (newName != null) {
            patient.setName(newName);
        }
        if (temperature != null) {
            patient.setTemperature(temperature);
        }
        if (pulse != null) {
            patient.setPulse(pulse);
        }
        if (dateOfBirth != null) {
            patient.setDateOfBirth(dateOfBirth);
        }
        patientRepository.save(patient);
    }

    @Override
    public List<PatientEntity> showAllActivePatients() {
        return patientRepository.findAllByStatusOrderByName(PatientStatus.ACTIVE);
    }
}
