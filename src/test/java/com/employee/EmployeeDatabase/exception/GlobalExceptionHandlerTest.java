package com.employee.EmployeeDatabase.exception;

import com.employee.EmployeeDatabase.controller.EmployeeController;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that {@link GlobalExceptionHandler} produces the standard
 * {@code { timestamp, status, error, message, path }} error shape for every handled exception type.
 */
@WebMvcTest(EmployeeController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void employeeNotFoundException_returns404WithStandardErrorShape() throws Exception {
        when(employeeService.getEmployeeById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"))
                .andExpect(jsonPath("$.path").value("/employees/99"));
    }

    @Test
    void duplicateEmailException_returns409WithStandardErrorShape() throws Exception {
        Employee employee = new Employee(null, "Jane", "Doe", "jane@example.com");
        when(employeeService.createEmployee(any(Employee.class)))
                .thenThrow(new DuplicateEmailException("jane@example.com"));

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Employee already exists with email: jane@example.com"))
                .andExpect(jsonPath("$.path").value("/employees"));
    }

    @Test
    void methodArgumentNotValidException_returns400WithFieldErrorsInMessage() throws Exception {
        Employee employee = new Employee(null, "", "", "not-an-email");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("firstName")))
                .andExpect(jsonPath("$.path").value("/employees"));
    }

    @Test
    void methodArgumentTypeMismatchException_returns400WithStandardErrorShape() throws Exception {
        mockMvc.perform(get("/employees/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/employees/not-a-number"));
    }
}
