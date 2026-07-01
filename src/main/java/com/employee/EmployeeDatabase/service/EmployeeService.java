package com.employee.EmployeeDatabase.service;

import com.employee.EmployeeDatabase.exception.DuplicateEmailException;
import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

/** Encapsulates employee business logic on top of the {@link EmployeeRepository}. */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a new employee, rejecting the request if the email is already in use.
     * Any client-supplied id is ignored — the repository always assigns a fresh id.
     */
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmailException(employee.getEmail());
        }
        employee.setId(null);
        return employeeRepository.save(employee);
    }
}
