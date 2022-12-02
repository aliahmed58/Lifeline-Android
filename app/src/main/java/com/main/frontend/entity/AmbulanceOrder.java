package com.main.frontend.entity;

import java.io.Serializable;

public class AmbulanceOrder implements Serializable {

    private String ambType;
    private String location;
    private String orderId;
    private String paymentMethod;
    private String spec;
    private String userid;
    private String driverId;
    private boolean accepted;

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public AmbulanceOrder() {

    }

    public AmbulanceOrder(String ambType, String location, String orderId, String paymentMethod, String spec, String userid, String driverId, boolean accepted) {
        this.ambType = ambType;
        this.location = location;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.spec = spec;
        this.userid = userid;
        this.driverId = driverId;
        this.accepted = accepted;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    public String getAmbType() {
        return ambType;
    }

    public String getLocation() {
        return location;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDriverId() {
        return driverId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getSpec() {
        return spec;
    }
}
