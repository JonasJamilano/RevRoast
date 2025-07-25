import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.border.*;
import java.io.File;

public class menu extends JFrame {
    // Custom Fonts
    private Font FONT_TITLE;
    private Font FONT_HEADER;
    private Font FONT_BODY;
    private Font FONT_BUTTON;
    private Font FONT_PRICE;

    // Color Scheme
    private final Color RACING_RED = new Color(230, 0, 0);
    private final Color DARK_GRAY = new Color(40, 40, 40);
    private final Color LIGHT_GRAY = new Color(100, 100, 100);
    private final Color PANEL_BG = new Color(25, 25, 25);
    private final Color SUCCESS_GREEN = new Color(0, 180, 0);

    // UI Components
    private JPanel menuPanel;
    private JComboBox<String> currencyDropdown;
    private String selectedCurrency = "PHP";
    private Map<String, Double> exchangeRates;
    private java.util.List<String> cartItems;
    private Map<String, Integer> cartQuantities;
    private int currentUserId;
    private JButton viewCartBtn;

    public menu() {
        this(-1); // Guest constructor
    }

    public menu(int userId) {
        this.currentUserId = userId;

        // Initialize UI
        initializeFonts();
        setupWindow();
        initializeData();
        buildUI();
    }

    private void initializeFonts() {
        try {
            FONT_TITLE = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Montserrat-Bold.ttf")).deriveFont(36f);
            FONT_HEADER = FONT_TITLE.deriveFont(14f);
            FONT_BODY = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Inter-Regular.ttf")).deriveFont(14f);
            FONT_BUTTON = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Inter-Bold.ttf")).deriveFont(12f);
            FONT_PRICE = FONT_BODY.deriveFont(Font.BOLD, 16f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(FONT_TITLE);
            ge.registerFont(FONT_BODY);
        } catch (Exception e) {
            // Fallback fonts
            FONT_TITLE = new Font("Arial Rounded MT Bold", Font.BOLD, 36);
            FONT_HEADER = new Font("Arial", Font.BOLD, 14);
            FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
            FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
            FONT_PRICE = new Font("Segoe UI", Font.BOLD, 16);
        }
    }

    private void setupWindow() {
        setTitle("REV & ROAST");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
    }

    private void initializeData() {
        cartItems = new ArrayList<>();
        cartQuantities = new HashMap<>();
        initializeExchangeRates();
    }

    private void initializeExchangeRates() {
        exchangeRates = new HashMap<>();
        exchangeRates.put("PHP", 1.0);
        exchangeRates.put("USD", 0.018);
        exchangeRates.put("YEN", 2.6);
    }

    private void buildUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new MatteBorder(1, 1, 1, 1, DARK_GRAY));

