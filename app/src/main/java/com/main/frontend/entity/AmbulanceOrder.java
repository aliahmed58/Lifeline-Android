package com.main.frontend.entity;

public class AmbulanceOrder {

    private String ambType;
    private String location;
    private String orderId;
    private String paymentMethod;
    private String spec;

    public AmbulanceOrder() {

    }

    public AmbulanceOrder(String ambType, String location, String orderId, String paymentMethod, String spec) {
        this.ambType = ambType;
        this.location = location;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.spec = spec;
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

    public String getSpec() {
        return spec;
    }
}
