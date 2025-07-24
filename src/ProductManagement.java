// ProductManagement.java
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductManagement extends JFrame {
    private Connection conn;
    private DefaultTableModel tableModel;
    private String username;

    public ProductManagement(String username) {
        this.username = username;
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
        setTitle("Product Management - Staff Access");
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
        JButton updateStockBtn = new JButton("Update Stock");
        JButton backBtn = new JButton("Back to Dashboard");

        buttonPanel.add(refreshBtn);
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
            new StaffHome(username);
            dispose();
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