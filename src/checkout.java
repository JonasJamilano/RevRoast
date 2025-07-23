import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

public class checkout extends JFrame {
    private JComboBox<String> currencyDropdown;
    private JPanel cartPanel;
    private JLabel totalLabel;

    private String[] cartItems = {"Espresso", "Latte", "Mocha"};
    private double[] pricesPHP = {120, 140, 160};
    private double conversionRate = 1.0;
    private String currentCurrency = "PHP";

    private final HashMap<String, Double> exchangeRates = new HashMap<>() {{
        put("PHP", 1.0);
        put("USD", 0.018);
        put("YEN", 2.61);
    }};

    public checkout() {
        setTitle("Rev & Roast - Checkout");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        // Header with currency dropdown
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.DARK_GRAY);
        JLabel currencyLabel = new JLabel("Currency:");
        currencyLabel.setForeground(Color.WHITE);
        currencyDropdown = new JComboBox<>(new String[]{"PHP", "USD", "YEN"});
        currencyDropdown.addActionListener(e -> updateCurrency());

        topPanel.add(currencyLabel);
        topPanel.add(currencyDropdown);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Cart display
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(cartPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer with total and button
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        totalLabel = new JLabel("Total: ₱" + computeTotal(pricesPHP), SwingConstants.CENTER);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(new Color(153, 61, 61));

        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setBackground(new Color(153, 61, 61));
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Order placed using " + currentCurrency + "!");
            dispose();
            new home();
        });

        bottomPanel.add(totalLabel);
        bottomPanel.add(placeOrderBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Now safe to populate the cart
        populateCart();

        add(mainPanel);
        setVisible(true);
        }

    private void populateCart() {
        cartPanel.removeAll();
        for (int i = 0; i < cartItems.length; i++) {
            String item = cartItems[i];
            double basePrice = pricesPHP[i];
            double convertedPrice = basePrice * conversionRate;
            String formatted = String.format("%s - %.2f %s", item, convertedPrice, currentCurrency);
            JLabel itemLabel = new JLabel(formatted);
            itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            cartPanel.add(itemLabel);
        }
        totalLabel.setText("Total: " + getSymbol(currentCurrency) + computeTotal(pricesPHP));
        cartPanel.revalidate();
        cartPanel.repaint();
    }

    private void updateCurrency() {
        currentCurrency = (String) currencyDropdown.getSelectedItem();
        conversionRate = exchangeRates.get(currentCurrency);
        populateCart();
    }

    private String getSymbol(String currency) {
        return switch (currency) {
            case "PHP" -> "₱";
            case "USD" -> "$";
            case "YEN" -> "¥";
            default -> "";
        };
    }

    private String computeTotal(double[] prices) {
        double total = 0;
        for (double p : prices) {
            total += p * conversionRate;
        }
        return String.format("%.2f %s", total, currentCurrency);
    }

    public static void main(String[] args) {
        new checkout();
    }
}
