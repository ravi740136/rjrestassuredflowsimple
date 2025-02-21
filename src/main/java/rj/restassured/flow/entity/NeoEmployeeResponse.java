package rj.restassured.flow.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import java.util.List;

public class NeoEmployeeResponse {

    private List<NeoEmployee> employees;

    public List<NeoEmployee> getEmployees() { return employees; }
    public void setEmployees(List<NeoEmployee> employees) { this.employees = employees; }
}

