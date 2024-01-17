package com.example.interview.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class PatientRequest {
    private String name;
    private Double temperature;
    private Double pulse;
    private LocalDate dateOfBirth;
}
