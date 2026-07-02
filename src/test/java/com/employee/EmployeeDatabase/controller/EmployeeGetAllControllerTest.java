package com.employee.EmployeeDatabase.controller;

import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Isolated web-layer tests for {@code GET /employees}, using a mocked {@link EmployeeService}
 * so that empty/single/multiple scenarios are fully controlled rather than relying on shared
 * in-memory repository state across tests.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeGetAllControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_withNoEmployees_returnsEmptyArray() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllEmployees_withSingleEmployee_returnsOneElementArray() throws Exception {
        Employee employee = new Employee(1L, "Jane", "Doe", "jane@example.com");
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("jane@example.com"));
    }

    @Test
    void getAllEmployees_withMultipleEmployees_returnsAllOrderedByRepository() throws Exception {
        Employee first = new Employee(1L, "Jane", "Doe", "jane@example.com");
        Employee second = new Employee(2L, "John", "Smith", "john@example.com");
        when(employeeService.getAllEmployees()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
