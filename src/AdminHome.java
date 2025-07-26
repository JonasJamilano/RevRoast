import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AdminHome extends JFrame {
    private String username;

    public AdminHome(String username) {
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Rev & Roast - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            JPanel mainPanel = new BackgroundPanel("f1bg.jpg");
            mainPanel.setLayout(new BorderLayout());

            JPanel headerPanel = createHeaderPanel();
            mainPanel.add(headerPanel, BorderLayout.NORTH);

            JPanel buttonPanel = createButtonPanel();
            mainPanel.add(buttonPanel, BorderLayout.CENTER);

            mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

            add(mainPanel);
            setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load resources: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            new login();
        }
    }

    private JPanel createHeaderPanel() {
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

        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 300, 50, 300));

        JButton productManagementBtn = createStyledButton("Product Management");
        JButton userManagementBtn = createStyledButton("User Management");
        JButton inventoryAuditBtn = createStyledButton("Inventory Audit");
        JButton logoutBtn = createStyledButton("Logout");

        buttonPanel.add(productManagementBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(userManagementBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(inventoryAuditBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(logoutBtn);

        productManagementBtn.addActionListener(e -> openWindow(new ProductManagement(username, "admin")));
        userManagementBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "User Management feature coming soon"));
        inventoryAuditBtn.addActionListener(e -> openWindow(new InventoryAudit(username)));
        logoutBtn.addActionListener(e -> {
            dispose();
            new login();
        });

        return buttonPanel;
    }

    private JLabel createFooterPanel() {
        JLabel footer = new JLabel("© 2025 Rev & Roast - Admin Access", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        return footer;
    }

    private void openWindow(JFrame newWindow) {
        try {
            newWindow.setVisible(true);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to open window: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(getBackground());
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(252, 17, 17));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(220, 0, 0));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(252, 17, 17));
            }
        });

        return btn;
    }

    class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(String fileName) throws IllegalArgumentException {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(fileName));
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                throw new IllegalArgumentException("Failed to load background image");
            }
            this.backgroundImage = icon.getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminHome("admin_test"));
    }
}