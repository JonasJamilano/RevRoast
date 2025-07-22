import java.awt.*;
import javax.swing.*;

public class cart extends JFrame {
    private JTextArea cartContents;

    public cart() {
        setTitle("Rev & Roast - Your Cart");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50));

        JLabel header = new JLabel("Your Cart", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 36));
        header.setForeground(new Color(153, 61, 61));
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // Currency Selector
        String[] currencies = {"PHP", "USD", "YEN"};
        JComboBox<String> currencySelector = new JComboBox<>(currencies);
        currencySelector.setSelectedItem("PHP");
        currencySelector.addActionListener(e -> {
            String selected = (String) currencySelector.getSelectedItem();
            currency.setCurrency(currency.CurrencyType.valueOf(selected));
            updateCartDisplay();
        });

        JPanel currencyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        currencyPanel.setBackground(new Color(50, 50, 50));
        currencyPanel.add(new JLabel("Currency:"));
        currencyPanel.add(currencySelector);

        // Cart Contents
        cartContents = new JTextArea();
        cartContents.setEditable(false);
        cartContents.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cartContents.setBackground(Color.WHITE);
        cartContents.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(cartContents);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Back to Home Button
        JButton backBtn = new JButton("Back to Home");
        styleButton(backBtn);
        backBtn.addActionListener(e -> {
            new home();
            dispose();
        });

        // Checkout Button
        JButton checkoutBtn = new JButton("Checkout");
        styleButton(checkoutBtn);
        checkoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Checkout functionality not implemented yet.");
        });

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(50, 50, 50));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        footerPanel.add(backBtn, BorderLayout.WEST);
        footerPanel.add(checkoutBtn, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(currencyPanel, BorderLayout.BEFORE_FIRST_LINE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateCartDisplay(); // Load default display
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(153, 61, 61));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
    }

    private void updateCartDisplay() {
        // Sample items in cart for now (you can replace this with dynamic values later)
        StringBuilder sb = new StringBuilder();
        sb.append("1x Rev Brew - ").append(currency.getCurrencySymbol())
          .append(String.format("%.2f", currency.convert(150))).append("\n");
        sb.append("2x Turbo Espresso - ").append(currency.getCurrencySymbol())
          .append(String.format("%.2f", currency.convert(180 * 2))).append("\n");

        cartContents.setText(sb.toString());
    }

    public static void main(String[] args) {
        new cart();
    }
}