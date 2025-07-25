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

        // Order info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBackground(PANEL_BG);
        infoPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        addInfoRow(infoPanel, "Order Date:");
        addInfoRow(infoPanel, "Status:");
        addInfoRow(infoPanel, "Payment Method:");
        addInfoRow(infoPanel, "Order Type:");
        addInfoRow(infoPanel, "Total Amount:");
        addInfoRow(infoPanel, "Currency:");

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Items table
        JTable itemsTable = new JTable();
        itemsTable.setFont(FONT_BODY);
        itemsTable.setForeground(Color.WHITE);
        itemsTable.setBackground(DARK_GRAY);
        itemsTable.setGridColor(LIGHT_GRAY);
        itemsTable.setRowHeight(25);

        JScrollPane tableScroll = new JScrollPane(itemsTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(PANEL_BG);

        mainPanel.add(tableScroll, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addInfoRow(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);

        JLabel value = new JLabel();
        value.setFont(FONT_BODY);
        value.setForeground(Color.WHITE);
        panel.add(value);
    }

    private void loadOrderDetails() {
        String currencyCode = "";

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
                    JPanel infoPanel = (JPanel) ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    infoPanel.removeAll();  // Clear all components
                    infoPanel.setLayout(new GridLayout(0, 2, 10, 5));

                    currencyCode = rs.getString("currency_code");
                    String currencySymbol = currencyCode.equals("USD") ? "$" :
                            currencyCode.equals("JPY") ? "¥" : "₱";

                    addInfoRow(infoPanel, "Order Date:", rs.getTimestamp("order_date").toString());
                    addInfoRow(infoPanel, "Status:", rs.getString("status"));
                    addInfoRow(infoPanel, "Payment Method:", rs.getString("payment_method"));
                    addInfoRow(infoPanel, "Order Type:", rs.getString("order_type"));
                    addInfoRow(infoPanel, "Total Amount:", currencySymbol + String.format("%.2f", rs.getDouble("total_amount")));
                    addInfoRow(infoPanel, "Currency:", currencyCode);

                    String instructions = rs.getString("special_instructions");
                    if (instructions != null && !instructions.isEmpty()) {
                        addInfoRow(infoPanel, "Instructions:", instructions);
                    }

                    infoPanel.revalidate();
                    infoPanel.repaint();
                }
            }

            // Load order items
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT p.name, oi.quantity, oi.price " +
                            "FROM order_items oi " +
                            "JOIN products p ON oi.product_id = p.product_id " +
                            "WHERE oi.order_id = ?")) {

                stmt.setInt(1, orderId);
                ResultSet rs = stmt.executeQuery();

                Vector<String> columns = new Vector<>();
                columns.add("Item");
                columns.add("Qty");
                columns.add("Price");
                columns.add("Subtotal");

                Vector<Vector<Object>> data = new Vector<>();

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("name"));
                    row.add(rs.getInt("quantity"));
                    row.add(rs.getDouble("price"));
                    row.add(rs.getInt("quantity") * rs.getDouble("price"));
                    data.add(row);
                }

                DefaultTableModel model = new DefaultTableModel(data, columns) {
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        if (columnIndex == 1) return Integer.class;
                        if (columnIndex == 2 || columnIndex == 3) return Double.class;
                        return String.class;
                    }
                };

                JScrollPane tableScroll = (JScrollPane) ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH);
                JTable itemsTable = (JTable) tableScroll.getViewport().getView();

                String currencySymbol = currencyCode.equals("USD") ? "$" :
                        currencyCode.equals("JPY") ? "¥" : "₱";

                itemsTable.setModel(model);
                itemsTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyCellRenderer(currencySymbol));
                itemsTable.getColumnModel().getColumn(3).setCellRenderer(new CurrencyCellRenderer(currencySymbol));

                itemsTable.revalidate();
                itemsTable.repaint();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading order details: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modified addInfoRow method to include value
    private void addInfoRow(JPanel panel, String label, String valueText) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);

        JLabel value = new JLabel(valueText);
        value.setFont(FONT_BODY);
        value.setForeground(Color.WHITE);
        panel.add(value);
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