import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.*;
import javax.swing.border.*;

public class PendingOrdersView extends JFrame {
    private final Color PANEL_BG = new Color(25, 25, 25);
    private final Color DARK_GRAY = new Color(40, 40, 40);
    private final Color LIGHT_GRAY = new Color(100, 100, 100);
    private final Color RACING_RED = new Color(230, 0, 0);

    private Font FONT_BODY;
    private Font FONT_HEADER;
    private Font FONT_BUTTON;

    private int userId;
    private JTable ordersTable;

    public PendingOrdersView(int userId) {
        this.userId = userId;
        initializeFonts();
        setupWindow();
        buildUI();
        loadPendingOrders();
    }

    private void initializeFonts() {
        try {
            FONT_BODY = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Inter-Regular.ttf")).deriveFont(14f);
            FONT_HEADER = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Montserrat-Bold.ttf")).deriveFont(18f);
            FONT_BUTTON = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Inter-Bold.ttf")).deriveFont(12f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(FONT_BODY);
            ge.registerFont(FONT_HEADER);
        } catch (Exception e) {
            // Fallback fonts
            FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
            FONT_HEADER = new Font("Arial", Font.BOLD, 18);
            FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
        }
    }

    private void setupWindow() {
        setTitle("REV & ROAST - Pending Orders");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PANEL_BG);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel title = new JLabel("PENDING ORDERS");
        title.setFont(FONT_HEADER);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BG);
        headerPanel.add(title, BorderLayout.WEST);

        // Refresh button
        JButton refreshBtn = new JButton("REFRESH");
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
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(PANEL_BG);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton viewDetailsBtn = new JButton("VIEW DETAILS");
        viewDetailsBtn.setFont(FONT_BUTTON);
        viewDetailsBtn.setBackground(RACING_RED);
        viewDetailsBtn.setForeground(Color.WHITE);
        viewDetailsBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        viewDetailsBtn.addActionListener(e -> viewOrderDetails());
        footerPanel.add(viewDetailsBtn);

        JButton backBtn = new JButton("BACK");
        backBtn.setFont(FONT_BUTTON);
        backBtn.setBackground(DARK_GRAY);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        backBtn.addActionListener(e -> {
            new home("User", userId).setVisible(true);
            dispose();
        });
        footerPanel.add(backBtn);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPendingOrders() {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT o.order_id, o.order_date, o.total_amount, o.status, " +
                             "c.currency_code FROM orders o " +
                             "JOIN currencies c ON o.currency_id = c.currency_id " +
                             "WHERE o.user_id = ? AND o.status != 'completed' " +
                             "ORDER BY o.order_date DESC")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            Vector<String> columns = new Vector<>();
            columns.add("Order ID");
            columns.add("Date");
            columns.add("Amount");
            columns.add("Status");
            columns.add("Currency");

            Vector<Vector<Object>> data = new Vector<>();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("order_id"));
                row.add(rs.getTimestamp("order_date"));
                row.add(rs.getDouble("total_amount"));
                row.add(rs.getString("status"));
                row.add(rs.getString("currency_code"));
                data.add(row);
            }

            DefaultTableModel model = new DefaultTableModel(data, columns) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return Integer.class;
                    if (columnIndex == 1) return Timestamp.class;
                    if (columnIndex == 2) return Double.class;
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            ordersTable.setModel(model);

            // Custom renderers
            ordersTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyCellRenderer());
            ordersTable.getColumnModel().getColumn(1).setCellRenderer(new DateCellRenderer());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading orders: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (Integer) ordersTable.getValueAt(selectedRow, 0);
        OrderDetailsView detailsView = new OrderDetailsView(orderId);
        detailsView.setVisible(true);
        detailsView.toFront(); // Bring to front in case it's behind
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

            String currencyCode = (String) table.getValueAt(row, 4);
            String symbol = currencyCode.equals("USD") ? "$" :
                    currencyCode.equals("JPY") ? "¥" : "₱";

            setText(symbol + String.format("%.2f", (Double) value));
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
                setText(ts.toLocalDateTime().toLocalDate().toString());
            }
            return this;
        }
    }
}