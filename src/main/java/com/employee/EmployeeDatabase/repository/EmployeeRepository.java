package com.employee.EmployeeDatabase.repository;

import com.employee.EmployeeDatabase.model.Employee;

import java.util.List;
import java.util.Optional;

/** Defines persistence operations for {@link Employee} records. */
public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(Long id);

    List<Employee> findAll();

    void deleteById(Long id);

    boolean existsByEmail(String email);
}
