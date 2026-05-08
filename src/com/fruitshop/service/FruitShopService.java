package com.fruitshop.service;

import com.fruitshop.data.FileHandler;
import com.fruitshop.exception.*;
import com.fruitshop.model.Bill;
import com.fruitshop.model.CartItem;
import com.fruitshop.model.Fruit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FruitShopService {
    private List<Fruit> inventory;
    private List<CartItem> cart;
    private List<Bill> bills;
    private int billCounter;
    private String currentUser = "";
    private String currentRole = "";

    public FruitShopService() {
        this.inventory = FileHandler.loadInventory();
        this.bills = FileHandler.loadBills();
        this.cart = new ArrayList<>();
        this.billCounter = bills.size() + 1;
    }

    // ===== LOGIN FUNCTIONALITY =====
    /**
     * Validates user login credentials
     * @param username Username entered by user
     * @param password Password entered by user
     * @param role User role (Saler/Buyer)
     * @return true if login is valid, false otherwise
     * @throws InvalidInputException if username/password/role is empty
     */
    public boolean validateLogin(String username, String password, String role) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty.");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new InvalidInputException("Role must be selected.");
        }

        // Only accept letters and spaces in username
        if (!username.trim().matches("^[a-zA-Z\\s]+$")) {
            throw new InvalidInputException("Username must contain only letters and spaces. No numbers or symbols allowed.");
        }

        // Validate role is either Saler or Buyer
        if (!role.equals("Saler") && !role.equals("Buyer")) {
            throw new InvalidInputException("Invalid role. Only 'Saler' and 'Buyer' are allowed.");
        }

        // Simple authentication: accept any non-empty username and password
        // (In real application, this would verify against a database)
        this.currentUser = username.trim();
        this.currentRole = role;
        return true;
    }

    /**
     * Get current logged-in user
     */
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current user role
     */
    public String getCurrentRole() {
        return currentRole;
    }

    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = "";
        this.currentRole = "";
        this.cart.clear();
    }

    public List<Fruit> getInventory() {
        return new ArrayList<>(inventory);
    }

    public void addFruit(String name, double price, double quantity) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Fruit name cannot be empty.");
        }
        if (!name.trim().matches("^[a-zA-Z\\s]+$")) {
            throw new InvalidInputException(
                    "Fruit name must contain only letters (a-z, A-Z). No numbers or symbols allowed.");
        }
        if (price <= 0) {
            throw new InvalidInputException("Price must be greater than zero.");
        }
        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }

        boolean exists = inventory.stream()
                .anyMatch(f -> f.getName().equalsIgnoreCase(name.trim()));
        if (exists) {
            throw new InvalidInputException("Fruit with this name already exists.");
        }

        String id = "F" + String.format("%03d", inventory.size() + 1);
        inventory.add(new Fruit(id, name.trim(), price, quantity));
        FileHandler.saveInventory(inventory);
    }

    public void updateFruit(String id, double newQuantity) throws InvalidInputException {
        if (newQuantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }

        Fruit fruit = inventory.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Fruit not found."));

        fruit.setQuantity(newQuantity);
        FileHandler.saveInventory(inventory);
    }

    public void updateFruitDetails(String id, String newName, double newPrice, double newQuantity)
            throws InvalidInputException {
        if (newName == null || newName.trim().isEmpty()) {
            throw new InvalidInputException("Fruit name cannot be empty.");
        }
        if (!newName.trim().matches("^[a-zA-Z\\s]+$")) {
            throw new InvalidInputException(
                    "Fruit name must contain only letters (a-z, A-Z). No numbers or symbols allowed.");
        }
        if (newPrice <= 0) {
            throw new InvalidInputException("Price must be greater than zero.");
        }
        if (newQuantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }
        Fruit fruit = inventory.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Fruit not found."));
        fruit.setName(newName);
        fruit.setPricePerKg(newPrice);
        fruit.setQuantity(newQuantity);
        FileHandler.saveInventory(inventory);
    }

    public void deleteFruit(String id) throws InvalidInputException {
        boolean removed = inventory.removeIf(f -> f.getId().equals(id));
        if (!removed) {
            throw new InvalidInputException("Fruit not found.");
        }

        for (int i = 0; i < inventory.size(); i++) {
            inventory.get(i).setId("F" + String.format("%03d", i + 1));
        }
        FileHandler.saveInventory(inventory);
    }

    public List<Fruit> searchFruits(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getInventory();
        }
        return inventory.stream()
                .filter(f -> f.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Fruit> getLowStockFruits() {
        return inventory.stream()
                .filter(f -> f.getQuantity() < 5)
                .collect(Collectors.toList());
    }

    public void addToCart(Fruit fruit, double quantity) throws InvalidInputException, InsufficientStockException {
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero.");
        }
        if (quantity > fruit.getQuantity()) {
            throw new InsufficientStockException(fruit.getName(), quantity, fruit.getQuantity());
        }

        for (CartItem item : cart) {
            if (item.getFruit().getId().equals(fruit.getId())) {
                double newQty = item.getQuantity() + quantity;
                if (newQty > fruit.getQuantity()) {
                    throw new InsufficientStockException(fruit.getName(), newQty, fruit.getQuantity());
                }
                item.setQuantity(newQty);
                return;
            }
        }
        cart.add(new CartItem(fruit, quantity));
    }

    public void removeFromCart(String fruitId) throws InvalidInputException {
        boolean removed = cart.removeIf(item -> item.getFruit().getId().equals(fruitId));
        if (!removed) {
            throw new InvalidInputException("Item not found in cart.");
        }
    }

    public void clearCart() {
        cart.clear();
    }

    public List<CartItem> getCart() {
        return new ArrayList<>(cart);
    }

    public double getCartSubtotal() {
        return cart.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public Bill generateBill(String customerName, double discountPercent) throws EmptyCartException, InvalidInputException {
        if (cart.isEmpty()) {
            throw new EmptyCartException();
        }

        // Validate customer name: must contain only letters and spaces
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new InvalidInputException("Customer name cannot be empty.");
        }
        if (!customerName.trim().matches("^[a-zA-Z\\s]+$")) {
            throw new InvalidInputException("Customer name must contain only letters and spaces. No numbers or symbols allowed.");
        }

        String billNumber = generateBillNumber();
        Bill bill = new Bill(billNumber, customerName.trim());

        for (CartItem item : cart) {
            bill.addItem(item);

            Fruit fruit = inventory.stream()
                    .filter(f -> f.getId().equals(item.getFruit().getId()))
                    .findFirst()
                    .orElse(null);

            if (fruit != null) {
                fruit.setQuantity(fruit.getQuantity() - item.getQuantity());
            }
        }

        bill.setDiscount(discountPercent);

        FileHandler.saveBill(bill);
        FileHandler.saveInventory(inventory);
        bills.add(bill);

        cart.clear();

        return bill;
    }

    private String generateBillNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "FS-" + date + "-" + String.format("%04d", billCounter++);
    }

    public List<Bill> getBills() {
        return new ArrayList<>(bills);
    }

    public Bill getBillByNumber(String billNumber) {
        return bills.stream()
                .filter(b -> b.getBillNumber().equals(billNumber))
                .findFirst()
                .orElse(null);
    }

    public int getTotalItemsInStock() {
        return (int) inventory.stream()
                .filter(f -> f.getQuantity() > 0)
                .count();
    }

    public double getTodaySales() {
        LocalDateTime today = LocalDateTime.now();
        return bills.stream()
                .filter(b -> b.getDateTime().toLocalDate().equals(today.toLocalDate()))
                .mapToDouble(Bill::getTotal)
                .sum();
    }

    public int getTodayBillCount() {
        LocalDateTime today = LocalDateTime.now();
        return (int) bills.stream()
                .filter(b -> b.getDateTime().toLocalDate().equals(today.toLocalDate()))
                .count();
    }

    public List<Bill> getRecentBills(int limit) {
        return bills.stream()
                .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}