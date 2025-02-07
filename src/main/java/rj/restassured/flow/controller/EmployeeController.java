package rj.restassured.flow.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import rj.restassured.flow.entity.Employee;
import rj.restassured.flow.service.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

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

    // Endpoint to get an employee by ID
    @GetMapping(value= "/{id}")
    		//produces = MediaType.APPLICATION_JSON_VALUE) 
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok().body(employeeService.getEmployeeById(id));
    }
}
