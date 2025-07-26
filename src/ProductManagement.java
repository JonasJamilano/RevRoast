// ProductManagement.java
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductManagement extends JFrame {
    private Connection conn;
    private DefaultTableModel tableModel;
    private String username;
    private String role;

    public ProductManagement(String username, String role) {
        this.username = username;
        this.role = role;
        initializeUI();
        connectToDatabase();
        loadProductData();
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setTitle("Product Management - " + (role.equalsIgnoreCase("admin") ? "Admin" : "Staff") + " Access");
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Description", "Price", "Stock", "Currency"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Only ID column is non-editable
            }
        };

        JTable productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton addProductBtn = new JButton("Add Product");
        JButton deleteProductBtn = new JButton("Delete Product");
        JButton updateStockBtn = new JButton("Update Stock");
        JButton backBtn = new JButton("Back to Dashboard");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(addProductBtn);
        buttonPanel.add(deleteProductBtn);
        buttonPanel.add(updateStockBtn);
        buttonPanel.add(backBtn);

        // Button actions
        refreshBtn.addActionListener(e -> loadProductData());

        updateStockBtn.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to update");
                return;
            }

            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            String productName = (String) tableModel.getValueAt(selectedRow, 1);
            int currentStock = (int) tableModel.getValueAt(selectedRow, 4);

            String newStockStr = JOptionPane.showInputDialog(this,
                    "Enter new stock quantity for " + productName + " (Current: " + currentStock + ")");

            if (newStockStr != null && !newStockStr.isEmpty()) {
                try {
                    int newStock = Integer.parseInt(newStockStr);
                    updateProductStock(productId, newStock);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number");
                }
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            if (role.equalsIgnoreCase("admin")) {
                new AdminHome(username);
            } else {
                new StaffHome(username);
            }
        });

        addProductBtn.addActionListener(e -> {
            if (!role.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(this, "Only admin is allowed to add products.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
        
            JTextField nameField = new JTextField();
            JTextField descField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField stockField = new JTextField();
            JTextField currencyIdField = new JTextField();
        
            Object[] inputFields = {
                "Name:", nameField,
                "Description:", descField,
                "Price:", priceField,
                "Stock:", stockField,
                "Currency ID (1=PHP, 2=USD, 3=YEN):", currencyIdField
            };
        
            int option = JOptionPane.showConfirmDialog(this, inputFields, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText();
                    String description = descField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    int currencyId = Integer.parseInt(currencyIdField.getText());
        
                    CallableStatement stmt = conn.prepareCall("{CALL add_product(?, ?, ?, ?, ?)}");
                    stmt.setString(1, name);
                    stmt.setString(2, description);
                    stmt.setDouble(3, price);
                    stmt.setInt(4, stock);
                    stmt.setInt(5, currencyId);
        
                    stmt.execute();
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                    loadProductData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
                }
            }
        });

        deleteProductBtn.addActionListener(e -> {
            if (!role.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(this, "Only admin is allowed to delete products.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
        
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to delete");
                return;
            }
        
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            String productName = (String) tableModel.getValueAt(selectedRow, 1);
        
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + productName + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    CallableStatement stmt = conn.prepareCall("{CALL delete_product(?)}");
                    stmt.setInt(1, productId);
                    stmt.execute();
                    JOptionPane.showMessageDialog(this, "Product deleted successfully");
                    loadProductData();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadProductData() {
        try {
            tableModel.setRowCount(0);

            String query = "SELECT p.product_id, p.name, p.description, p.price, p.stock_quantity, c.currency_code " +
                    "FROM products p JOIN currencies c ON p.currency_id = c.currency_id";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        String.format("₱%.2f", rs.getDouble("price")),
                        rs.getInt("stock_quantity"),
                        rs.getString("currency_code")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateProductStock(int productId, int newStock) {
        try {
            CallableStatement stmt = conn.prepareCall("{call update_product_stock(?, ?, ?)}");
            stmt.setInt(1, productId);
            stmt.setInt(2, newStock);
            stmt.setString(3, username);

            stmt.execute();
            JOptionPane.showMessageDialog(this, "Stock updated successfully");
            loadProductData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating stock: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }
}