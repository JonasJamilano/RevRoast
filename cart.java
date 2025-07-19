import java.awt.*;
import javax.swing.*;

public class cart extends JFrame {

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

        JTextArea cartContents = new JTextArea("Cart is currently empty.");
        cartContents.setEditable(false);
        cartContents.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cartContents.setBackground(Color.WHITE);
        cartContents.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(cartContents);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Back to Home Button
        JButton backBtn = new JButton("Back to Home");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setBackground(new Color(153, 61, 61)); 
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(new Dimension(160, 40));
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setContentAreaFilled(false);
        backBtn.setOpaque(true);

        backBtn.addActionListener(e -> {
            new home();
            dispose();
        });

        // Checkout Button
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        checkoutBtn.setBackground(new Color(153, 61, 61)); 
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setPreferredSize(new Dimension(160, 40));
        checkoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        checkoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkoutBtn.setContentAreaFilled(false);
        checkoutBtn.setOpaque(true);

        checkoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Checkout functionality not implemented yet.");
        });

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(50, 50, 50));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));

        footerPanel.add(backBtn, BorderLayout.WEST);
        footerPanel.add(checkoutBtn, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new cart();
    }
}