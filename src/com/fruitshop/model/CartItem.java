package com.fruitshop.model;

import java.io.Serializable;


public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Fruit fruit;
    private double quantity;

    public CartItem(Fruit fruit, double quantity) {
        this.fruit = fruit;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return fruit.getPricePerKg() * quantity;
    }

    public Fruit getFruit() {
        return fruit;
    }

    public double getQuantity() {
        return quantity;
    }

   
    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return fruit.getName() + " x " + quantity + " kg = ₹" + getTotalPrice();
    }
}