package com.employee.EmployeeDatabase;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Employee Management API",
				version = "1.0.0",
				description = "REST API for creating, retrieving, updating, and deleting employee records."
		)
)
public class EmployeeDatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeDatabaseApplication.class, args);
	}

}
