import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class login extends JFrame {
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

        // Email Label and Field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // Password Label and Field
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(252, 17, 17));
        loginBtn.setForeground(Color.WHITE);
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
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(new Color(139, 69, 19));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));

        backButton.addActionListener(e -> {
            new home(); // home will now check session
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
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {

                stmt.setString(1, email);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String username = rs.getString("name"); // fetch name
                    String role = rs.getString("role");

                    // Set session
                    UserSession.setUser(username, role);

                    JOptionPane.showMessageDialog(this, "Welcome, " + username + " (" + role + ")!");
                    new home();  // no need to pass username
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(login::new);
    }
}
