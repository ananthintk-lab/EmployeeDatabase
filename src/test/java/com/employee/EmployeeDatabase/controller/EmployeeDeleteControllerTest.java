package com.employee.EmployeeDatabase.controller;

import com.employee.EmployeeDatabase.exception.EmployeeNotFoundException;
import com.employee.EmployeeDatabase.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Isolated web-layer tests for {@code DELETE /employees/{id}}, using a mocked {@link EmployeeService}
 * per the pattern established in {@link EmployeeGetAllControllerTest}.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void deleteEmployee_whenIdExists_returns204() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEmployee_whenIdNotFound_returns404() throws Exception {
        doThrow(new EmployeeNotFoundException(99L)).when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }
}
