package com.employee.EmployeeDatabase.service;

import com.employee.EmployeeDatabase.exception.DuplicateEmailException;
import com.employee.EmployeeDatabase.exception.EmployeeNotFoundException;
import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @Test
    void getAllEmployees_withNoEmployees_returnsEmptyList() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllEmployees_withEmployees_returnsThemInRepositoryOrder() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee first = new Employee(1L, "Jane", "Doe", "jane@example.com");
        Employee second = new Employee(2L, "John", "Smith", "john@example.com");
        when(employeeRepository.findAll()).thenReturn(List.of(first, second));

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).containsExactly(first, second);
    }

    @Test
    void getEmployeeById_whenFound_returnsEmployee() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee employee = new Employee(1L, "Jane", "Doe", "jane@example.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertThat(result).isEqualTo(employee);
    }

    @Test
    void getEmployeeById_whenNotFound_throwsEmployeeNotFoundException() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateEmployee_withExistingIdAndUniqueEmail_savesAndReturnsEmployee() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee existing = new Employee(1L, "Jane", "Doe", "jane@example.com");
        Employee update = new Employee(null, "Jane", "Smith", "jane.smith@example.com");
        Employee saved = new Employee(1L, "Jane", "Smith", "jane.smith@example.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmailAndIdNot("jane.smith@example.com", 1L)).thenReturn(false);
        when(employeeRepository.save(update)).thenReturn(saved);

        Employee result = employeeService.updateEmployee(1L, update);

        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(update.getId()).isEqualTo(1L);
        verify(employeeRepository).save(update);
    }

    @Test
    void updateEmployee_whenIdNotFound_throwsEmployeeNotFoundException() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee update = new Employee(null, "Jane", "Doe", "jane@example.com");
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(99L, update))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_withEmailBelongingToAnotherEmployee_throwsDuplicateEmailException() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee existing = new Employee(1L, "Jane", "Doe", "jane@example.com");
        Employee update = new Employee(null, "Jane", "Doe", "john@example.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmailAndIdNot("john@example.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> employeeService.updateEmployee(1L, update))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("john@example.com");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_keepingSameEmail_isAllowed() {
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        Employee existing = new Employee(1L, "Jane", "Doe", "jane@example.com");
        Employee update = new Employee(null, "Jane", "Doe", "jane@example.com");
        Employee saved = new Employee(1L, "Jane", "Doe", "jane@example.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.existsByEmailAndIdNot("jane@example.com", 1L)).thenReturn(false);
        when(employeeRepository.save(update)).thenReturn(saved);

        Employee result = employeeService.updateEmployee(1L, update);

        assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }
}
