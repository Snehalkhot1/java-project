package com.fruitshop.exception;


public class EmptyCartException extends Exception {
    public EmptyCartException(String message) {
        super(message);
    }

    public EmptyCartException() {
        super("Cart is empty. Please add items before proceeding.");
    }
}