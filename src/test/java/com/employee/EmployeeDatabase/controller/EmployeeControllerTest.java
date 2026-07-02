package com.employee.EmployeeDatabase.controller;

import com.employee.EmployeeDatabase.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createEmployee_withValidData_returns201WithCreatedEmployee() throws Exception {
        Employee employee = new Employee(null, "Jane", "Doe", "jane.doe.create@example.com");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe.create@example.com"));
    }

    @Test
    void createEmployee_withBlankFirstName_returns400() throws Exception {
        Employee employee = new Employee(null, "", "Doe", "blank.firstname@example.com");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_withInvalidEmail_returns400() throws Exception {
        Employee employee = new Employee(null, "John", "Smith", "not-an-email");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_withDuplicateEmail_returns409() throws Exception {
        Employee employee = new Employee(null, "Alice", "Brown", "duplicate.check@example.com");
        String payload = objectMapper.writeValueAsString(employee);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict());
    }

    @Test
    void createEmployee_withClientSuppliedId_honorsThatIdInsteadOfAutoGenerating() throws Exception {
        Employee employee = new Employee(9001L, "Client", "Supplied", "client.supplied.id@example.com");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9001));

        mockMvc.perform(get("/employees/9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("client.supplied.id@example.com"));
    }

    @Test
    void createEmployee_withIdAlreadyTaken_returns409() throws Exception {
        Employee first = new Employee(9002L, "First", "Owner", "first.owner@example.com");
        Employee second = new Employee(9002L, "Second", "Owner", "second.owner@example.com");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isConflict());
    }
}
