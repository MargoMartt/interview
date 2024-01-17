package com.example.interview.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException notUniqName(String name) {
        String message = String.format("Patient with name %s already exists", name);
        return new ForbiddenException(message);
    }

    public static ForbiddenException patientNotFound(String name) {
        String message = String.format("Patient with name %s doesn't exist", name);
        return new ForbiddenException(message);
    }

    public static ForbiddenException alreadyDeleted(String name) {
        String message = String.format("Patient %s has already been deleted ", name);
        return new ForbiddenException(message);
    }}
