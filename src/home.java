import java.awt.*;
import javax.swing.*;

public class home extends JFrame {

    public home() {
        this(null);
    }

    public home(String username) {
        setTitle("Rev & Roast - Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new BackgroundPanel("src/f1bg.jpg");
        mainPanel.setLayout(new BorderLayout());

        // HEADER: Title and username at the top
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel("Rev & Roast", SwingConstants.LEFT);
        title.setFont(new Font("Playfair Display", Font.BOLD, 36));
        title.setForeground(new Color(252, 65, 17));
        headerPanel.add(title, BorderLayout.WEST);

        if (username != null) {
            JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
            welcomeLabel.setFont(new Font("Poppins", Font.PLAIN, 16));
            welcomeLabel.setForeground(Color.RED);
            headerPanel.add(welcomeLabel, BorderLayout.EAST);
        }

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // CENTER: Main buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 300, 50, 300));

        JButton browseBtn = createStyledButton("Browse Menu");
        JButton cartBtn = createStyledButton("View Cart");

        buttonPanel.add(browseBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(cartBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        if (username == null) {
            JButton loginBtn = createStyledButton("Login");
            JButton registerBtn = createStyledButton("Register");

            buttonPanel.add(loginBtn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            buttonPanel.add(registerBtn);

            loginBtn.addActionListener(e -> {
                new login();
                dispose();
            });

            registerBtn.addActionListener(e -> {
                new register();
                dispose();
            });
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // FOOTER
        JLabel footer = new JLabel("© 2025 Rev & Roast", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions
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
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
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
        new home(); // launch without login
    }
}
