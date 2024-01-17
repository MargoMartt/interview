package com.example.interview.service;

import com.example.interview.domain.PatientRequest;
import com.example.interview.domain.PatientStatus;
import com.example.interview.entity.PatientEntity;
import com.example.interview.exception.ForbiddenException;
import com.example.interview.repository.PatientRepository;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @Mock
    PatientRepository repository;
    @InjectMocks
    PatientServiceImpl service;

    @Test
    public void testAddPatient_Success() {
        PatientEntity patient = getPatient();
        when(repository.existsByName(patient.getName())).thenReturn(false);

        service.addPatient(patient.getName(), patient.getTemperature(), patient.getPulse(), patient.getDateOfBirth());

        verify(repository, times(1)).existsByName(patient.getName());
        verify(repository, times(1)).save(any());
    }

    @Test
    public void testAddPatient_ForbiddenException_NotUniqName() {
        PatientEntity patient = getPatient();
        when(repository.existsByName(patient.getName())).thenReturn(true);

        assertThrows(ForbiddenException.class, () ->
                service.addPatient(patient.getName(), patient.getTemperature(), patient.getPulse(), patient.getDateOfBirth()));

        verify(repository, times(1)).existsByName(patient.getName());
        verify(repository, times(0)).save(any());
    }

    @Test
    public void testDeletePatient_Success() {
        PatientEntity patient = getPatient();
        when(repository.findByName(anyString())).thenReturn(patient);

        service.deletePatient(patient.getName());

        verify(repository, times(1)).save(any());
    }

    @Test
    public void testDeletePatient_ForbiddenException_PatientNotFound() {
        when(repository.findByName(anyString())).thenReturn(null);

        ThrowableAssert.ThrowingCallable deletePatient =
                () -> service.deletePatient(anyString());

        assertThatThrownBy(deletePatient).isInstanceOf(ForbiddenException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void testDeletePatient_ForbiddenException_AlreadyDeleted() {
        PatientEntity patient = getPatient();
        when(repository.findByName(anyString())).thenReturn(patient);
        patient.setStatus(PatientStatus.DELETED);

        ThrowableAssert.ThrowingCallable deletePatient =
                () -> service.deletePatient(patient.getName());

        assertThatThrownBy(deletePatient).isInstanceOf(ForbiddenException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void testUpdatePatient_Success() {
        String oldName = "Ritka";
        PatientEntity patient = getPatient();
        PatientRequest patientRequest = patientRequest();
        when(repository.findByName(anyString())).thenReturn(patient);

        service.updatePatient(oldName, patientRequest);

        verify(repository, times(1)).save(any());

    }

    @Test
    public void testUpdatePatient_ForbiddenException_PatientNotFound() {
        String oldName = "Rita";
        PatientRequest patientRequest = patientRequest();

        when(repository.findByName(oldName)).thenReturn(null);

        assertThrows(ForbiddenException.class, () ->
                service.updatePatient(oldName, patientRequest));

        verify(repository, times(0)).save(any());
    }

    @Test
    public void testUpdatePatient_ForbiddenException_NotUniqName() {
        String oldName = "Rita";
        PatientRequest patientRequest = patientRequest();
        PatientEntity patient = getPatient();
        when(repository.findByName(oldName)).thenReturn(patient);

        when(repository.existsByName(patientRequest.getName())).thenReturn(true);

        assertThrows(ForbiddenException.class, () ->
                service.updatePatient(oldName, patientRequest));

        verify(repository, times(0)).save(any());
    }

    @Test
    public void testUpdatePatient_ForbiddenException_AlreadyDeleted() {
        String oldName = "Ritka";
        PatientEntity patient = getPatient();
        patient.setStatus(PatientStatus.DELETED);
        PatientRequest patientRequest = patientRequest();

        when(repository.findByName(oldName)).thenReturn(patient);


        ThrowableAssert.ThrowingCallable deletePatient =
                () -> service.updatePatient(oldName, patientRequest);

        assertThatThrownBy(deletePatient).isInstanceOf(ForbiddenException.class);

        verify(repository, never()).save(any());
    }

    @Test
    public void testShowAllActivePatients_Success() {
        List<PatientEntity> entities = addPatients();
        when(repository.findAllByStatusOrderByName(PatientStatus.ACTIVE)).thenReturn(entities);

        List<PatientEntity> patients = service.showAllActivePatients();

        assertEquals(entities.size(), patients.size());

    }

    @Test
    public void testShowAllActivePatients_NoPatients() {
        when(repository.findAllByStatusOrderByName(PatientStatus.ACTIVE)).thenReturn(null);

        List<PatientEntity> patients = service.showAllActivePatients();

        assertNull(patients);
    }

    private PatientEntity getPatient() {
        PatientEntity patient = new PatientEntity();
        patient.setId(1);
        patient.setName("Rita");
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setPulse(120.0);
        patient.setTemperature(36.6);
        patient.setDateOfBirth(LocalDate.of(2002, 7, 9));
        return patient;
    }

    private PatientRequest patientRequest() {
        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setName("Ritka");
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
