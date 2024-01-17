package com.example.interview.controller;

import com.example.interview.domain.PatientRequest;
import com.example.interview.entity.PatientEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RequestMapping("api/patient")
public interface PatientController {

    @PostMapping(value = "/add")
    ResponseEntity<String> addPatient(@RequestBody(required = false) PatientRequest patientRequest);

    @PostMapping(value = "/delete")
    ResponseEntity<String> setPatientDeleted (@RequestParam(required = false) String name);

    @PostMapping(value = "/update")
    ResponseEntity<String> updatePatient (@RequestParam(required = false) String oldName,
                                          @RequestBody(required = false) PatientRequest patientRequest);

    @GetMapping(value = "/allActive")
    ResponseEntity<List<PatientEntity>> showAllActivePatients();
}
