package rj.restassured.flow.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletResponse;
import rj.restassured.flow.entity.Employee;
import rj.restassured.flow.exceptions.InvalidEmployeeDataException;
import rj.restassured.flow.service.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> employee, HttpServletResponse response) {
    	return employeeService.authenticateEmployee(employee.get("firstName"), employee.get("lastName"), response);
    }
      
    // Endpoint to register an employee
    @PostMapping("/register")
 //   @ResponseStatus(HttpStatus.CREATED)
    //public Employee registerEmployee(@RequestBody Employee employee) {
    public ResponseEntity<Employee> registerEmployee(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.registerEmployee(employee));
    }
    
    @PostMapping("/registerloc")
 //   @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Employee> registerEmployeeLocation(@RequestBody Employee employee,  UriComponentsBuilder uriBuilder) {
    	 Employee savedEmployee = employeeService.registerEmployee(employee);

    	    // Build location URI
    	    URI location = uriBuilder.path("/employees/{id}").buildAndExpand(savedEmployee.getId()).toUri();

    	    // Return ResponseEntity with created status, location header, and employee object
    	    return ResponseEntity.created(location).body(savedEmployee);
    }

    
    // ✅ GET Employee by ID with improved validation
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        Long employeeId = validateAndParseId(id);

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
        return ResponseEntity.ok(employee);
    }

    // ✅ Utility method to validate and parse ID
    private Long validateAndParseId(String id) {
        try {
            Long parsedId = Long.parseLong(id);
            if (parsedId <= 0) {
                throw new IllegalArgumentException("Invalid ID. Must be a positive number.");
            }
            return parsedId;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("ID must be a numeric value.");
        }
    }

        @PostMapping("/search")
        public ResponseEntity<List<Employee>> searchEmployees(
                @RequestParam String department,
                @RequestParam String city) {
            List<Employee> employees = employeeService.searchEmployees(department, city);            
            if (employees.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content if no results
            }
            return ResponseEntity.ok(employees); // 200 OK with result list
        }
        
        @GetMapping("/search/name")
        public ResponseEntity<Employee> getEmployeeByName(
                @RequestParam String firstName,
                @RequestParam String lastName) {
            Employee employee = employeeService.getEmployeeByFirstAndLastName(firstName, lastName);
            if (employee == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(employee);
        }
        
        @GetMapping
        public ResponseEntity<List<Employee>> getAllEmployees() {
            List<Employee> employees = employeeService.getAllEmployees();
            if (employees.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content if no employees found
            }
            return ResponseEntity.ok(employees); // 200 OK with employee list
        }
        
        @PutMapping("/{id}")        
        public ResponseEntity<String> updateEmployee(
                @PathVariable Long id,
                @RequestBody Employee updatedEmployee,
                @CookieValue(value = "SESSIONID", required = false) String sessionId) {
            
            if (sessionId == null || sessionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session ID is missing"); // 401 Unauthorized
            }

            // Fetch admin employees and validate session ID
            Employee adminEmployee = employeeService.getEmployeeByFirstAndLastName("admin", "admin");
            if (adminEmployee == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin account not found"); // 403 Forbidden
            }

            boolean isValidAdmin = sessionId.equals(adminEmployee.getSessionId());
            if (!isValidAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid session ID or not an admin"); // 403 Forbidden
            }

            // Fetch the employee to be updated
            Employee existingEmployee = employeeService.getEmployeeById(id);
            if (existingEmployee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found"); // 404 Not Found
            }

            System.out.println("updated employee: "+updatedEmployee);
            // Validate updated data (Check for null fields)
            if (updatedEmployee.getFirstName() == null || updatedEmployee.getFirstName().isEmpty() ||
                updatedEmployee.getLastName() == null || updatedEmployee.getLastName().isEmpty() ||
                updatedEmployee.getCity() == null || updatedEmployee.getCity().isEmpty() ||
                updatedEmployee.getDepartment() == null || updatedEmployee.getDepartment().isEmpty()) {
            	 throw new InvalidEmployeeDataException("Invalid input: Fields cannot be empty");
            }

            // Check if any field is actually updated, else return 304 Not Modified
            if (existingEmployee.getFirstName().equals(updatedEmployee.getFirstName()) &&
                existingEmployee.getLastName().equals(updatedEmployee.getLastName()) &&
                existingEmployee.getCity().equals(updatedEmployee.getCity()) &&
                existingEmployee.getDepartment().equals(updatedEmployee.getDepartment())) {
            	return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }

            // Update employee details
            existingEmployee.setFirstName(updatedEmployee.getFirstName());
            existingEmployee.setLastName(updatedEmployee.getLastName());
            existingEmployee.setDepartment(updatedEmployee.getDepartment());
            existingEmployee.setCity(updatedEmployee.getCity());

            try {
                employeeService.updateEmployee(existingEmployee); // Save changes
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error occurred"); // 500 Internal Server Error
            }

            return ResponseEntity.ok("Employee updated successfully"); // 200 OK
        }


        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteEmployee(
                @PathVariable Long id,
                @CookieValue(value = "SESSIONID", required = false) String sessionId) {

            if (sessionId == null || sessionId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session ID is missing"); // 401 Unauthorized
            }

            // Fetch admin employees and validate session ID
            Employee adminEmployee = employeeService.getEmployeeByFirstAndLastName("admin", "admin");

            if (adminEmployee == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin account not found"); // 403 Forbidden
            }

            boolean isValidAdmin = sessionId.equals(adminEmployee.getSessionId());

            if (!isValidAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid session ID or not an admin"); // 403 Forbidden
            }

            // Fetch the employee to be deleted
            Employee existingEmployee = employeeService.getEmployeeById(id);
            if (existingEmployee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found"); // 404 Not Found
            }

            // Delete employee
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        }

        @DeleteMapping
        public ResponseEntity<String> deleteAllEmployees() {
            // Delete all employees
            employeeService.deleteAllEmployees();
            return ResponseEntity.ok("All employees deleted successfully");
        }

}
