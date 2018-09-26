package service.models;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class User {

    private String id;
    private String email;
    private String name;
    private String password;
    private String role;
    private boolean canBook;

    public User() {
    }

    public User(String email, String name, String password, String role) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.canBook = true;
    }

    public User(String id, String email, String name, String password, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.canBook = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean getCanBook() {
        return canBook;
    }

    public void setCanBook(boolean canBook) {
        this.canBook = canBook;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() { return Objects.hash(this.id); }

    @Override
    public String toString() {
        return "model.User{id='" + this.id +
                "',email='" + this.email +
                "',password='" + this.password +
                "',name='" + this.email +
                "',canBook='" + this.canBook +
                "',role='" + this.role + "'}";
    }

    public String createToken() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 30);
        Algorithm alg = Algorithm.HMAC256("rest_sot_assignment");
        String token = JWT.create()
                .withIssuer("auth service")
                .withSubject(this.role)
                .withKeyId(this.id)
                .withExpiresAt(now.getTime())
                .sign(alg);
        return token;
    }

    public String authenticate(String password) {
        if (this.password.equals(password)) {
            return createToken();
        }
        return null;
    }
}
