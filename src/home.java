import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class home extends JFrame {

    public home() {
        // Force cross-platform look and feel to respect button colors
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Rev & Roast - Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background panel
        JPanel mainPanel = new BackgroundPanel("src/f1bg.jpg");
        mainPanel.setLayout(new BorderLayout());

        // Stylish title
        JLabel title = new JLabel("Rev & Roast", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 42));
        title.setForeground(new Color(255, 87, 34));
        title.setBorder(new EmptyBorder(30, 0, 30, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 280, 30, 280));

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
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setBackground(new Color(255, 87, 34)); // Orange-red
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 87, 34), 1, true)); // Rounded border
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        return btn;
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            backgroundImage = new ImageIcon(fileName).getImage();
            if (backgroundImage == null) {
                System.out.println("Background image not found: " + fileName);
            }
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
