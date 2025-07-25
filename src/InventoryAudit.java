// InventoryAudit.java
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InventoryAudit extends JFrame {
    private Connection conn;
    private DefaultTableModel tableModel;
    private String username;

    public InventoryAudit(String username) {
        this.username = username;
        initializeUI();
        connectToDatabase();
        loadInventoryData();
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
        setTitle("Inventory Audit - Staff Access");
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(
                new Object[]{"Audit ID", "Product", "Staff", "Old Qty", "New Qty", "Change Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable auditTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(auditTable);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton viewLowStockBtn = new JButton("View Low Stock");
        JButton backBtn = new JButton("Back to Dashboard");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewLowStockBtn);
        buttonPanel.add(backBtn);

        // Button actions
        refreshBtn.addActionListener(e -> loadInventoryData());

        viewLowStockBtn.addActionListener(e -> {
            try {
                String query = "SELECT p.name, i.current_stock " +
                        "FROM products p JOIN inventory_audit i ON p.product_id = i.product_id " +
                        "WHERE i.new_quantity < 5 " +  // Assuming 5 is low stock threshold
                        "GROUP BY p.product_id";

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                StringBuilder lowStockItems = new StringBuilder("Low Stock Items:\n");
                while (rs.next()) {
                    lowStockItems.append(rs.getString("name"))
                            .append(": ")
                            .append(rs.getInt("current_stock"))
                            .append("\n");
                }

                JOptionPane.showMessageDialog(this, lowStockItems.toString(),
                        "Low Stock Items", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading low stock items: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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

    private void loadInventoryData() {
        try {
            tableModel.setRowCount(0);

            String query = "SELECT a.audit_id, p.name AS product_name, u.name AS staff_name, " +
                    "a.old_quantity, a.new_quantity, a.change_date " +
                    "FROM inventory_audit a " +
                    "JOIN products p ON a.product_id = p.product_id " +
                    "JOIN users u ON a.staff_id = u.user_id " +
                    "ORDER BY a.change_date DESC";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("audit_id"),
                        rs.getString("product_name"),
                        rs.getString("staff_name"),
                        rs.getInt("old_quantity"),
                        rs.getInt("new_quantity"),
                        rs.getTimestamp("change_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading inventory audit: " + e.getMessage(),
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