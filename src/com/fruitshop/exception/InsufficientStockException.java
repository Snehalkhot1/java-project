package com.fruitshop.exception;


public class InsufficientStockException extends Exception {
    private String fruitName;
    private double requested;
    private double available;

    public InsufficientStockException(String fruitName, double requested, double available) {
        super(String.format("Insufficient stock for %s. Requested: %.2f kg, Available: %.2f kg",
                fruitName, requested, available));
        this.fruitName = fruitName;
        this.requested = requested;
        this.available = available;
    }

    public String getFruitName() {
        return fruitName;
    }

    public double getRequested() {
        return requested;
    }

    public double getAvailable() {
        return available;
    }
}