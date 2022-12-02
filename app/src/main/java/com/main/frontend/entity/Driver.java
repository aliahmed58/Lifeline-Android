package com.main.frontend.entity;

import java.io.Serializable;

public class Driver extends User implements Serializable {
    private String ambID;

    public Driver() {
        super();
    }

    public Driver(String name, String phone, String userType, String ambId) {
        super(name, phone, userType);
        this.ambID = ambId;
    }

    public String getAmbID() {
        return ambID;
    }
}
