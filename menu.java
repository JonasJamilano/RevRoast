import java.awt.*;
import java.util.*;
import javax.swing.*;

public class menu extends JFrame {
    private JPanel menuPanel;
    private JComboBox<String> currencyDropdown;
    private String selectedCurrency = "PHP";
    private Map<String, Double> exchangeRates;

    public menu() {
        setTitle("Rev & Roast - Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        exchangeRates = new HashMap<>();
        exchangeRates.put("PHP", 1.0);
        exchangeRates.put("USD", 0.018);  // Example rate: 1 PHP = 0.018 USD
        exchangeRates.put("YEN", 2.6);    // Example rate: 1 PHP = 2.6 YEN

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50)); 

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(50, 50, 50));

        JLabel title = new JLabel("Menu", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(new Color(153, 61, 61)); 
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        currencyDropdown = new JComboBox<>(new String[] {"PHP", "USD", "YEN"});
        currencyDropdown.setSelectedItem("PHP");
        currencyDropdown.addActionListener(e -> {
            selectedCurrency = (String) currencyDropdown.getSelectedItem();
            updateMenuItems();
        });

        JPanel currencyPanel = new JPanel();
        currencyPanel.setBackground(new Color(50, 50, 50));
        currencyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));
        currencyPanel.add(new JLabel("Currency: "));
        currencyPanel.add(currencyDropdown);

        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(currencyPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 2, 20, 20));
        menuPanel.setBackground(new Color(30, 30, 30));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        updateMenuItems();

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Home");
        backBtn.setPreferredSize(new Dimension(150, 40));
        backBtn.setBackground(new Color(153, 61, 61));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            dispose();
            new home(); 
        });

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(50, 50, 50));
        footerPanel.add(backBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void updateMenuItems() {
        menuPanel.removeAll();
        addMenuItem(menuPanel, "Espresso", 120);
        addMenuItem(menuPanel, "Cappuccino", 150);
        addMenuItem(menuPanel, "Latte", 140);
        addMenuItem(menuPanel, "Mocha", 160);
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void addMenuItem(JPanel panel, String name, double basePricePHP) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 61, 61), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel itemName = new JLabel(name);
        itemName.setFont(new Font("SansSerif", Font.BOLD, 18));

        double convertedPrice = basePricePHP * exchangeRates.get(selectedCurrency);
        String currencySymbol = selectedCurrency.equals("USD") ? "$" : selectedCurrency.equals("YEN") ? "¥" : "₱";
        JLabel itemPrice = new JLabel(currencySymbol + String.format("%.2f", convertedPrice));
        itemPrice.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(new Color(153, 61, 61));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, name + " added to cart!");
        });

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(itemName);
        infoPanel.add(itemPrice);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(addBtn, BorderLayout.SOUTH);

        panel.add(card);
    }

    public static void main(String[] args) {
        new menu();
    }
}