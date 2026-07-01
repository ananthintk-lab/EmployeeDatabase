package com.employee.EmployeeDatabase.service;

import com.employee.EmployeeDatabase.exception.DuplicateEmailException;
import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void createEmployee_withUniqueEmail_savesAndReturnsEmployee() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee employee = new Employee(null, "Jane", "Doe", "jane@example.com");
        Employee saved = new Employee(1L, "Jane", "Doe", "jane@example.com");
        when(employeeRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(employeeRepository.save(employee)).thenReturn(saved);

        Employee result = employeeService.createEmployee(employee);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        verify(employeeRepository).save(employee);
    }

    @Test
    void createEmployee_withDuplicateEmail_throwsDuplicateEmailException() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee employee = new Employee(null, "Jane", "Doe", "jane@example.com");
        when(employeeRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(employee))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("jane@example.com");

        verify(employeeRepository, never()).save(any());
    }
}
