package com.example.interview.controller;

import com.example.interview.domain.PatientRequest;
import com.example.interview.domain.PatientStatus;
import com.example.interview.entity.PatientEntity;
import com.example.interview.exception.ForbiddenException;
import com.example.interview.repository.PatientRepository;
import com.example.interview.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class PatientControllerImplUnitTest {
    @Mock
    private PatientService service;

    @Mock
    private PatientRepository repository;
    @InjectMocks
    private PatientControllerImpl controller;

    @Test
    public void tesAddPatient_Success() {
        PatientRequest patientRequest = patientRequest();
        ResponseEntity<String> responseEntity = controller.addPatient(patientRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Patient was added", responseEntity.getBody());
        verify(service, times(1))
                .addPatient("Rita", 36.6, 120.0, LocalDate.of(2002, 9, 7));
    }

    @Test
    public void tesAddPatient_ForbiddenException_NotUniqName() {
        PatientRequest patientRequest = patientRequest();
        doThrow(new ForbiddenException("Rita")).when(service)
                .addPatient("Rita", 36.6, 120.0, LocalDate.of(2002, 9, 7));
        ResponseEntity<String> response = controller.addPatient(patientRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("Rita"));
        verify(service, times(1))
                .addPatient("Rita", 36.6, 120.0, LocalDate.of(2002, 9, 7));
    }

    @Test
    public void testSetPatientDeleted_Success() {
        ResponseEntity<String> responseEntity = controller.setPatientDeleted(anyString());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Patient was deleted", responseEntity.getBody());
        verify(service, times(1)).deletePatient(anyString());
    }

    @Test
    public void testSetPatientDeleted_ForbiddenException_PatientNotFound() {
        ForbiddenException forbiddenException = ForbiddenException.patientNotFound("Rita");
        doThrow(forbiddenException).when(service).deletePatient("Rita");

        ResponseEntity<String> response = controller.setPatientDeleted("Rita");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(forbiddenException.getMessage(), response.getBody());
    }

    @Test
    public void testSetPatientDeleted_ForbiddenException_PatientAlreadyDeleted() {
        ForbiddenException forbiddenException = ForbiddenException.alreadyDeleted("Rita");
        doThrow(forbiddenException).when(service).deletePatient("Rita");

        ResponseEntity<String> response = controller.setPatientDeleted("Rita");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(forbiddenException.getMessage(), response.getBody());
    }


    @Test
    public void tetUpdatePatient_Success() {
        String oldName = "Rita";
        PatientRequest patientRequest = patientRequest();

        ResponseEntity<String> response = controller.updatePatient(oldName, patientRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Patient was updated", response.getBody());
    }

    @Test
    public void tetUpdatePatient_ForbiddenException_PatientNotFound() {
        String oldName = "Rita";
        PatientRequest patientRequest = patientRequest();
        ForbiddenException forbiddenException = ForbiddenException.patientNotFound(oldName);

        doThrow(forbiddenException).when(service).updatePatient(oldName, patientRequest);

        ResponseEntity<String> response = controller.updatePatient(oldName, patientRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(forbiddenException.getMessage(), response.getBody());
    }

    @Test
    public void tetUpdatePatient_ForbiddenException_NotUniqName() {
        String oldName = "Rita";
        PatientRequest patientRequest = patientRequest();
        ForbiddenException forbiddenException = ForbiddenException.notUniqName(patientRequest.getName());

        doThrow(forbiddenException).when(service).updatePatient(oldName, patientRequest);

        ResponseEntity<String> response = controller.updatePatient(oldName, patientRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(forbiddenException.getMessage(), response.getBody());

        verify(service, times(1)).updatePatient(oldName, patientRequest);
    }


    @Test
    public void tetUpdatePatient_ForbiddenException_PatientAlreadyDeleted() {
        String oldName = "Rita";
        PatientRequest patientRequest = patientRequest();
        ForbiddenException forbiddenException = ForbiddenException.alreadyDeleted(oldName);

        doThrow(forbiddenException).when(service).updatePatient(oldName, patientRequest);

        ResponseEntity<String> response = controller.updatePatient(oldName, patientRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(forbiddenException.getMessage(), response.getBody());

        verify(service, times(1)).updatePatient(oldName, patientRequest);
    }


    @Test
    public void testShowAllActivePatients_Success() {
        List<PatientEntity> allActivePatients = addPatients();
        when(service.showAllActivePatients()).thenReturn(allActivePatients);

        ResponseEntity<List<PatientEntity>> response = controller.showAllActivePatients();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(allActivePatients.size(), response.getBody().size());
        verifyPatients(allActivePatients, response.getBody());
    }

    private void verifyPatients(List<PatientEntity> allActivePatients, List<PatientEntity> responsePatients) {
        assertSoftly((softAssertions) -> {
            softAssertions.assertThat(responsePatients.stream().map(PatientEntity::getId).collect(Collectors.toList()))
                    .withFailMessage("Patient id should be equals")
                    .isEqualTo(allActivePatients.stream().map(PatientEntity::getId).collect(Collectors.toList()));
            softAssertions.assertThat(responsePatients.stream().map(PatientEntity::getName).collect(Collectors.toList()))
                    .withFailMessage("Patient name should be equals")
                    .isEqualTo(allActivePatients.stream().map(PatientEntity::getName).collect(Collectors.toList()));
            softAssertions.assertThat(responsePatients.stream().map(PatientEntity::getStatus).collect(Collectors.toList()))
                    .withFailMessage("Patient status should be equals")
                    .isEqualTo(allActivePatients.stream().map(PatientEntity::getStatus).collect(Collectors.toList()));
            softAssertions.assertThat(responsePatients.stream().map(PatientEntity::getPulse).collect(Collectors.toList()))
                    .withFailMessage("Patient pulse should be equals")
                    .isEqualTo(allActivePatients.stream().map(PatientEntity::getPulse).collect(Collectors.toList()));
            softAssertions.assertThat(responsePatients.stream().map(PatientEntity::getTemperature).collect(Collectors.toList()))
                    .withFailMessage("Patient temperature should be equals")
                    .isEqualTo(allActivePatients.stream().map(PatientEntity::getTemperature).collect(Collectors.toList()));
            softAssertions.assertThat(responsePatients.stream().map(PatientEntity::getDateOfBirth).collect(Collectors.toList()))
                    .withFailMessage("Patient date of birth should be equals")
                    .isEqualTo(allActivePatients.stream().map(PatientEntity::getDateOfBirth).collect(Collectors.toList()));
        });
    }


    private PatientRequest patientRequest() {
        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setName("Rita");
        patientRequest.setPulse(120.0);
        patientRequest.setTemperature(36.6);
        patientRequest.setDateOfBirth(LocalDate.of(2002, 9, 7));
        return patientRequest;
    }

    private List<PatientEntity> addPatients() {
        List<PatientEntity> patientEntities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            PatientEntity patient = new PatientEntity();
            patient.setName("Name" + i);
            patient.setStatus(PatientStatus.ACTIVE);
            patient.setTemperature(36.6);
            patient.setPulse(120.0);
            patient.setDateOfBirth(LocalDate.of(2000, i + 1, i + 1));
            patientEntities.add(patient);
        }
        return patientEntities;
    }
}
