package com.fruitshop.ui;

import com.fruitshop.exception.*;
import com.fruitshop.model.*;
import com.fruitshop.service.FruitShopService;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class FruitShopGUI extends JFrame {
    private String userRole = "";
    private FruitShopService service;

    // Improved color scheme for better visibility
    private static final Color PRIMARY = new Color(34, 139, 34);        // Forest Green
    private static final Color SECONDARY = new Color(70, 180, 70);     // Bright Green
    private static final Color ACCENT = new Color(255, 165, 0);       // Orange
    private static final Color BG_COLOR = new Color(240, 250, 240);   // Honeydew
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BUTTON_TEXT = Color.WHITE;
    private static final Color SUCCESS_COLOR = new Color(34, 139, 34);   // Green
    private static final Color ERROR_COLOR = new Color(220, 20, 60);     // Crimson Red
    private static final Color WARNING_COLOR = new Color(255, 140, 0);   // Dark Orange
    private static final Color INFO_COLOR = new Color(30, 144, 255);     // Dodger Blue

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private DefaultTableModel inventoryTableModel;
    private DefaultTableModel cartTableModel;
    private DefaultTableModel billHistoryTableModel;
    private List<CartItem> currentCart;
    private JButton selectedNavButton;

    public FruitShopGUI() {
        // Show login dialog first
        showLoginDialog();
        if (!userRole.isEmpty()) {
            service = new FruitShopService();
            currentCart = new ArrayList<>();
            initializeUI();
        } else {
            // If login cancelled, exit
            System.exit(0);
        }
    }

    private void showLoginDialog() {
        JDialog dialog = new JDialog((Frame) null, "Login", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(BG_COLOR);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JTextField userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userField.setPreferredSize(new Dimension(200, 28));
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passField.setPreferredSize(new Dimension(200, 28));
        
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JComboBox<String> roleBox = new JComboBox<>(new String[] { "Saler", "Buyer" });
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleBox.setPreferredSize(new Dimension(200, 28));

        form.add(userLabel);
        form.add(userField);
        form.add(passLabel);
        form.add(passField);
        form.add(roleLabel);
        form.add(roleBox);

        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttons.setBackground(PANEL_BG);
        
        JButton loginBtn = new JButton("LOGIN");
        JButton cancelBtn = new JButton("CANCEL");

        // Style login button - GREEN
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(PRIMARY);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setBorderPainted(true);
        loginBtn.setPreferredSize(new Dimension(130, 40));
        
        // Add hover effect to login button
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(SECONDARY);
            }
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(PRIMARY);
            }
        });

        // Style cancel button - RED
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(ERROR_COLOR);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        cancelBtn.setOpaque(true);
        cancelBtn.setContentAreaFilled(true);
        cancelBtn.setBorderPainted(true);
        cancelBtn.setPreferredSize(new Dimension(130, 40));
        
        // Add hover effect to cancel button
        cancelBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(new Color(200, 10, 40));
            }
            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(ERROR_COLOR);
            }
        });

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            // Simple validation: require non-empty username/password
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter username and password.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            userRole = role;
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> {
            userRole = "";
            dialog.dispose();
        });
        
        buttons.add(loginBtn);
        buttons.add(cancelBtn);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void initializeUI() {
        setTitle("Fruit Shop Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);

        mainPanel.add(createDashboardView(), "DASHBOARD");
        mainPanel.add(createInventoryView(), "INVENTORY");
        mainPanel.add(createNewSaleView(), "NEWSALE");
        mainPanel.add(createBillsView(), "BILLS");

        add(createNavigationPanel(), BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        cardLayout.show(mainPanel, "DASHBOARD");

        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("🍎 Fruit Shop Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        header.add(dateLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(PRIMARY);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setPreferredSize(new Dimension(200, 0));

        String[] buttons = { "Dashboard", "Inventory", "New Sale", "View Bills" };
        String[] commands = { "DASHBOARD", "INVENTORY", "NEWSALE", "BILLS" };

        JButton firstNavBtn = null;
        for (int i = 0; i < buttons.length; i++) {
            JButton btn = createNavButton(buttons[i], commands[i]);
            if (i == 0) {
                firstNavBtn = btn;
            }
            navPanel.add(btn);
            navPanel.add(Box.createVerticalStrut(10));
        }

        if (firstNavBtn != null) {
            selectNavButton(firstNavBtn);
        }

        navPanel.add(Box.createVerticalGlue());

        JButton exitBtn = createNavButton("Exit", "EXIT");
        exitBtn.setBackground(ERROR_COLOR);
        exitBtn.setContentAreaFilled(true);
        exitBtn.setBorderPainted(true);
        exitBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                exitBtn.setBackground(new Color(200, 10, 40));
            }

            public void mouseExited(MouseEvent e) {
                exitBtn.setBackground(ERROR_COLOR);
            }
        });
        navPanel.add(exitBtn);

        return navPanel;
    }

    private JButton createNavButton(String text, String command) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(getContrastColor(PRIMARY));
        btn.setBackground(PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 50));
        btn.setActionCommand(command);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.addActionListener(e -> {
            if (command.equals("EXIT")) {
                System.exit(0);
            } else {
                selectNavButton(btn);
                cardLayout.show(mainPanel, command);
                refreshCurrentView(command);
            }
        });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != selectedNavButton) {
                    btn.setBackground(SECONDARY);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (btn != selectedNavButton) {
                    btn.setBackground(PRIMARY);
                }
            }
        });

        return btn;
    }

    private void refreshCurrentView(String view) {
        switch (view) {
            case "DASHBOARD":
                updateDashboard();
                break;
            case "INVENTORY":
                updateInventoryTable();
                break;
            case "NEWSALE":
                updateFruitList();
                break;
            case "BILLS":
                updateBillHistory();
                break;
        }
    }

    // ==================== DASHBOARD VIEW ====================
    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 2, 15, 15));
        content.setBackground(BG_COLOR);

        content.add(createStatCard("Today's Sales", "₹0.00", "Total revenue today", SUCCESS_COLOR));

        content.add(createStatCard("Items in Stock", "0", "Different fruits available", ACCENT));

        content.add(createStatCard("Today's Bills", "0", "Transactions today", INFO_COLOR));

        content.add(createStatCard("Low Stock Alert", "0", "Items below 5kg", ERROR_COLOR));

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setName(title);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(color);
        top.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        top.add(titleLabel, BorderLayout.WEST);

        card.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(PANEL_BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(Color.GRAY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(valueLabel);
        center.add(Box.createVerticalStrut(5));
        center.add(subLabel);
        center.add(Box.createVerticalGlue());

        card.add(center, BorderLayout.CENTER);

        return card;
    }

    private void updateDashboard() {
        JPanel dashboardPanel = (JPanel) mainPanel.getComponent(0);
        JPanel contentPanel = (JPanel) dashboardPanel.getComponent(1);

        Component[] cards = contentPanel.getComponents();

        for (Component comp : cards) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel) comp;
                String title = card.getName();
                if (title != null) {

                    JPanel center = (JPanel) card.getComponent(1);
                    JLabel valueLabel = (JLabel) center.getComponent(1);

                    switch (title) {
                        case "Today's Sales":
                            valueLabel.setText("₹" + String.format("%.2f", service.getTodaySales()));
                            break;
                        case "Items in Stock":
                            valueLabel.setText(String.valueOf(service.getTotalItemsInStock()));
                            break;
                        case "Today's Bills":
                            valueLabel.setText(String.valueOf(service.getTodayBillCount()));
                            break;
                        case "Low Stock Alert":
                            valueLabel.setText(String.valueOf(service.getLowStockFruits().size()));
                            break;
                    }
                }
            }
        }
    }

    // ==================== INVENTORY VIEW ====================
    private JPanel createInventoryView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Inventory Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        top.add(title, BorderLayout.WEST);

        JButton addBtn = createButton("+ Add New Fruit", PRIMARY);
        addBtn.addActionListener(e -> showAddFruitDialog());
        top.add(addBtn, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);

        String[] columns = { "ID", "Name", "Price/kg (₹)", "Quantity (kg)", "Status" };
        inventoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(inventoryTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(PRIMARY);
                c.setForeground(Color.WHITE);
                ((JComponent) c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(SECONDARY));

        panel.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.setBackground(BG_COLOR);

        JButton updateBtn = createButton("Update Quantity", ACCENT);
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                showUpdateQuantityDialog(row);
            } else {
                showError("Please select a fruit to update.");
            }
        });

        JButton deleteBtn = createButton("Delete Fruit", ERROR_COLOR);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) inventoryTableModel.getValueAt(row, 0);
                confirmDelete(id);
            } else {
                showError("Please select a fruit to delete.");
            }
        });

        JButton refreshBtn = createButton("Refresh", INFO_COLOR);
        refreshBtn.addActionListener(e -> updateInventoryTable());

        actions.add(updateBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        panel.add(actions, BorderLayout.SOUTH);

        updateInventoryTable();
        return panel;
    }

    private void updateInventoryTable() {
        inventoryTableModel.setRowCount(0);
        for (Fruit fruit : service.getInventory()) {
            String status;
            if (fruit.getQuantity() == 0) {
                status = "❌ Unavailable";
            } else if (fruit.getQuantity() < 5) {
                status = "⚠ Low Stock";
            } else {
                status = "✓ In Stock";
            }
            inventoryTableModel.addRow(new Object[] {
                    fruit.getId(),
                    fruit.getName(),
                    fruit.getPricePerKg(),
                    fruit.getQuantity(),
                    status
            });
        }
    }

    private void showAddFruitDialog() {
        JDialog dialog = new JDialog(this, "Add New Fruit", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(BG_COLOR);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField = new JTextField();

        form.add(createLabel("Fruit Name:"));
        form.add(nameField);
        form.add(createLabel("Price per kg (₹):"));
        form.add(priceField);
        form.add(createLabel("Quantity (kg):"));
        form.add(qtyField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.setBackground(PANEL_BG);

        JButton saveBtn = createButton("Save", PRIMARY);
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                String qtyText = qtyField.getText().trim();

                // Validate name: only letters (no numbers or special chars)
                if (!name.matches("[a-zA-Z ]+")) {
                    showError("Fruit name must contain only letters.");
                    return;
                }

                // Validate price and quantity: only numbers (allow decimal)
                if (!priceText.matches("\\d+(\\.\\d+)?")) {
                    showError("Price must be a valid number.");
                    return;
                }
                if (!qtyText.matches("\\d+(\\.\\d+)?")) {
                    showError("Quantity must be a valid number.");
                    return;
                }

                double price = Double.parseDouble(priceText);
                double qty = Double.parseDouble(qtyText);

                service.addFruit(name, price, qty);
                updateInventoryTable();
                dialog.dispose();
                showSuccess("Fruit added successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for price and quantity.");
            } catch (InvalidInputException ex) {
                showError(ex.getMessage());
            }
        });

        JButton cancelBtn = createButton("Cancel", WARNING_COLOR);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUpdateQuantityDialog(int row) {
        String id = (String) inventoryTableModel.getValueAt(row, 0);
        String name = (String) inventoryTableModel.getValueAt(row, 1);
        double price = (double) inventoryTableModel.getValueAt(row, 2);
        double currentQty = (double) inventoryTableModel.getValueAt(row, 3);

        JDialog dialog = new JDialog(this, "Update Fruit Details", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(BG_COLOR);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        form.add(new JLabel("Fruit Name:"));
        JTextField nameField = new JTextField(name);
        form.add(nameField);
        form.add(new JLabel("Price per kg (₹):"));
        JTextField priceField = new JTextField(String.valueOf(price));
        form.add(priceField);
        form.add(new JLabel("Quantity (kg):"));
        JTextField qtyField = new JTextField(String.valueOf(currentQty));
        form.add(qtyField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.setBackground(PANEL_BG);

        JButton updateBtn = createButton("Update", PRIMARY);
        updateBtn.addActionListener(e -> {
            try {
                String newName = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                String qtyText = qtyField.getText().trim();

                // Validate name: only letters (no numbers or special chars)
                if (!newName.matches("[a-zA-Z ]+")) {
                    showError("Fruit name must contain only letters.");
                    return;
                }
                // Validate price and quantity: only numbers (allow decimal)
                if (!priceText.matches("\\d+(\\.\\d+)?")) {
                    showError("Price must be a valid number.");
                    return;
                }
                if (!qtyText.matches("\\d+(\\.\\d+)?")) {
                    showError("Quantity must be a valid number.");
                    return;
                }

                double newPrice = Double.parseDouble(priceText);
                double newQty = Double.parseDouble(qtyText);

                // Update all details (assuming service has updateFruitDetails method)
                boolean nameChanged = !newName.equals(name);
                boolean priceChanged = newPrice != price;
                boolean qtyChanged = newQty != currentQty;

                // If only quantity changed, use existing method
                if (!nameChanged && !priceChanged) {
                    service.updateFruit(id, newQty);
                } else {
                    // Otherwise, update all details (implement this in service/model as needed)
                    service.updateFruitDetails(id, newName, newPrice, newQty);
                }
                updateInventoryTable();
                dialog.dispose();
                showSuccess("Fruit details updated successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for price and quantity.");
            } catch (InvalidInputException ex) {
                showError(ex.getMessage());
            }
        });

        JButton cancelBtn = createButton("Cancel", WARNING_COLOR);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttons.add(updateBtn);
        buttons.add(cancelBtn);

        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void confirmDelete(String id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this fruit?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteFruit(id);
                updateInventoryTable();
                showSuccess("Fruit deleted successfully!");
            } catch (InvalidInputException ex) {
                showError(ex.getMessage());
            }
        }
    }

    // ==================== NEW SALE VIEW ====================
    private JPanel createNewSaleView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("New Sale");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(400);
        split.setResizeWeight(0.4);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(BG_COLOR);

        JLabel fruitListLabel = new JLabel("Available Fruits");
        fruitListLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fruitListLabel.setForeground(PRIMARY);
        leftPanel.add(fruitListLabel, BorderLayout.NORTH);

        JPanel fruitListPanel = new JPanel();
        fruitListPanel.setLayout(new BoxLayout(fruitListPanel, BoxLayout.Y_AXIS));
        fruitListPanel.setBackground(PANEL_BG);
        fruitListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane fruitScroll = new JScrollPane(fruitListPanel);
        fruitScroll.setBorder(BorderFactory.createLineBorder(SECONDARY));

        leftPanel.add(fruitScroll, BorderLayout.CENTER);

        JPanel addToCartPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        addToCartPanel.setBackground(PANEL_BG);
        addToCartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel qtyLabel = new JLabel("Quantity (kg):");
        JTextField qtyField = new JTextField("1");

        JButton addToCartBtn = createButton("Add to Cart", PRIMARY);
        addToCartBtn.addActionListener(e -> {

            Component[] comps = fruitListPanel.getComponents();
            for (Component comp : comps) {
                if (comp instanceof JPanel) {
                    JPanel fruitPanel = (JPanel) comp;
                    JCheckBox check = (JCheckBox) fruitPanel.getComponent(0);
                    if (check.isSelected()) {
                        try {
                            String fruitInfo = ((JLabel) fruitPanel.getComponent(1)).getText();
                            String fruitName = fruitInfo.split(" - ")[0];
                            double qty = Double.parseDouble(qtyField.getText().trim());

                            Fruit fruit = service.getInventory().stream()
                                    .filter(f -> f.getName().equals(fruitName))
                                    .findFirst().orElse(null);

                            if (fruit != null) {
                                service.addToCart(fruit, qty);
                                updateCartTable();
                                check.setSelected(false);
                                qtyField.setText("1");
                                showSuccess("Added to cart!");
                            }
                        } catch (NumberFormatException ex) {
                            showError("Please enter a valid quantity.");
                        } catch (InvalidInputException | InsufficientStockException ex) {
                            showError(ex.getMessage());
                        }
                        break;
                    }
                }
            }
        });

        addToCartPanel.add(new JLabel());
        addToCartPanel.add(qtyField);
        addToCartPanel.add(new JLabel());
        addToCartPanel.add(addToCartBtn);

        leftPanel.add(addToCartPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(BG_COLOR);

        String[] cartColumns = { "Fruit", "Qty (kg)", "Rate (₹)", "Amount (₹)" };
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable cartTable = new JTable(cartTableModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setRowHeight(22);

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        rightPanel.add(cartScroll, BorderLayout.CENTER);

        JPanel billingPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        billingPanel.setBackground(PANEL_BG);
        billingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        billingPanel.add(createLabel("Customer Name:"));
        JTextField customerField = new JTextField();

        billingPanel.add(createLabel("Discount:"));
        JComboBox<String> discountCombo = new JComboBox<>(new String[] { "0%", "5%", "10%", "15%", "20%" });

        billingPanel.add(createLabel("Subtotal:"));
        JLabel subtotalLabel = new JLabel("₹0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        billingPanel.add(createLabel("Total:"));
        JLabel totalLabel = new JLabel("₹0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY);

        rightPanel.add(billingPanel, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actions.setBackground(BG_COLOR);

        JButton generateBtn = createButton("Generate Bill", ACCENT);
        generateBtn.addActionListener(e -> {
            try {
                String customer = customerField.getText().trim();
                if (customer.isEmpty()) {
                    showError("Customer name is required.");
                    return;
                }
                String discountStr = (String) discountCombo.getSelectedItem();
                double discount = Double.parseDouble(discountStr.replace("%", ""));

                Bill bill = service.generateBill(customer, discount);

                showBillDialog(bill);

                customerField.setText("");
                discountCombo.setSelectedIndex(0);
                updateCartTable();
                updateFruitList();
                showSuccess("Bill generated successfully!");
            } catch (EmptyCartException ex) {
                showError("Cart is empty. Please add items first.");
            } catch (Exception ex) {
                showError("Error generating bill: " + ex.getMessage());
            }
        });

        JButton clearBtn = createButton("Clear Cart", ERROR_COLOR);
        clearBtn.addActionListener(e -> {
            service.clearCart();
            updateCartTable();
            showSuccess("Cart cleared!");
        });

        JButton removeBtn = createButton("Remove Selected", WARNING_COLOR);
        removeBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row >= 0) {
                String fruitName = (String) cartTableModel.getValueAt(row, 0);
                Fruit fruit = service.getInventory().stream()
                        .filter(f -> f.getName().equals(fruitName))
                        .findFirst().orElse(null);
                if (fruit != null) {
                    try {
                        service.removeFromCart(fruit.getId());
                        updateCartTable();
                    } catch (InvalidInputException ex) {
                        showError(ex.getMessage());
                    }
                }
            } else {
                showError("Please select an item to remove.");
            }
        });

        actions.add(generateBtn);
        actions.add(removeBtn);
        actions.add(clearBtn);

        rightPanel.add(actions, BorderLayout.NORTH);

        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void updateFruitList() {
        JPanel newSalePanel = null;
        for (Component c : mainPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                Component[] comps = p.getComponents();
                for (Component comp : comps) {
                    if (comp instanceof JSplitPane) {
                        newSalePanel = p;
                        break;
                    }
                }
            }
        }

        if (newSalePanel == null)
            return;

        // Get the split pane
        JSplitPane split = null;
        for (Component c : newSalePanel.getComponents()) {
            if (c instanceof JSplitPane) {
                split = (JSplitPane) c;
                break;
            }
        }

        if (split == null)
            return;

        JPanel leftPanel = (JPanel) split.getLeftComponent();
        JScrollPane scroll = null;
        for (Component c : leftPanel.getComponents()) {
            if (c instanceof JScrollPane) {
                scroll = (JScrollPane) c;
                break;
            }
        }

        if (scroll == null)
            return;

        JPanel fruitListPanel = (JPanel) scroll.getViewport().getView();

        for (Fruit fruit : service.getInventory()) {
            if (fruit.getQuantity() > 0) {
                JPanel fruitPanel = new JPanel(new BorderLayout(10, 5));
                fruitPanel.setBackground(PANEL_BG);
                fruitPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JCheckBox check = new JCheckBox();
                JLabel info = new JLabel(fruit.getName() + " - ₹" + fruit.getPricePerKg() + "/kg ("
                        + fruit.getQuantity() + " kg available)");
                info.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                fruitPanel.add(check, BorderLayout.WEST);
                fruitPanel.add(info, BorderLayout.CENTER);

                fruitListPanel.add(fruitPanel);
                fruitListPanel.add(Box.createVerticalStrut(5));
            }
        }

        fruitListPanel.revalidate();
        fruitListPanel.repaint();
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double subtotal = 0;

        for (CartItem item : service.getCart()) {
            cartTableModel.addRow(new Object[] {
                    item.getFruit().getName(),
                    item.getQuantity(),
                    item.getFruit().getPricePerKg(),
                    item.getTotalPrice()
            });
            subtotal += item.getTotalPrice();
        }

        JPanel newSalePanel = null;
        for (Component c : mainPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                Component[] panelComps = p.getComponents();
                for (Component comp : panelComps) {
                    if (comp instanceof JSplitPane) {
                        newSalePanel = p;
                        break;
                    }
                }
            }
        }

        if (newSalePanel == null)
            return;

        JSplitPane split = null;
        for (Component c : newSalePanel.getComponents()) {
            if (c instanceof JSplitPane) {
                split = (JSplitPane) c;
                break;
            }
        }

        if (split == null)
            return;

        JPanel right = (JPanel) split.getRightComponent();
        JPanel billing = (JPanel) right.getComponent(1);

        JLabel subtotalLabel = (JLabel) billing.getComponent(2);
        JLabel totalLabel = (JLabel) billing.getComponent(3);

        subtotalLabel.setText("₹" + String.format("%.2f", subtotal));
        totalLabel.setText("₹" + String.format("%.2f", subtotal));
    }

    private void showBillDialog(Bill bill) {
        JDialog dialog = new JDialog(this, "Bill Generated", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(bill.generateBillText());
        textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(textArea);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton printBtn = createButton("Print", PRIMARY);
        printBtn.addActionListener(e -> {
            try {
                textArea.print();
            } catch (Exception ex) {
                showError("Error printing: " + ex.getMessage());
            }
        });

        JButton exportBtn = createButton("Export to File", ACCENT);
        exportBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".txt")) {
                    path += ".txt";
                }
                com.fruitshop.data.FileHandler.exportBill(bill, path);
                showSuccess("Bill exported successfully!");
            }
        });

        JButton closeBtn = createButton("Close", WARNING_COLOR);
        closeBtn.addActionListener(e -> dialog.dispose());

        buttons.add(printBtn);
        buttons.add(exportBtn);
        buttons.add(closeBtn);

        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ==================== BILLS VIEW ====================
    private JPanel createBillsView() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Bill History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = { "Bill No", "Date", "Customer", "Items", "Total (₹)" };
        billHistoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(billHistoryTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(SECONDARY));

        panel.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actions.setBackground(BG_COLOR);

        JButton viewBtn = createButton("View Details", PRIMARY);
        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String billNo = (String) billHistoryTableModel.getValueAt(row, 0);
                Bill bill = service.getBillByNumber(billNo);
                if (bill != null) {
                    showBillDialog(bill);
                }
            } else {
                showError("Please select a bill to view.");
            }
        });

        JButton refreshBtn = createButton("Refresh", INFO_COLOR);
        refreshBtn.addActionListener(e -> updateBillHistory());

        actions.add(viewBtn);
        actions.add(refreshBtn);

        panel.add(actions, BorderLayout.SOUTH);

        updateBillHistory();
        return panel;
    }

    private void updateBillHistory() {
        billHistoryTableModel.setRowCount(0);

        for (Bill bill : service.getBills()) {
            billHistoryTableModel.addRow(new Object[] {
                    bill.getBillNumber(),
                    bill.getDateTime().toLocalDate(),
                    bill.getCustomerName(),
                    bill.getItems().size(),
                    bill.getTotal()
            });
        }
    }

    // ==================== STATUS BAR ====================
    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(SECONDARY);
        status.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel label = new JLabel("Ready");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Color.WHITE);

        status.add(label, BorderLayout.WEST);

        return status;
    }

    // ==================== HELPER METHODS ====================
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(getContrastColor(color));
        btn.setBackground(color);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        
        // Add hover effect
        btn.addMouseListener(new MouseAdapter() {
            private Color originalColor = color;
            
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(
                    Math.min(255, color.getRed() + 30),
                    Math.min(255, color.getGreen() + 30),
                    Math.min(255, color.getBlue() + 30)
                ));
            }
            
            public void mouseExited(MouseEvent e) {
                btn.setBackground(originalColor);
            }
        });
        
        return btn;
    }

    private Color getContrastColor(Color background) {
        int brightness = (int) Math.sqrt(
                background.getRed() * background.getRed() * 0.299
                + background.getGreen() * background.getGreen() * 0.587
                + background.getBlue() * background.getBlue() * 0.114);
        return brightness > 130 ? Color.BLACK : Color.WHITE;
    }

    private void selectNavButton(JButton btn) {
        if (selectedNavButton != null && selectedNavButton != btn) {
            resetNavButtonStyle(selectedNavButton);
        }
        selectedNavButton = btn;
        selectedNavButton.setBackground(Color.WHITE);
        selectedNavButton.setForeground(Color.BLACK);
    }

    private void resetNavButtonStyle(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(getContrastColor(PRIMARY));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new FruitShopGUI());
    }
}