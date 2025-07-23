import java.awt.*;
import javax.swing.*;

public class history extends JFrame {

    public history() {
        setTitle("Rev & Roast - Order History");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("Order History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Mock order data
        String[] orderHistory = {
            "Order #1001 - 2x Espresso, 1x Mocha - ₱420.00 - PHP",
            "Order #1002 - 1x Latte - $2.52 - USD",
            "Order #1003 - 3x Mocha - ¥1254.30 - YEN"
        };

        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setBackground(Color.LIGHT_GRAY);

        for (String order : orderHistory) {
            JLabel orderLabel = new JLabel(order);
            orderLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            orderLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            ordersPanel.add(orderLabel);
        }

        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Home");
        backBtn.setBackground(new Color(153, 61, 61));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            dispose();
            new home();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.add(backBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new history();
    }
}