import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class ProcessOrders extends JFrame {
    private final Color PANEL_BG = new Color(25, 25, 25);
    private final Color DARK_GRAY = new Color(40, 40, 40);
    private final Color LIGHT_GRAY = new Color(100, 100, 100);
    private final Color RACING_RED = new Color(230, 0, 0);
    private final Color SUCCESS_GREEN = new Color(0, 180, 0);

    private Font FONT_BODY;
    private Font FONT_HEADER;
    private Font FONT_BUTTON;

    private String username;
    private JTable ordersTable;
    private JButton completeOrderBtn;
    private JButton refreshBtn;

    public ProcessOrders(String username) {
        this.username = username;
        initializeFonts();
        setupWindow();
        buildUI();
        loadPendingOrders();
    }

    private void initializeFonts() {
        try {
            FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
            FONT_HEADER = new Font("Arial", Font.BOLD, 18);
            FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
        } catch (Exception e) {
            // Fallback to default fonts
            FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
            FONT_HEADER = new Font("SansSerif", Font.BOLD, 18);
            FONT_BUTTON = new Font("SansSerif", Font.BOLD, 12);
        }
    }

    private void setupWindow() {
        setTitle("REV & ROAST - Process Orders");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PANEL_BG);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel title = new JLabel("PROCESS ORDERS - Staff: " + username);
        title.setFont(FONT_HEADER);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BG);
        headerPanel.add(title, BorderLayout.WEST);

        // Refresh button
        refreshBtn = new JButton("REFRESH");
        refreshBtn.setFont(FONT_BUTTON);
        refreshBtn.setBackground(DARK_GRAY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        refreshBtn.addActionListener(e -> loadPendingOrders());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        ordersTable = new JTable();
        ordersTable.setFont(FONT_BODY);
        ordersTable.setForeground(Color.WHITE);
        ordersTable.setBackground(DARK_GRAY);
        ordersTable.setGridColor(LIGHT_GRAY);
        ordersTable.setRowHeight(30);
        ordersTable.setSelectionBackground(RACING_RED);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_BG);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerPanel.setBackground(PANEL_BG);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        completeOrderBtn = new JButton("COMPLETE ORDER");
        completeOrderBtn.setFont(FONT_BUTTON);
        completeOrderBtn.setBackground(SUCCESS_GREEN);
        completeOrderBtn.setForeground(Color.WHITE);
        completeOrderBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        completeOrderBtn.addActionListener(e -> completeSelectedOrder());
        footerPanel.add(completeOrderBtn);

        JButton backBtn = new JButton("BACK");
        backBtn.setFont(FONT_BUTTON);
        backBtn.setBackground(DARK_GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        backBtn.addActionListener(e -> {
            new StaffHome(username).setVisible(true);
            dispose();
        });
        footerPanel.add(backBtn);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPendingOrders() {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT o.order_id, o.order_date, u.name as customer_name, " +
                             "o.total_amount, o.orderstatus, o.payment_method, o.order_type " +
                             "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                             "WHERE o.orderstatus != 'completed' " +
                             "ORDER BY o.order_date ASC")) {

            ResultSet rs = stmt.executeQuery();

            Vector<String> columns = new Vector<>();
            columns.add("Order ID");
            columns.add("Date");
            columns.add("Customer");
            columns.add("Amount");
            columns.add("Status");
            columns.add("Payment");
            columns.add("Type");

            Vector<Vector<Object>> data = new Vector<>();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("order_id"));
                row.add(rs.getTimestamp("order_date"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getDouble("total_amount"));
                row.add(rs.getString("orderstatus"));  // Changed from status to orderstatus
                row.add(rs.getString("payment_method"));
                row.add(rs.getString("order_type"));
                data.add(row);
            }

            DefaultTableModel model = new DefaultTableModel(data, columns) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Integer.class;
                    if (columnIndex == 1) return Timestamp.class;
                    if (columnIndex == 3) return Double.class;
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            ordersTable.setModel(model);

            // Custom renderers
            ordersTable.getColumnModel().getColumn(3).setCellRenderer(new CurrencyCellRenderer());
            ordersTable.getColumnModel().getColumn(1).setCellRenderer(new DateCellRenderer());
            ordersTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading orders: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeSelectedOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (Integer) ordersTable.getValueAt(selectedRow, 0);
        String customerName = (String) ordersTable.getValueAt(selectedRow, 2);
        double amount = (Double) ordersTable.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark order #" + orderId + " for " + customerName + " (₱" + String.format("%.2f", amount) + ") as completed?",
                "Confirm Completion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnector.getConnection();
                 CallableStatement stmt = conn.prepareCall("{CALL complete_order(?, ?)}")) {

                // First we need to get the staff user_id (current user)
                int staffId = 0;
                try (PreparedStatement userStmt = conn.prepareStatement(
                        "SELECT user_id FROM users WHERE name = ?")) {
                    userStmt.setString(1, username);
                    ResultSet rs = userStmt.executeQuery();
                    if (rs.next()) {
                        staffId = rs.getInt("user_id");
                    }
                }

                // Set parameters for the stored procedure
                stmt.setInt(1, orderId);
                stmt.setInt(2, staffId);

                stmt.execute();

                JOptionPane.showMessageDialog(this,
                        "Order #" + orderId + " completed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadPendingOrders(); // Refresh the list

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error completing order: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Custom cell renderers
    private class CurrencyCellRenderer extends DefaultTableCellRenderer {
        public CurrencyCellRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setText("₱" + String.format("%.2f", (Double) value));
            return this;
        }
    }

    private class DateCellRenderer extends DefaultTableCellRenderer {
        public DateCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Timestamp) {
                Timestamp ts = (Timestamp) value;
                setText(ts.toLocalDateTime().toLocalDate().toString() + " " +
                        ts.toLocalDateTime().toLocalTime().toString().substring(0, 8));
            }
            return this;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        public StatusCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = (String) value;
            if ("processing".equalsIgnoreCase(status)) {
                setForeground(Color.YELLOW);
            } else if ("preparing".equalsIgnoreCase(status)) {
                setForeground(Color.ORANGE);
            } else {
                setForeground(Color.WHITE);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        // For testing
        SwingUtilities.invokeLater(() -> new ProcessOrders("staff_user").setVisible(true));
    }
}