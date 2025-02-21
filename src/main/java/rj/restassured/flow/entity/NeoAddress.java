package rj.restassured.flow.entity;

public class NeoAddress {

    private String city;
    private String state; // Keep this if your JSON has "state"
    private String zip;   // Add this to match the JSON structure

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }
}

