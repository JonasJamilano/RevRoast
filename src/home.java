import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.sql.*;

public class home extends JFrame {
    private Integer currentUserId;
    private String currentUsername;
    private String customerName; // Added to store customer's actual name

    public home() {
        this(null, null); // Guest constructor
    }

    public home(String username) {
        this(username, null); // For backward compatibility
    }

    public home(String username, Integer userId) {
        this.currentUserId = userId;
        this.currentUsername = username;

        // Fetch customer name if logged in
        if (userId != null) {
            this.customerName = getCustomerName(userId);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Rev & Roast - Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new BackgroundPanel("src/f1bg.jpg");
        mainPanel.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel("Rev & Roast", SwingConstants.LEFT);
        title.setFont(new Font("Playfair Display", Font.BOLD, 36));
        title.setForeground(new Color(252, 65, 17));
        headerPanel.add(title, BorderLayout.WEST);

        // User Info Panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        if (currentUsername != null && customerName != null) {
            JLabel welcomeLabel = new JLabel("Welcome, " + customerName + "!"); // Use customerName instead of username
            welcomeLabel.setFont(new Font("Poppins", Font.PLAIN, 16));
            welcomeLabel.setForeground(Color.RED);
            userPanel.add(welcomeLabel);

            JButton logoutBtn = new JButton("Logout");
            logoutBtn.setFont(new Font("Poppins", Font.PLAIN, 12));
            logoutBtn.setBackground(new Color(252, 65, 17));
            logoutBtn.setForeground(Color.WHITE);
            logoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            logoutBtn.addActionListener(e -> {
                new home();
                dispose();
            });
            userPanel.add(logoutBtn);
        }
        headerPanel.add(userPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 300, 50, 300));

        JButton browseBtn = createStyledButton("Browse Menu");
        buttonPanel.add(browseBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        if (currentUsername == null) {
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
        } else {
            // Only show order-related buttons to logged-in users
            JButton pendingOrdersBtn = createStyledButton("Pending Orders");
            JButton orderHistoryBtn = createStyledButton("Order History");

            buttonPanel.add(pendingOrdersBtn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            buttonPanel.add(orderHistoryBtn);

            pendingOrdersBtn.addActionListener(e -> {
                new PendingOrdersView(currentUserId).setVisible(true);
                dispose();
            });

            orderHistoryBtn.addActionListener(e -> {
                new OrderHistory(currentUserId).setVisible(true); // Changed from OrderDetailsView to OrderHistory
                dispose();
            });
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 Rev & Roast", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Browse Menu button action
        browseBtn.addActionListener(e -> {
            try {
                EventQueue.invokeLater(() -> {
                    if (currentUserId != null) {
                        new menu(currentUserId).setVisible(true);
                    } else {
                        new menu().setVisible(true);
                    }
                    dispose();
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error opening menu: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }

    // Method to fetch customer name from database
    private String getCustomerName(int userId) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT name FROM users WHERE user_id = ?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback to username if name not found
            return currentUsername;
        }
        return currentUsername; // Fallback to username if name not found
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(new Color(252, 65, 17));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(252, 40, 40));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(252, 65, 17));
            }
        });

        return btn;
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String fileName) {
            try {
                backgroundImage = new ImageIcon(fileName).getImage();
                if (backgroundImage == null) {
                    setBackground(new Color(30, 30, 30));
                }
            } catch (Exception e) {
                setBackground(new Color(30, 30, 30));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new home().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage(),
                        "Fatal Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}