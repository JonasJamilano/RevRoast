import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class login extends JFrame {
    // Role constants
    private static final String ROLE_STAFF = "staff";
    private static final String ROLE_CUSTOMER = "customer";

    // Static block for cross-platform styling
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", new Color(252, 17, 17));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focusPainted", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public login() {
        setTitle("Rev & Roast - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Rev & Roast", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(new Color(252, 65, 17));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Email Field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Login Button (Now properly red on macOS)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton loginBtn = createStyledButton("Login");
        panel.add(loginBtn, gbc);

        // Register Label
        gbc.gridy++;
        JLabel registerLabel = new JLabel("Don't have an account? Click here to register", SwingConstants.CENTER);
        registerLabel.setForeground(new Color(255, 200, 150));
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new register();
            }
            public void mouseEntered(MouseEvent e) {
                registerLabel.setForeground(new Color(255, 180, 120));
            }
            public void mouseExited(MouseEvent e) {
                registerLabel.setForeground(new Color(255, 200, 150));
            }
        });
        panel.add(registerLabel, gbc);

        // Back Button
        gbc.gridy++;
        JButton backButton = createStyledButton("Back to Home");
        backButton.setBackground(new Color(139, 69, 19)); // Brown color for back button
        backButton.addActionListener(e -> {
            new home();
            dispose();
        });
        panel.add(backButton, gbc);

        // Login Logic
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT user_id, name, role, email FROM users WHERE email = ? AND password = ?")) {

                stmt.setString(1, email);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("name");
                    String role = rs.getString("role");

                    JOptionPane.showMessageDialog(this,
                            "Welcome back, " + username + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    dispose();

                    if ("admin".equalsIgnoreCase(role)) {
                        new AdminHome(username);
                    } else if (ROLE_STAFF.equalsIgnoreCase(role)) {
                        new StaffHome(username);
                    } else {
                        new home(username, userId);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid email or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel);
        setVisible(true);
    }

    // Custom button creator with macOS fixes
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
        btn.setBackground(new Color(252, 17, 17)); // Default red
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(220, 0, 0)); // Darker red
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(252, 17, 17)); // Original red
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}