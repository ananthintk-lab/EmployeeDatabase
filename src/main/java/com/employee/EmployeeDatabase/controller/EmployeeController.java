package com.employee.EmployeeDatabase.controller;

import com.employee.EmployeeDatabase.model.Employee;
import com.employee.EmployeeDatabase.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Exposes REST endpoints for managing {@link Employee} records. */
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /** Creates a new employee, honoring a client-supplied id if present, or generating one otherwise. */
    @Operation(summary = "Create a new employee", description = "Creates an employee record. If the request body includes an id, it is used as-is (rejected with 409 if already taken); otherwise an id is generated. The email must be unique across all employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure (blank fields or invalid email)"),
            @ApiResponse(responseCode = "409", description = "An employee with this email or id already exists")
    })
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /** Returns all employees ordered by id ascending, or an empty array if none exist. */
    @Operation(summary = "Get all employees", description = "Returns all employees ordered by id ascending, or an empty array if none exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of employees returned successfully")
    })
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /** Returns the employee with the given id, or 404 if no such employee exists. */
    @Operation(summary = "Get an employee by id", description = "Returns the employee with the given id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "No employee exists with the given id"),
            @ApiResponse(responseCode = "400", description = "The id path variable is not a valid Long")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /** Updates the employee with the given id and returns the updated record. */
    @Operation(summary = "Update an employee", description = "Updates the employee with the given id. Email uniqueness check excludes the employee being updated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failure (blank fields or invalid email)"),
            @ApiResponse(responseCode = "404", description = "No employee exists with the given id"),
            @ApiResponse(responseCode = "409", description = "Another employee already uses this email")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    /** Deletes the employee with the given id, or 404 if no such employee exists. */
    @Operation(summary = "Delete an employee", description = "Deletes the employee with the given id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No employee exists with the given id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
