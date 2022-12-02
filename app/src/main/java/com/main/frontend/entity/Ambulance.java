package com.main.frontend.entity;

public class Ambulance {

    private String numberPlate;
    private String ambulanceType;

    public Ambulance() {}

    public Ambulance(String numberPlate, String ambulanceType) {
        this.numberPlate = numberPlate;
        this.ambulanceType = ambulanceType;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public String getAmbulanceType() {
        return ambulanceType;
    }
}

