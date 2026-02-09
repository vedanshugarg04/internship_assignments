package com.example.multithreadingdemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String city;

    public Friend() {}

    public Friend(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
}
