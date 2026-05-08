package com.fruitshop.data;

import com.fruitshop.model.Fruit;
import com.fruitshop.model.Bill;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String INVENTORY_FILE = "inventory.dat";
    private static final String BILLS_FILE = "bills.dat";

    // Inventory Operations
    public static void saveInventory(List<Fruit> inventory) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE))) {
            oos.writeObject(inventory);
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Fruit> loadInventory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INVENTORY_FILE))) {
            return (List<Fruit>) ois.readObject();
        } catch (FileNotFoundException e) {
            return getDefaultInventory();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
            return getDefaultInventory();
        }
    }

    // Bill Operations
    public static void saveBill(Bill bill) {
        List<Bill> bills = loadBills();
        bills.add(bill);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BILLS_FILE))) {
            oos.writeObject(bills);
        } catch (IOException e) {
            System.err.println("Error saving bill: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Bill> loadBills() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BILLS_FILE))) {
            return (List<Bill>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading bills: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void exportBill(Bill bill, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(bill.generateBillText());
        } catch (IOException e) {
            System.err.println("Error exporting bill: " + e.getMessage());
        }
    }

    // Default inventory for new installations
    private static List<Fruit> getDefaultInventory() {
        List<Fruit> defaultInventory = new ArrayList<>();
        defaultInventory.add(new Fruit("F001", "Apple", 150, 50));
        defaultInventory.add(new Fruit("F002", "Banana", 40, 100));
        defaultInventory.add(new Fruit("F003", "Orange", 80, 60));
        defaultInventory.add(new Fruit("F004", "Grapes", 120, 40));
        defaultInventory.add(new Fruit("F005", "Mango", 100, 80));
        defaultInventory.add(new Fruit("F006", "Papaya", 50, 30));
        defaultInventory.add(new Fruit("F007", "Watermelon", 30, 50));
        defaultInventory.add(new Fruit("F008", "Pineapple", 60, 40));
        defaultInventory.add(new Fruit("F009", "Pomegranate", 150, 25));
        defaultInventory.add(new Fruit("F010", "Guava", 45, 35));
        return defaultInventory;
    }
}