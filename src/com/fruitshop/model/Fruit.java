package com.fruitshop.model;

import java.io.Serializable;
import java.time.LocalDateTime;


public class Fruit implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private double pricePerKg;
    private double quantity; // in kg
    private String unit;

    public Fruit(String id, String name, double pricePerKg, double quantity) {
        this.id = id;
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.quantity = quantity;
        this.unit = "kg";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return name + " - ₹" + pricePerKg + "/kg";
    }
}