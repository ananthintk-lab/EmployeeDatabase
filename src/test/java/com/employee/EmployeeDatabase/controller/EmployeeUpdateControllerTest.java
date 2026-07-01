package com.employee.EmployeeDatabase.controller;

import com.employee.EmployeeDatabase.exception.DuplicateEmailException;
import com.employee.EmployeeDatabase.exception.EmployeeNotFoundException;
import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Isolated web-layer tests for {@code PUT /employees/{id}}, using a mocked {@link EmployeeService}
 * per the pattern established in {@link EmployeeGetAllControllerTest}.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void updateEmployee_withValidRequest_returns200WithUpdatedEmployee() throws Exception {
        Employee update = new Employee(null, "Jane", "Smith", "jane.smith@example.com");
        Employee updated = new Employee(1L, "Jane", "Smith", "jane.smith@example.com");
        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(updated);

        mockMvc.perform(put("/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }

    @Test
    void updateEmployee_whenIdNotFound_returns404() throws Exception {
        Employee update = new Employee(null, "Jane", "Doe", "jane@example.com");
        when(employeeService.updateEmployee(eq(99L), any(Employee.class)))
                .thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(put("/employees/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }

    @Test
    void updateEmployee_withBlankFirstName_returns400() throws Exception {
        Employee update = new Employee(null, "", "Doe", "jane@example.com");

        mockMvc.perform(put("/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployee_withEmailBelongingToAnotherEmployee_returns409() throws Exception {
        Employee update = new Employee(null, "Jane", "Doe", "john@example.com");
        when(employeeService.updateEmployee(eq(1L), any(Employee.class)))
                .thenThrow(new DuplicateEmailException("john@example.com"));

        mockMvc.perform(put("/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Employee already exists with email: john@example.com"));
    }
}
