package com.employee.EmployeeDatabase.exception;

/** Thrown when an employee lookup by ID finds no matching record. */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}
