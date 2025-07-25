import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.border.*;
import java.io.File;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

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
    private String selectedCurrency;
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

        // Ask for currency first
        selectedCurrency = showCurrencySelectionDialog();
        if (selectedCurrency == null) {
            selectedCurrency = "PHP"; // Default if canceled
        }

        buildUI();
    }

    private String showCurrencySelectionDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("SELECT CURRENCY");
        title.setFont(FONT_HEADER.deriveFont(18f));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JComboBox<String> currencySelect = new JComboBox<>(new String[]{"PHP", "USD", "JPY"});
        currencySelect.setFont(FONT_BODY);
        currencySelect.setBackground(DARK_GRAY);
        currencySelect.setForeground(Color.WHITE);
        currencySelect.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(PANEL_BG);
        centerPanel.add(currencySelect);
        panel.add(centerPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Currency Selection",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (String) currencySelect.getSelectedItem();
        }
        return null;
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
        exchangeRates.put("JPY", 2.6);
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

        // Title only
        JLabel title = new JLabel("MENU");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(title, BorderLayout.WEST);

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
                selectedCurrency.equals("JPY") ? "¥" : "₱";
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

        // Main container panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_BG);

        JLabel title = new JLabel("ORDER SUMMARY");
        title.setFont(FONT_HEADER.deriveFont(20f));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(title, BorderLayout.NORTH);

        // Current user display
        String userDisplay = currentUserId > 0 ? "Logged in as User #" + currentUserId : "Guest Checkout";
        JLabel userLabel = new JLabel(userDisplay);
        userLabel.setFont(FONT_BODY);
        userLabel.setForeground(LIGHT_GRAY);
        headerPanel.add(userLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Items Panel with scrollable table
        String[] columnNames = {"Item", "Qty", "Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        double total = 0;
        String currencySymbol = selectedCurrency.equals("USD") ? "$" :
                selectedCurrency.equals("JPY") ? "¥" : "₱";

        for (String item : cartItems) {
            int quantity = cartQuantities.get(item);
            String itemName = item.split(" - ")[0];
            String priceStr = item.split(" - ")[1];
            double price = Double.parseDouble(priceStr.substring(1));
            double subtotal = price * quantity;
            total += subtotal;

            model.addRow(new Object[]{
                    itemName,
                    quantity,
                    currencySymbol + String.format("%.2f", price),
                    currencySymbol + String.format("%.2f", subtotal)
            });
        }

        JTable itemsTable = new JTable(model);
        itemsTable.setFont(FONT_BODY);
        itemsTable.setForeground(Color.WHITE);
        itemsTable.setBackground(DARK_GRAY);
        itemsTable.setGridColor(LIGHT_GRAY);
        itemsTable.setRowHeight(25);
        itemsTable.setShowGrid(true);
        itemsTable.setSelectionBackground(RACING_RED);
        itemsTable.getTableHeader().setFont(FONT_BUTTON);
        itemsTable.getTableHeader().setBackground(DARK_GRAY);
        itemsTable.getTableHeader().setForeground(Color.WHITE);

        // Center align quantity and right align price columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        itemsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        itemsTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane tableScroll = new JScrollPane(itemsTable);
        tableScroll.setBorder(new LineBorder(LIGHT_GRAY, 1));
        tableScroll.setBackground(PANEL_BG);

        // 3. Total Panel
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(PANEL_BG);
        totalPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel totalLabel = new JLabel("TOTAL: " + currencySymbol + String.format("%.2f", total));
        totalLabel.setFont(FONT_PRICE.deriveFont(18f));
        totalLabel.setForeground(RACING_RED);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalPanel.add(totalLabel, BorderLayout.EAST);

        // 4. Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(PANEL_BG);
        optionsPanel.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, LIGHT_GRAY),
                new EmptyBorder(15, 0, 0, 0)
        ));

        // Payment Method
        JPanel paymentPanel = createOptionPanel("Payment Method:",
                new JComboBox<>(new String[]{"Cash", "Credit Card", "GCash"}));

        // Order Type
        JPanel orderTypePanel = createOptionPanel("Order Type:",
                new JComboBox<>(new String[]{"Pickup", "Delivery"}));

        // Special Instructions
        JPanel instructionsPanel = new JPanel(new BorderLayout(5, 5));
        instructionsPanel.setBackground(PANEL_BG);

        JLabel instructionsLabel = new JLabel("Special Instructions:");
        instructionsLabel.setFont(FONT_BODY);
        instructionsLabel.setForeground(Color.WHITE);

        JTextArea instructionsField = new JTextArea(3, 20);
        instructionsField.setFont(FONT_BODY);
        instructionsField.setBackground(DARK_GRAY);
        instructionsField.setForeground(Color.WHITE);
        instructionsField.setLineWrap(true);
        instructionsField.setWrapStyleWord(true);
        instructionsField.setBorder(new CompoundBorder(
                new LineBorder(LIGHT_GRAY, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));

        instructionsPanel.add(instructionsLabel, BorderLayout.NORTH);
        instructionsPanel.add(new JScrollPane(instructionsField), BorderLayout.CENTER);

        optionsPanel.add(paymentPanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(orderTypePanel);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(instructionsPanel);

        // 5. Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PANEL_BG);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton cancelBtn = createMinimalButton("CANCEL", DARK_GRAY);
        cancelBtn.addActionListener(e -> {
            ((Window)SwingUtilities.getRoot(cancelBtn)).dispose();
        });

        JButton checkoutBtn = createMinimalButton("PROCEED TO CHECKOUT", RACING_RED);
        checkoutBtn.addActionListener(e -> {
            JComboBox<String> paymentCombo = (JComboBox<String>) paymentPanel.getComponent(1);
            JComboBox<String> orderTypeCombo = (JComboBox<String>) orderTypePanel.getComponent(1);
            processCheckout(paymentCombo, orderTypeCombo, instructionsField);
            ((Window)SwingUtilities.getRoot(checkoutBtn)).dispose();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(checkoutBtn);

        // Assembly
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(PANEL_BG);
        bottomContainer.add(optionsPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomContainer, BorderLayout.SOUTH);

        // Create custom dialog
        JDialog checkoutDialog = new JDialog(this, "Checkout", true);
        checkoutDialog.setContentPane(mainPanel);
        checkoutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        checkoutDialog.pack();
        checkoutDialog.setSize(600, 700);
        checkoutDialog.setLocationRelativeTo(this);
        checkoutDialog.setVisible(true);
    }

    private JPanel createOptionPanel(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(PANEL_BG);

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY);
        label.setForeground(Color.WHITE);

        component.setFont(FONT_BODY);
        component.setBackground(DARK_GRAY);
        component.setForeground(Color.WHITE);
        if (component instanceof JComboBox) {
            ((JComboBox<?>) component).setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setBackground(isSelected ? RACING_RED : DARK_GRAY);
                    setForeground(Color.WHITE);
                    return this;
                }
            });
        }

        panel.add(label, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private void processCheckout(JComboBox<String> paymentOptions, JComboBox<String> orderTypeOptions,
                                 JTextArea instructionsField) {
        // 1. Validation Phase
        if (currentUserId <= 0) {
            handleGuestCheckout();
            return;
        }

        if (cartItems.isEmpty()) {
            showErrorMessage("Your cart is empty");
            return;
        }

        // 2. Confirmation Dialog with Order Summary
        if (!confirmOrderDetails(paymentOptions, orderTypeOptions)) {
            return;
        }

        // 3. Payment Processing (simulated)
        if (!processPayment(paymentOptions)) {
            return;
        }

        // 4. Database Transaction
        OrderResult orderResult = processOrderTransaction(
                paymentOptions,
                orderTypeOptions,
                instructionsField
        );

        if (orderResult != null && orderResult.success) {
            // 5. Success Handling
            handleSuccessfulCheckout(orderResult);
        }
    }

    private void handleGuestCheckout() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "You need to login to checkout. Login now?",
                "Login Required",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            new login();
            dispose();
        }
    }

    private boolean confirmOrderDetails(JComboBox<String> paymentOptions, JComboBox<String> orderTypeOptions) {
        String paymentMethod = (String) paymentOptions.getSelectedItem();
        String orderType = (String) orderTypeOptions.getSelectedItem();
        double total = calculateCartTotal();
        String currencySymbol = selectedCurrency.equals("USD") ? "$" :
                selectedCurrency.equals("JPY") ? "¥" : "₱";

        String message = String.format(
                "<html><div style='width: 300px;'>" +
                        "<h3>Confirm Your Order</h3>" +
                        "<p><b>Payment Method:</b> %s</p>" +
                        "<p><b>Order Type:</b> %s</p>" +
                        "<p><b>Total Amount:</b> %s%.2f</p>" +
                        "<p>Proceed with checkout?</p>" +
                        "</div></html>",
                paymentMethod, orderType, currencySymbol, total
        );

        int confirm = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Order",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return confirm == JOptionPane.YES_OPTION;
    }

    private boolean processPayment(JComboBox<String> paymentOptions) {
        String paymentMethod = (String) paymentOptions.getSelectedItem();

        // Simulate payment processing
        if (paymentMethod.equals("Credit Card")) {
            // Show credit card input dialog
            JPanel cardPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            cardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JTextField cardNumber = new JTextField();
            JTextField expiry = new JTextField();
            JTextField cvv = new JTextField();

            cardPanel.add(new JLabel("Card Number:"));
            cardPanel.add(cardNumber);
            cardPanel.add(new JLabel("Expiry Date (MM/YY):"));
            cardPanel.add(expiry);
            cardPanel.add(new JLabel("CVV:"));
            cardPanel.add(cvv);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    cardPanel,
                    "Enter Card Details",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return false;
            }

            // Simple validation
            if (cardNumber.getText().trim().isEmpty() ||
                    expiry.getText().trim().isEmpty() ||
                    cvv.getText().trim().isEmpty()) {
                showErrorMessage("Please fill in all card details");
                return false;
            }
        }

        return true;
    }

    private OrderResult processOrderTransaction(JComboBox<String> paymentOptions,
                                                JComboBox<String> orderTypeOptions, JTextArea instructionsField) {
        // Calculate total amount from cart
        double totalAmount = calculateCartTotal();
        String paymentMethod = (String) paymentOptions.getSelectedItem();
        String orderType = (String) orderTypeOptions.getSelectedItem();
        String specialInstructions = instructionsField.getText();
        int currencyId;

        try {
            currencyId = getCurrencyIdByCode(selectedCurrency);
        } catch (SQLException ex) {
            showErrorMessage("Error getting currency: " + ex.getMessage());
            return null;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try {
                // 1. Create the order with 'processing' status
                int orderId;
                int rpmPointsEarned;

                try (CallableStatement orderStmt = conn.prepareCall(
                        "{call send_order(?, ?, ?, ?, ?, ?, ?, ?)}")) {

                    orderStmt.setInt(1, currentUserId);
                    orderStmt.setInt(2, currencyId);
                    orderStmt.setString(3, paymentMethod);
                    orderStmt.setString(4, orderType);
                    orderStmt.setString(5, specialInstructions);
                    orderStmt.setDouble(6, totalAmount);
                    orderStmt.registerOutParameter(7, Types.INTEGER); // order_id
                    orderStmt.registerOutParameter(8, Types.INTEGER); // rpm_points_earned

                    orderStmt.execute();

                    orderId = orderStmt.getInt(7);
                    rpmPointsEarned = orderStmt.getInt(8);
                }

                // 2. Insert all order items
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

                // 3. Update product stocks (reserve them)
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

                return new OrderResult(true, orderId, rpmPointsEarned, totalAmount);

            } catch (SQLException ex) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                showErrorMessage("Checkout failed: " + ex.getMessage());
                ex.printStackTrace();
                return new OrderResult(false, -1, 0, 0);
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
            return new OrderResult(false, -1, 0, 0);
        }
    }

    private void handleSuccessfulCheckout(OrderResult orderResult) {
        // Clear cart
        cartItems.clear();
        cartQuantities.clear();
        updateCartButtonCount();

        // Refresh menu to update stock quantities
        updateMenuItems();

        // Show success message
        String currencySymbol = selectedCurrency.equals("USD") ? "$" :
                selectedCurrency.equals("JPY") ? "¥" : "₱";

        String message = String.format(
                "<html><div style='width: 300px;'>" +
                        "<h3>Order Confirmed!</h3>" +
                        "<p><b>Order #:</b> %d</p>" +
                        "<p><b>Total:</b> %s%.2f</p>" +
                        "<p><b>Points Earned:</b> %d RPM points</p>" +
                        "<p>Thank you for your order!</p>" +
                        "</div></html>",
                orderResult.orderId,
                currencySymbol,
                orderResult.totalAmount,
                orderResult.rpmPointsEarned
        );

        JOptionPane.showMessageDialog(
                this,
                message,
                "Order Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
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

    // Helper class for order results
    private class OrderResult {
        boolean success;
        int orderId;
        int rpmPointsEarned;
        double totalAmount;

        public OrderResult(boolean success, int orderId, int rpmPointsEarned, double totalAmount) {
            this.success = success;
            this.orderId = orderId;
            this.rpmPointsEarned = rpmPointsEarned;
            this.totalAmount = totalAmount;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new menu().setVisible(true); // Test without login
        });
    }
}