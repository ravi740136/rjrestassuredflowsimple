package rj.restassured.flow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NeoEmployee {

    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String department;
    private int salary;
    private boolean active;
    private String email;
    private String joiningDate;
    
    private NeoAddress address; // Field name matches JSON, so no @JsonProperty needed

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJoiningDate() { return joiningDate; }
    public void setJoiningDate(String joiningDate) { this.joiningDate = joiningDate; }

    public NeoAddress getAddress() { return address; }
    public void setAddress(NeoAddress address) { this.address = address; }
}
