// TransactionLog.java
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TransactionLog extends JFrame {
    private Connection conn;
    private DefaultTableModel tableModel;
    private String username;

    public TransactionLog(String username) {
        this.username = username;
        initializeUI();
        connectToDatabase();
        loadTransactionData();
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
        setTitle("Transaction Log - Staff Access");
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(
                new Object[]{"Transaction ID", "Order ID", "Payment Method", "Amount", "Status", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "pending", "completed", "failed"});
        JButton filterBtn = new JButton("Filter");
        JButton refreshBtn = new JButton("Refresh");
        JButton backBtn = new JButton("Back to Dashboard");

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(filterBtn);
        filterPanel.add(refreshBtn);
        filterPanel.add(backBtn);

        // Button actions
        filterBtn.addActionListener(e -> {
            String selectedStatus = (String) statusFilter.getSelectedItem();
            loadTransactionData(selectedStatus.equals("All") ? null : selectedStatus);
        });

        refreshBtn.addActionListener(e -> loadTransactionData());

        backBtn.addActionListener(e -> {
            new StaffHome(username);
            dispose();
        });

        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loadTransactionData() {
        loadTransactionData(null);
    }

    private void loadTransactionData(String statusFilter) {
        try {
            tableModel.setRowCount(0);
            
            String query = "SELECT t.transaction_id, t.order_id, t.payment_method, " +
                           "t.amount, t.payment_status, t.timestamp " +
                           "FROM transaction_log t " +
                           "JOIN orders o ON t.order_id = o.order_id ";
            
            if (statusFilter != null) {
                query += "WHERE t.payment_status = ? ";
            }
            
            query += "ORDER BY t.timestamp DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            
            if (statusFilter != null) {
                stmt.setString(1, statusFilter);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getInt("order_id"),
                    rs.getString("payment_method"),
                    String.format("₱%.2f", rs.getDouble("amount")),
                    rs.getString("payment_status"),
                    rs.getTimestamp("timestamp")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(),
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