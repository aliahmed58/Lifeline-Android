package com.main.frontend.entity;

import java.io.Serializable;

public class BedOrder implements Serializable {

    private String orderId;
    private String spec;
    private String userId;
    private boolean accepted;
    private int age;
    private String hospId;
    public BedOrder() {

    }

    public String getHospId() {
        return hospId;
    }

    public void setHospId(String hospId) {
        this.hospId = hospId;
    }

    public BedOrder( String orderId, String spec, String userId, boolean accepted, int age, String hospId) {
        this.spec = spec;
        this.orderId = orderId;
        this.userId = userId;
        this.accepted = accepted;
        this.age = age;
        this.hospId = hospId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
