package com.employee.EmployeeDatabase.service;

import com.employee.EmployeeDatabase.exception.DuplicateEmailException;
import com.employee.EmployeeDatabase.exception.EmployeeIdAlreadyExistsException;
import com.employee.EmployeeDatabase.exception.EmployeeNotFoundException;
import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Encapsulates employee business logic on top of the {@link EmployeeRepository}. */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a new employee, rejecting the request if the email is already in use.
     * If the client supplies an id in the request body, it is honored (rejecting the
     * request with a conflict if that id is already taken); otherwise the repository
     * assigns a fresh id.
     */
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmailException(employee.getEmail());
        }
        if (employee.getId() != null && employeeRepository.findById(employee.getId()).isPresent()) {
            throw new EmployeeIdAlreadyExistsException(employee.getId());
        }
        return employeeRepository.save(employee);
    }

    /** Returns all employees, ordered by id ascending. */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /** Returns the employee with the given id, or throws {@link EmployeeNotFoundException} if none exists. */
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    /**
     * Updates the employee with the given id, rejecting the request if the id does not exist
     * or if the email is already in use by a different employee.
     */
    public Employee updateEmployee(Long id, Employee employee) {
        employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        if (employeeRepository.existsByEmailAndIdNot(employee.getEmail(), id)) {
            throw new DuplicateEmailException(employee.getEmail());
        }
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    /** Deletes the employee with the given id, or throws {@link EmployeeNotFoundException} if none exists. */
    public void deleteEmployee(Long id) {
        employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        employeeRepository.deleteById(id);
    }
}
