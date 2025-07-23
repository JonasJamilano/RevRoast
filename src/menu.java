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

    // UI Components
    private JPanel menuPanel;
    private JComboBox<String> currencyDropdown;
    private String selectedCurrency = "PHP";
    private Map<String, Double> exchangeRates;
    private java.util.List<String> cartItems;
    private Map<String, Integer> cartQuantities;

    public menu() {
        // Initialize fonts
        loadCustomFonts();

        // Window setup
        setTitle("REV & ROAST");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);

        // Data initialization
        cartItems = new ArrayList<>();
        cartQuantities = new HashMap<>();
        initializeExchangeRates();

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new MatteBorder(1, 1, 1, 1, DARK_GRAY));

        // Build UI components
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createMenuScrollPane(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void loadCustomFonts() {
        try {
            // Try to load Montserrat
            FONT_TITLE = Font.createFont(Font.TRUETYPE_FONT,
                    new File("fonts/Montserrat-Bold.ttf")).deriveFont(36f);
            FONT_HEADER = FONT_TITLE.deriveFont(14f);

            // Try to load Inter
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

    private void initializeExchangeRates() {
        exchangeRates = new HashMap<>();
        exchangeRates.put("PHP", 1.0);
        exchangeRates.put("USD", 0.018);
        exchangeRates.put("YEN", 2.6);
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

        // Divider
        JSeparator divider = new JSeparator(SwingConstants.HORIZONTAL);
        divider.setForeground(LIGHT_GRAY);
        divider.setPreferredSize(new Dimension(getWidth(), 1));

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
        headerPanel.add(divider, BorderLayout.SOUTH);

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
            dispose();
            new home();
        });

        JButton viewCartBtn = createMinimalButton("CART (0)", RACING_RED);
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
             ResultSet rs = stmt.executeQuery("SELECT name, price FROM products")) {

            while (rs.next()) {
                String productName = rs.getString("name");
                double basePrice = rs.getDouble("price");
                addMenuItem(productName, basePrice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void addMenuItem(String name, double basePricePHP) {
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

        // Price
        double convertedPrice = basePricePHP * exchangeRates.get(selectedCurrency);
        String currencySymbol = selectedCurrency.equals("USD") ? "$" :
                selectedCurrency.equals("YEN") ? "¥" : "₱";
        JLabel itemPrice = new JLabel(currencySymbol + String.format("%.2f", convertedPrice));
        itemPrice.setFont(FONT_PRICE);
        itemPrice.setForeground(RACING_RED);

        // Quantity controls
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        qtyPanel.setBackground(DARK_GRAY);

        JLabel qtyLabel = new JLabel("QTY:");
        qtyLabel.setFont(FONT_BODY);
        qtyLabel.setForeground(LIGHT_GRAY);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
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
        addBtn.addActionListener(e -> {
            int quantity = (Integer) qtySpinner.getValue();
            String itemKey = name + " - " + currencySymbol + String.format("%.2f", convertedPrice);

            if (!cartItems.contains(itemKey)) {
                cartItems.add(itemKey);
                cartQuantities.put(itemKey, quantity);
            } else {
                cartQuantities.put(itemKey, cartQuantities.get(itemKey) + quantity);
            }

            updateCartButtonCount();

            JOptionPane.showMessageDialog(this,
                    quantity + " × " + name + " added",
                    "",
                    JOptionPane.PLAIN_MESSAGE);
        });

        // Assembly
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(DARK_GRAY);
        infoPanel.add(itemName, BorderLayout.NORTH);
        infoPanel.add(itemPrice, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(DARK_GRAY);
        bottomPanel.add(qtyPanel, BorderLayout.WEST);
        bottomPanel.add(addBtn, BorderLayout.EAST);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        menuPanel.add(card);
    }

    private void updateCartButtonCount() {
        int totalItems = cartQuantities.values().stream().mapToInt(Integer::intValue).sum();

        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    if (subComp instanceof JButton && ((JButton) subComp).getText().startsWith("CART")) {
                        ((JButton) subComp).setText("CART (" + totalItems + ")");
                        return;
                    }
                }
            }
        }
    }

    private void showCart() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty", "", JOptionPane.PLAIN_MESSAGE);
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

        Object[] options = {"CHECKOUT", "CONTINUE SHOPPING"};
        int choice = JOptionPane.showOptionDialog(
                this,
                cartPanel,
                "",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Order placed successfully", "", JOptionPane.PLAIN_MESSAGE);
            cartItems.clear();
            cartQuantities.clear();
            updateCartButtonCount();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new menu().setVisible(true);
        });
    }
}