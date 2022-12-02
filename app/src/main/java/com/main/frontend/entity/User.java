package com.main.frontend.entity;


import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String phone;
    private String userType;

    public User () {

    }
    public User(String name, String phone, String userType) {
        this.name = name;
        this.phone = phone;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserType() {
        return userType;
    }
}
