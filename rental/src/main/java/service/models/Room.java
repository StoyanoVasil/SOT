package service.models;

import java.util.Objects;
import java.util.UUID;

public class Room {

    private String id;
    private String address;
    private String city;
    private String landlord;
    private int rent;
    private String status;
    private String tenant;

    public Room() {
    }

    public Room(String address, String city, String landlord, int rent) {
        this.id = UUID.randomUUID().toString();
        this.address = address;
        this.city = city;
        this.landlord = landlord;
        this.rent = rent;
        this.status = "free";
        this.tenant = "";
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getLandlord() {
        return landlord;
    }

    public void setLandlord(String landlord) {
        this.landlord = landlord;
    }

    public int getRent() { return rent; }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room user = (Room) o;
        return this.id.equals(user.id);
    }

    @Override
    public int hashCode() { return Objects.hash(this.address); }

    @Override
    public String toString() {
        return "model.Room{address='" + this.address +
                "',city='" + this.city +
                "',landlord='" + this.landlord +
                "',rent='" + this.rent +
                "',id='" + this.id+
                "',tenant='" + this.tenant+
                "',status='" + this.status + "'}";
    }

    public void book(String tenant) {
        setStatus("booked");
        setTenant(tenant);
    }

    public void rent() {
        setStatus("rented");
    }
}
