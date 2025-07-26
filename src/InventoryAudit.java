import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryAudit extends JFrame {
    private String username;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryAudit(String username) {
        this.username = username;
        initializeUI();
        loadInventory();
    }

    private void initializeUI() {
        setTitle("Inventory Audit");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Inventory Audit", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Playfair Display", Font.BOLD, 28));
        headerLabel.setForeground(new Color(252, 65, 17));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Table with columns
        String[] columns = {"Product ID", "Name", "Description", "Price", "Stock Quantity", "Currency ID"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JPanel buttonPanel = new JPanel();
        JButton backBtn = createStyledButton("Back");
        buttonPanel.add(backBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Back Action
        backBtn.addActionListener(e -> {
            dispose();
            new AdminHome(username);
        });
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(252, 17, 17));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(220, 0, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(252, 17, 17));
            }
        });
        return btn;
    }

    private void loadInventory() {
        tableModel.setRowCount(0); // Clear table before loading
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT product_id, name, description, price, stock_quantity, currency_id FROM products")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("currency_id")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading inventory: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
