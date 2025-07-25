import java.awt.*;
import javax.swing.*;

public class AdminHome extends JFrame {
    private String username;

    public AdminHome(String username) {
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Rev & Roast - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new BackgroundPanel("f1bg.jpg");
        mainPanel.setLayout(new BorderLayout());

        // HEADER: Title and username at the top
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Playfair Display", Font.BOLD, 36));
        title.setForeground(new Color(252, 65, 17));
        headerPanel.add(title, BorderLayout.WEST);

        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (Admin)");
        welcomeLabel.setFont(new Font("Poppins", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.RED);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // CENTER: Main buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 300, 50, 300));

        // Admin-specific buttons
        JButton userManagementBtn = createStyledButton("User Management");
        JButton productManagementBtn = createStyledButton("Product Management");
        JButton inventoryAuditBtn = createStyledButton("Inventory Audit");
        JButton logoutBtn = createStyledButton("Logout");

        buttonPanel.add(userManagementBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(productManagementBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(inventoryAuditBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // FOOTER
        JLabel footer = new JLabel("© 2025 Rev & Roast - Admin Access", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Button actions
        userManagementBtn.addActionListener(e -> {
            new UserManagement(username);
            dispose();
        });

        productManagementBtn.addActionListener(e -> {
            new ProductManagement(username);
            dispose();
        });

        inventoryAuditBtn.addActionListener(e -> {
            new InventoryAudit(username);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            UserSession.clear();
            new home();
            dispose();
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(Color.RED);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        return btn;
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            backgroundImage = new ImageIcon(getClass().getClassLoader().getResource(fileName)).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        new AdminHome("admin_user");
    }
}