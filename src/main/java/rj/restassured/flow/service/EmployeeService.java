package rj.restassured.flow.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import rj.restassured.flow.entity.Employee;
import rj.restassured.flow.exceptions.DuplicateEmployeeException;
import rj.restassured.flow.exceptions.InvalidEmployeeDataException;
import rj.restassured.flow.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public Employee registerEmployee(Employee employee) {

		// Validate employee data
		if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
			throw new InvalidEmployeeDataException("First name cannot be empty");
		}

		if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
			throw new InvalidEmployeeDataException("Last name cannot be empty");
		}

		Optional<Employee> existingEmployee = employeeRepository.findByFirstNameAndLastName(employee.getFirstName(),
				employee.getLastName());

		if (existingEmployee.isPresent()) {
			throw new DuplicateEmployeeException("Employee with the same first and last name already exists.");
		}

		// Set the session ID and save
		employee.setSessionId(UUID.randomUUID().toString());
		return employeeRepository.save(employee);
	}

	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id).orElse(null);
	}

	public List<Employee> searchEmployees(String department, String city) {
		return employeeRepository.findByDepartmentAndCity(department, city);
	}

	public Employee getEmployeeByFirstAndLastName(String firstName, String lastName) {
		return employeeRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);
	}

	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	public ResponseEntity<String> authenticateEmployee(String firstName, String lastName,
			HttpServletResponse response) {
		Optional<Employee> existingEmployee = employeeRepository.findByFirstNameAndLastName(firstName, lastName);
		System.out.println("=====" + existingEmployee);

		if (existingEmployee.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"); // 401 Unauthorized
		}

		Employee foundEmployee = existingEmployee.get();

		// Check if the user is "admin"
		if ("admin".equalsIgnoreCase(foundEmployee.getFirstName())
				&& "admin".equalsIgnoreCase(foundEmployee.getLastName())) {

			String sessionId = foundEmployee.getSessionId();
			if (sessionId == null) {
				sessionId = UUID.randomUUID().toString();
				foundEmployee.setSessionId(sessionId);
				employeeRepository.save(foundEmployee);
			}

			// Set secure session cookie
			Cookie sessionCookie = new Cookie("SESSIONID", sessionId);
			sessionCookie.setHttpOnly(true);
			sessionCookie.setPath("/");
			response.addCookie(sessionCookie);

			return ResponseEntity.ok("Login successful (Admin session assigned)");
		}

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Only admin gets a session");
	}

	public void updateEmployee(Employee employee) {
		employeeRepository.save(employee); // Saves updated employee
	}

	public void deleteEmployee(Long id) {
		employeeRepository.deleteById(id); // Delete employee by ID
	}

    public void deleteAllEmployees() {
        employeeRepository.deleteAll();
    }
}
