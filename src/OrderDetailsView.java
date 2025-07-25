import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.*;
import javax.swing.border.*;

public class OrderDetailsView extends JFrame {
    private final Color PANEL_BG = new Color(25, 25, 25);
    private final Color DARK_GRAY = new Color(40, 40, 40);
    private final Color LIGHT_GRAY = new Color(100, 100, 100);

    private Font FONT_BODY;
    private Font FONT_HEADER;

    private int orderId;
    private JPanel infoPanel;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;

    public OrderDetailsView(int orderId) {
        this.orderId = orderId;
        initializeFonts();
        setupWindow();
        buildUI();
        loadOrderDetails();
    }

    private void initializeFonts() {
        try {
            FONT_BODY = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Inter-Regular.ttf")).deriveFont(14f);
            FONT_HEADER = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Montserrat-Bold.ttf")).deriveFont(16f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(FONT_BODY);
            ge.registerFont(FONT_HEADER);
        } catch (Exception e) {
            // Fallback fonts
            FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
            FONT_HEADER = new Font("Arial", Font.BOLD, 16);
        }
    }

    private void setupWindow() {
        setTitle("REV & ROAST - Order Details #" + orderId);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PANEL_BG);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel title = new JLabel("ORDER DETAILS #" + orderId);
        title.setFont(FONT_HEADER);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Initialize info panel with empty values
        infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBackground(PANEL_BG);
        infoPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        addInfoRow("Order Date:", "");
        addInfoRow("Status:", "");
        addInfoRow("Payment Method:", "");
        addInfoRow("Order Type:", "");
        addInfoRow("Total Amount:", "");
        addInfoRow("Currency:", "");

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Initialize items table
        itemsTableModel = new DefaultTableModel(
                new Object[]{"Item", "Qty", "Price", "Subtotal"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return Integer.class;
                if (columnIndex == 2 || columnIndex == 3) return Double.class;
                return String.class;
            }
        };

        itemsTable = new JTable(itemsTableModel);
        itemsTable.setFont(FONT_BODY);
        itemsTable.setForeground(Color.WHITE);
        itemsTable.setBackground(DARK_GRAY);
        itemsTable.setGridColor(LIGHT_GRAY);
        itemsTable.setRowHeight(25);
        itemsTable.getTableHeader().setFont(FONT_BODY);
        itemsTable.getTableHeader().setBackground(DARK_GRAY);
        itemsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane tableScroll = new JScrollPane(itemsTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(PANEL_BG);

        mainPanel.add(tableScroll, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addInfoRow(String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(Color.WHITE);
        infoPanel.add(lbl);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(FONT_BODY);
        valueLbl.setForeground(Color.WHITE);
        infoPanel.add(valueLbl);
    }

    private void updateInfoRow(int componentIndex, String value) {
        if (infoPanel.getComponentCount() > componentIndex + 1) {
            ((JLabel)infoPanel.getComponent(componentIndex + 1)).setText(value);
        }
    }

    private void loadOrderDetails() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Load order header info
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT o.order_date, o.status, o.payment_method, " +
                            "o.order_type, o.total_amount, c.currency_code, " +
                            "o.special_instructions FROM orders o " +
                            "JOIN currencies c ON o.currency_id = c.currency_id " +
                            "WHERE o.order_id = ?")) {

                stmt.setInt(1, orderId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String currencyCode = rs.getString("currency_code");
                    String currencySymbol = currencyCode.equals("USD") ? "$" :
                            currencyCode.equals("JPY") ? "¥" : "₱";

                    // Update info panel labels
                    updateInfoRow(0, rs.getTimestamp("order_date").toString());
                    updateInfoRow(2, rs.getString("status"));
                    updateInfoRow(4, rs.getString("payment_method"));
                    updateInfoRow(6, rs.getString("order_type"));
                    updateInfoRow(8, currencySymbol + String.format("%.2f", rs.getDouble("total_amount")));
                    updateInfoRow(10, currencyCode);

                    // Add special instructions if present
                    String instructions = rs.getString("special_instructions");
                    if (instructions != null && !instructions.isEmpty()) {
                        if (infoPanel.getComponentCount() <= 12) {
                            addInfoRow("Instructions:", instructions);
                        } else {
                            updateInfoRow(12, instructions);
                        }
                    }

                    // Load order items
                    itemsTableModel.setRowCount(0); // Clear existing rows
                    try (PreparedStatement itemsStmt = conn.prepareStatement(
                            "SELECT p.name, oi.quantity, oi.price " +
                                    "FROM order_items oi " +
                                    "JOIN products p ON oi.product_id = p.product_id " +
                                    "WHERE oi.order_id = ?")) {

                        itemsStmt.setInt(1, orderId);
                        ResultSet itemsRs = itemsStmt.executeQuery();

                        while (itemsRs.next()) {
                            String name = itemsRs.getString("name");
                            int quantity = itemsRs.getInt("quantity");
                            double price = itemsRs.getDouble("price");
                            itemsTableModel.addRow(new Object[]{
                                    name,
                                    quantity,
                                    price,
                                    quantity * price
                            });
                        }
                    }

                    // Set currency renderers
                    itemsTable.getColumnModel().getColumn(2)
                            .setCellRenderer(new CurrencyCellRenderer(currencySymbol));
                    itemsTable.getColumnModel().getColumn(3)
                            .setCellRenderer(new CurrencyCellRenderer(currencySymbol));
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Order not found with ID: " + orderId,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading order details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private class CurrencyCellRenderer extends DefaultTableCellRenderer {
        private String currencySymbol;

        public CurrencyCellRenderer(String currencySymbol) {
            setHorizontalAlignment(JLabel.RIGHT);
            this.currencySymbol = currencySymbol;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setText(currencySymbol + String.format("%.2f", (Double) value));
            return this;
        }
    }
}