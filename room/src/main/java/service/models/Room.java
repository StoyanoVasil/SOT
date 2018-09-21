package service.models;

import java.util.Objects;

public class Room {

    private String address;
    private String landlord;
    private int rent;
    private String status;

    public Room() {
    }

    public Room(String address, String landlord, int rent, String status) {
        this.address = address;
        this.landlord = landlord;
        this.rent = rent;
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandlord() {
        return landlord;
    }

    public void setLandlord(String landlord) {
        this.landlord = landlord;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room user = (Room) o;
        return this.address.equals(user.address);
    }

    @Override
    public int hashCode() { return Objects.hash(this.address); }

    @Override
    public String toString() {
        return "model.Room{address='" + this.address +
                "',landlord='" + this.landlord +
                "',rent='" + this.rent +
                "',status='" + this.status + "'}";
    }
}
