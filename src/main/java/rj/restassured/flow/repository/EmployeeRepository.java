package rj.restassured.flow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rj.restassured.flow.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	 Optional<List<Employee>> findByFirstNameAndLastName(String firstName, String lastName);
	    List<Employee> findByDepartmentAndCity(String department, String city);
	    List<Employee> findAll();	
}


