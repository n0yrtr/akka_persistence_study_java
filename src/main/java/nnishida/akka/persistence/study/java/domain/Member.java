package nnishida.akka.persistence.study.java.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Member implements Serializable {
    private final String id;
    private String name;
    private String email;

    public Member(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