        // Build components
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createMenuScrollPane(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BG);
        headerPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, RACING_RED),
                new EmptyBorder(15, 25, 15, 25)
        ));

        // Title
        JLabel title = new JLabel("MENU");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.LEFT);

        // Currency selector
        currencyDropdown = new JComboBox<>(new String[]{"PHP", "USD", "YEN"});
        currencyDropdown.setSelectedItem("PHP");
        currencyDropdown.setFont(FONT_BODY);
        currencyDropdown.setBackground(DARK_GRAY);
        currencyDropdown.setForeground(Color.WHITE);
        currencyDropdown.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        currencyDropdown.addActionListener(e -> {
            selectedCurrency = (String) currencyDropdown.getSelectedItem();
            updateMenuItems();
        });

        JPanel currencyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        currencyPanel.setBackground(PANEL_BG);
        JLabel currencyLabel = new JLabel("CURRENCY:");
        currencyLabel.setFont(FONT_HEADER);
        currencyLabel.setForeground(LIGHT_GRAY);
        currencyPanel.add(currencyLabel);
        currencyPanel.add(currencyDropdown);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(currencyPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createMenuScrollPane() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 3, 15, 15));
        menuPanel.setBackground(PANEL_BG);
        menuPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        updateMenuItems();

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PANEL_BG);
        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JButton backBtn = createMinimalButton("BACK", DARK_GRAY);
        backBtn.addActionListener(e -> {
            if (currentUserId > 0) {
                new home("User", currentUserId);
            } else {
                new home();
            }
            dispose();
        });

        viewCartBtn = createMinimalButton("CART (0)", RACING_RED);
        viewCartBtn.addActionListener(e -> showCart());

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(PANEL_BG);
        footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, DARK_GRAY));
        footerPanel.add(backBtn);
        footerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        footerPanel.add(viewCartBtn);

        return footerPanel;
    }

    private JButton createMinimalButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor == RACING_RED ?
                        new Color(200, 0, 0) : new Color(60, 60, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void updateMenuItems() {
        menuPanel.removeAll();

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT product_id, name, price, stock_quantity FROM products")) {

            while (rs.next()) {
                String productName = rs.getString("name");
                double basePrice = rs.getDouble("price");
                int stock = rs.getInt("stock_quantity");
                addMenuItem(productName, basePrice, stock);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading menu: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void addMenuItem(String name, double basePricePHP, int stock) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(DARK_GRAY);
        card.setBorder(new CompoundBorder(
                new LineBorder(LIGHT_GRAY, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(250, 120));

        // Item name
        JLabel itemName = new JLabel(name);
        itemName.setFont(FONT_HEADER);
        itemName.setForeground(Color.WHITE);
        itemName.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Price and stock
        double convertedPrice = basePricePHP * exchangeRates.get(selectedCurrency);
        String currencySymbol = selectedCurrency.equals("USD") ? "$" :
                selectedCurrency.equals("YEN") ? "¥" : "₱";
        JLabel itemPrice = new JLabel(currencySymbol + String.format("%.2f", convertedPrice));
        itemPrice.setFont(FONT_PRICE);
        itemPrice.setForeground(RACING_RED);

        JLabel stockLabel = new JLabel(stock + " available");
        stockLabel.setFont(FONT_BODY.deriveFont(12f));
        stockLabel.setForeground(stock > 0 ? SUCCESS_GREEN : Color.RED);

        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(DARK_GRAY);
        pricePanel.add(itemPrice, BorderLayout.NORTH);
        pricePanel.add(stockLabel, BorderLayout.SOUTH);

        // Quantity controls
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        qtyPanel.setBackground(DARK_GRAY);

        JLabel qtyLabel = new JLabel("QTY:");
        qtyLabel.setFont(FONT_BODY);
        qtyLabel.setForeground(LIGHT_GRAY);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, stock > 0 ? stock : 1, 1));
        qtySpinner.setPreferredSize(new Dimension(50, 25));
        qtySpinner.setFont(FONT_BODY);
        ((JSpinner.DefaultEditor) qtySpinner.getEditor()).getTextField().setBackground(PANEL_BG);
        ((JSpinner.DefaultEditor) qtySpinner.getEditor()).getTextField().setForeground(Color.WHITE);
        ((JSpinner.DefaultEditor) qtySpinner.getEditor()).getTextField().setBorder(new EmptyBorder(2, 5, 2, 5));

        qtyPanel.add(qtyLabel);
        qtyPanel.add(qtySpinner);

        // Add to Cart button
        JButton addBtn = new JButton("ADD TO CART");
        addBtn.setBackground(PANEL_BG);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(FONT_BUTTON);
        addBtn.setBorder(new CompoundBorder(
                new LineBorder(RACING_RED, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        addBtn.setFocusPainted(false);

        if (stock <= 0) {
            addBtn.setEnabled(false);
            addBtn.setBackground(new Color(80, 80, 80));
            addBtn.setBorder(new CompoundBorder(
                    new LineBorder(new Color(100, 100, 100), 1),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }

        addBtn.addActionListener(e -> {
            int quantity = (Integer) qtySpinner.getValue();
            String itemKey = name + " - " + currencySymbol + String.format("%.2f", convertedPrice);

            try (Connection conn = DatabaseConnector.getConnection();
                 CallableStatement stmt = conn.prepareCall("{call add_to_cart(?, ?)}")) {

                int productId = getProductIdByName(name);
                stmt.setInt(1, productId);
                stmt.setInt(2, quantity);
                stmt.execute();

                if (!cartItems.contains(itemKey)) {
                    cartItems.add(itemKey);
                    cartQuantities.put(itemKey, quantity);
                } else {
                    cartQuantities.put(itemKey, cartQuantities.get(itemKey) + quantity);
                }

                updateCartButtonCount();
                showSuccessMessage(quantity + " × " + name + " added to cart");

            } catch (SQLException ex) {
                showErrorMessage("Failed to add to cart: " + ex.getMessage());
            }
        });

        // Assembly
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(DARK_GRAY);
        infoPanel.add(itemName, BorderLayout.NORTH);
        infoPanel.add(pricePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(DARK_GRAY);
        bottomPanel.add(qtyPanel, BorderLayout.WEST);
        bottomPanel.add(addBtn, BorderLayout.EAST);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        menuPanel.add(card);
    }

    private int getProductIdByName(String productName) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT product_id FROM products WHERE name = ?")) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("product_id");
            }
        }
        throw new SQLException("Product not found: " + productName);
    }

    private int getCurrencyIdByCode(String currencyCode) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT currency_id FROM currencies WHERE currency_code = ?")) {
            stmt.setString(1, currencyCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("currency_id");
            }
        }
        throw new SQLException("Currency not found: " + currencyCode);
    }

    private void updateCartButtonCount() {
        int totalItems = cartQuantities.values().stream().mapToInt(Integer::intValue).sum();
        viewCartBtn.setText("CART (" + totalItems + ")");
    }

    private void showCart() {
        if (cartItems.isEmpty()) {
            showInformationMessage("Your cart is empty");
            return;
        }

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(PANEL_BG);
        cartPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("YOUR ORDER");
        title.setFont(FONT_HEADER.deriveFont(18f));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        cartPanel.add(title, BorderLayout.NORTH);

        JTextArea cartContent = new JTextArea();
        cartContent.setEditable(false);
        cartContent.setBackground(PANEL_BG);
        cartContent.setForeground(Color.WHITE);
        cartContent.setFont(FONT_BODY);

        StringBuilder cartText = new StringBuilder();
        double total = 0;

        for (String item : cartItems) {
            int quantity = cartQuantities.get(item);
            String itemName = item.split(" - ")[0];
            String priceStr = item.split(" - ")[1];
            double price = Double.parseDouble(priceStr.substring(1));

            cartText.append(String.format("%d × %-25s %s%6.2f\n",
                    quantity,
                    itemName,
                    priceStr.charAt(0),
                    price * quantity));

            total += price * quantity;
        }

        cartText.append("\n");
        cartText.append(String.format("%-30s %s%6.2f",
                "TOTAL:",
                cartItems.get(0).split(" - ")[1].charAt(0),
                total));

        cartContent.setText(cartText.toString());
        cartPanel.add(new JScrollPane(cartContent), BorderLayout.CENTER);

        // Payment options
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentPanel.setBackground(PANEL_BG);
        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setFont(FONT_BODY);
        paymentLabel.setForeground(Color.WHITE);
        JComboBox<String> paymentOptions = new JComboBox<>(new String[]{"Cash", "Credit Card", "GCash"});
        paymentPanel.add(paymentLabel);
        paymentPanel.add(paymentOptions);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(paymentPanel, BorderLayout.NORTH);
        bottomPanel.add(cartPanel, BorderLayout.CENTER);

        Object[] options = {"CHECKOUT", "CONTINUE SHOPPING"};
        int choice = JOptionPane.showOptionDialog(
                this,
                bottomPanel,
                "",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            processCheckout(paymentOptions);
        }
    }

    private void processCheckout(JComboBox<String> paymentOptions) {
        if (currentUserId <= 0) {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "You need to login to checkout. Login now?",
                    "Login Required",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                new login();
                dispose();
            }
            return;
        }

        if (cartItems.isEmpty()) {
            showErrorMessage("Your cart is empty");
            return;
        }

        // Calculate total amount from cart
        double totalAmount = calculateCartTotal();
        String paymentMethod = (String) paymentOptions.getSelectedItem();
        int currencyId;

        try {
            currencyId = getCurrencyIdByCode(selectedCurrency);
        } catch (SQLException ex) {
            showErrorMessage("Error getting currency: " + ex.getMessage());
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                // 1. Create the order and get order ID
                int orderId;
                int rpmPointsEarned;
                int totalUserPoints;

                try (CallableStatement orderStmt = conn.prepareCall(
                        "{call complete_order(?, ?, ?, ?, ?, ?)}")) {

                    orderStmt.setInt(1, currentUserId);
                    orderStmt.setInt(2, currencyId);
                    orderStmt.setString(3, paymentMethod);
                    orderStmt.setDouble(4, totalAmount);
                    orderStmt.registerOutParameter(5, Types.INTEGER); // order_id
                    orderStmt.registerOutParameter(6, Types.INTEGER); // rpm_points_earned

                    orderStmt.execute();

                    orderId = orderStmt.getInt(5);
                    rpmPointsEarned = orderStmt.getInt(6);
                }

                // 2. Get current total points after update
                try (PreparedStatement pointsStmt = conn.prepareStatement(
                        "SELECT rpm_points FROM users WHERE user_id = ?")) {
                    pointsStmt.setInt(1, currentUserId);
                    ResultSet rs = pointsStmt.executeQuery();
                    totalUserPoints = rs.next() ? rs.getInt("rpm_points") : 0;
                }

                // 3. Insert all order items
                try (PreparedStatement itemStmt = conn.prepareStatement(
                        "INSERT INTO order_items (order_id, product_id, quantity, price) " +
                                "VALUES (?, ?, ?, (SELECT price FROM products WHERE product_id = ?))")) {

                    for (String item : cartItems) {
                        String productName = item.split(" - ")[0];
                        int quantity = cartQuantities.get(item);
                        int productId = getProductIdByName(productName);

                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, productId);
                        itemStmt.setInt(3, quantity);
                        itemStmt.setInt(4, productId);
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                // 4. Update product stocks
                try (PreparedStatement stockStmt = conn.prepareStatement(
                        "UPDATE products SET stock_quantity = stock_quantity - ? " +
                                "WHERE product_id = ?")) {

                    for (String item : cartItems) {
                        String productName = item.split(" - ")[0];
                        int quantity = cartQuantities.get(item);
                        int productId = getProductIdByName(productName);

                        stockStmt.setInt(1, quantity);
                        stockStmt.setInt(2, productId);
                        stockStmt.addBatch();
                    }
                    stockStmt.executeBatch();
                }

                conn.commit(); // Commit transaction if all succeeded

                // Show success message with points information
                showSuccessMessage(
                        "Order #" + orderId + " completed!\n" +
                                "Total: " + String.format("%.2f", totalAmount) + "\n" +
                                "Earned " + rpmPointsEarned + " RPM points\n" +
                                "Your total points: " + totalUserPoints);

                // Clear cart
                cartItems.clear();
                cartQuantities.clear();
                updateCartButtonCount();

                // Refresh menu to update stock quantities
                updateMenuItems();

            } catch (SQLException ex) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                showErrorMessage("Checkout failed: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException ex) {
            showErrorMessage("Database connection error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int getCurrentUserPoints(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT rpm_points FROM users WHERE user_id = ?")) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("rpm_points") : 0;
        }
    }

    private double calculateCartTotal() {
        double total = 0;
        for (String item : cartItems) {
            int quantity = cartQuantities.get(item);
            String priceStr = item.split(" - ")[1];
            double price = Double.parseDouble(priceStr.substring(1));
            total += price * quantity;
        }
        return total;
    }

    // Helper methods for messages
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new menu().setVisible(true); // Test without login
        });
    }
}