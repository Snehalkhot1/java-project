package com.fruitshop.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;

    private String billNumber;
    private LocalDateTime dateTime;
    private String customerName;
    private List<CartItem> items;
    private double subtotal;
    private double discountPercent;
    private double discountAmount;
    private double total;

    public Bill(String billNumber, String customerName) {
        this.billNumber = billNumber;
        this.dateTime = LocalDateTime.now();
        this.customerName = customerName != null && !customerName.trim().isEmpty() ? customerName : "Guest";
        this.items = new ArrayList<>();
        this.discountPercent = 0;
        this.discountAmount = 0;
        this.subtotal = 0;
        this.total = 0;
    }

    public void addItem(CartItem item) {
        items.add(item);
        calculateTotals();
    }

    public void calculateTotals() {
        subtotal = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        discountAmount = subtotal * (discountPercent / 100);
        total = subtotal - discountAmount;
    }

    public void setDiscount(double discountPercent) {
        this.discountPercent = discountPercent;
        calculateTotals();
    }

    public String generateBillText() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("                    FRUIT SHOP BILL                     \n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        sb.append("Bill No: ").append(billNumber).append("\n");
        sb.append("Date: ").append(dateTime.toLocalDate()).append("\n");
        sb.append("Time: ").append(dateTime.toLocalTime()).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("───────────────────────────────────────────────────────\n");
        sb.append(String.format("%-20s %8s %10s %12s\n", "Item", "Qty(kg)", "Rate(₹)", "Amount(₹)"));
        sb.append("───────────────────────────────────────────────────────\n");

        for (CartItem item : items) {
            sb.append(String.format("%-20s %8.2f %10.2f %12.2f\n",
                    item.getFruit().getName(),
                    item.getQuantity(),
                    item.getFruit().getPricePerKg(),
                    item.getTotalPrice()));
        }

        sb.append("───────────────────────────────────────────────────────\n");
        sb.append(String.format("%-40s %12.2f\n", "Subtotal:", subtotal));

        if (discountPercent > 0) {
            sb.append(String.format("%-40s %12.2f\n", "Discount (" + (int) discountPercent + "%):", -discountAmount));
        }

        sb.append("───────────────────────────────────────────────────────\n");
        sb.append(String.format("%-40s ₹%12.2f\n", "TOTAL:", total));
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("              Thank you for shopping with us!           \n");
        sb.append("                    Visit again!                        \n");

        return sb.toString();
    }

   
    public String getBillNumber() {
        return billNumber;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTotal() {
        return total;
    }
}