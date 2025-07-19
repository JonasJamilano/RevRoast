import java.awt.*;
import javax.swing.*;

public class menu extends JFrame {
    public menu() {
        setTitle("Rev & Roast - Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50)); 

        JLabel title = new JLabel("Menu", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(new Color(153, 61, 61)); 
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 2, 20, 20));
        menuPanel.setBackground(new Color(30, 30, 30));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Sample items
        addMenuItem(menuPanel, "Espresso", "₱120");
        addMenuItem(menuPanel, "Cappuccino", "₱150");
        addMenuItem(menuPanel, "Latte", "₱140");
        addMenuItem(menuPanel, "Mocha", "₱160");

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

    private void addMenuItem(JPanel panel, String name, String price) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 61, 61), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel itemName = new JLabel(name);
        itemName.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel itemPrice = new JLabel(price);
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