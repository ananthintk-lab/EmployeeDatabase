package com.employee.EmployeeDatabase.exception;

/** Thrown when attempting to create or update an employee with an email already in use. */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Employee already exists with email: " + email);
    }
}
