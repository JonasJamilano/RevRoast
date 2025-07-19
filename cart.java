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
        header.setFont(new Font("Serif", Font.BOLD, 30));
        header.setForeground(new Color(252, 65, 17));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JTextArea cartContents = new JTextArea("Cart is currently empty.");
        cartContents.setEditable(false);
        cartContents.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cartContents.setBackground(Color.WHITE);
        cartContents.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(cartContents);

        JButton backBtn = new JButton("Back to Home");
        backBtn.setPreferredSize(new Dimension(150, 40));
        backBtn.addActionListener(e -> {
            new home();
            dispose();
        });

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(50, 50, 50));
        footerPanel.add(backBtn);

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