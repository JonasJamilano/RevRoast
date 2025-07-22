import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import javax.swing.*;
import java.io.FileNotFoundException;

public class register extends JFrame {
    private JLabel imagePreview;
    private Properties dbProperties;
    private JTextField nameField, usernameField, emailField;
    private JPasswordField passwordField;

    public register() {
        loadDatabaseConfig(); // Load database configuration first

        setTitle("Rev & Roast - Register");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(35, 35, 35));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Register to Rev & Roast", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(new Color(252, 65, 17));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Full Name Field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        panel.add(nameField, gbc);

        // Username Field
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Email Field
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Profile Picture Upload
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(createLabel("Profile Picture:"), gbc);
        gbc.gridx = 1;
        JButton uploadBtn = new JButton("Upload Image");
        panel.add(uploadBtn, gbc);

        // Image Preview
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(100, 100));
        imagePreview.setOpaque(true);
        imagePreview.setBackground(Color.LIGHT_GRAY);
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(imagePreview, gbc);

        // Register Button
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerBtn = createRegisterButton();
        panel.add(registerBtn, gbc);

        // Login Link
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createLoginLink(), gbc);

        // Event Handlers
        uploadBtn.addActionListener(this::handleImageUpload);
        registerBtn.addActionListener(this::handleRegistration);

        add(panel);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createRegisterButton() {
        JButton button = new JButton("Register");
        button.setPreferredSize(new Dimension(200, 40));
        button.setBackground(new Color(252, 17, 17));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        return button;
    }

    private JLabel createLoginLink() {
        JLabel loginLabel = new JLabel("Already have an account? Click here to login", SwingConstants.CENTER);
        loginLabel.setForeground(new Color(255, 200, 150));
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new login().setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginLabel.setForeground(new Color(255, 180, 120));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginLabel.setForeground(new Color(255, 200, 150));
            }
        });
        return loginLabel;
    }

    private void loadDatabaseConfig() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in classpath");
            }
            dbProperties.load(input);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load database configuration: " + e.getMessage(),
                "Configuration Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void handleImageUpload(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imagePreview.setIcon(new ImageIcon(img));
        }
    }

    private void handleRegistration(ActionEvent e) {
        String fullName = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                dbProperties.getProperty("db.url"),
                dbProperties.getProperty("db.username"),
                dbProperties.getProperty("db.password"));
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)")) {

            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, "customer");

            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Registered successfully!");
                new login().setVisible(true);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new register().setVisible(true));
    }
}