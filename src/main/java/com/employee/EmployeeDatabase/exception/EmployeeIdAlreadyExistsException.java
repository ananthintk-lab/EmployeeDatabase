package com.employee.EmployeeDatabase.exception;

/** Thrown when attempting to create an employee with a client-supplied id that is already in use. */
public class EmployeeIdAlreadyExistsException extends RuntimeException {

    public EmployeeIdAlreadyExistsException(Long id) {
        super("Employee already exists with id: " + id);
    }
}
