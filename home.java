import java.awt.*;
import javax.swing.*;

public class home extends JFrame {

    public home() {
        setTitle("Rev & Roast - Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new BackgroundPanel("src/f1bg.jpg"); 
        mainPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Rev & Roast", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(new Color(252, 65, 17)); 
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 300, 50, 300)); 

        JButton browseBtn = createStyledButton("Browse Menu");
        JButton cartBtn = createStyledButton("View Cart");
        JButton loginBtn = createStyledButton("Login");
        JButton registerBtn = createStyledButton("Register");

        buttonPanel.add(browseBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(cartBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(loginBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(registerBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("© 2025 Rev & Roast", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Event Listeners
        loginBtn.addActionListener(e -> {
            new login();
            dispose();
        });

        registerBtn.addActionListener(e -> {
            new register();
            dispose();
        });

        browseBtn.addActionListener(e -> {
            new menu();
            dispose();
        });

        cartBtn.addActionListener(e -> {
            new cart();
            dispose();
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(new Color(255, 87, 34)); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        return btn;
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            backgroundImage = new ImageIcon(fileName).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        new home();
    }
}
