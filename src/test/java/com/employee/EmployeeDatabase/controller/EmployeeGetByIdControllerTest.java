package com.employee.EmployeeDatabase.controller;

import com.employee.EmployeeDatabase.exception.EmployeeNotFoundException;
import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Isolated web-layer tests for {@code GET /employees/{id}}, using a mocked {@link EmployeeService}
 * per the pattern established in {@link EmployeeGetAllControllerTest}.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeGetByIdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getEmployeeById_whenFound_returns200WithEmployee() throws Exception {
        Employee employee = new Employee(1L, "Jane", "Doe", "jane@example.com");
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void getEmployeeById_whenNotFound_returns404() throws Exception {
        when(employeeService.getEmployeeById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }

    @Test
    void getEmployeeById_withInvalidIdType_returns400() throws Exception {
        mockMvc.perform(get("/employees/not-a-number"))
                .andExpect(status().isBadRequest());
    }
}
