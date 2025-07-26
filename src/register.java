import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import java.sql.*;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class register extends JFrame {
    private JLabel imagePreview;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$");
    private byte[] profilePictureData = null;

    public register() {
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

        JLabel title = new JLabel("Register to Rev & Roast", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(new Color(252, 65, 17));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel pictureLabel = new JLabel("Profile Picture:");
        pictureLabel.setForeground(Color.WHITE);
        panel.add(pictureLabel, gbc);

        gbc.gridx = 1;
        JButton uploadBtn = new JButton("Upload Image");
        panel.add(uploadBtn, gbc);

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

        gbc.gridy++;
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(252, 17, 17));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn, gbc);

        gbc.gridy++;
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
        panel.add(loginLabel, gbc);

        uploadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    String extension = getExtension(f);
                    return extension != null && (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png"));
                }

                public String getDescription() {
                    return "Image Files (*.jpg, *.jpeg, *.png)";
                }

                private String getExtension(File f) {
                    String ext = null;
                    String s = f.getName();
                    int i = s.lastIndexOf('.');
                    if (i > 0 && i < s.length() - 1) {
                        ext = s.substring(i + 1).toLowerCase();
                    }
                    return ext;
                }
            });

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedImage originalImage = ImageIO.read(file);
                    Image scaledImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imagePreview.setIcon(new ImageIcon(scaledImage));

                    // Convert image to byte array for database storage
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String format = file.getName().substring(file.getName().lastIndexOf('.') + 1);
                    ImageIO.write(originalImage, format, baos);
                    profilePictureData = baos.toByteArray();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerBtn.addActionListener(e -> {
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            // Validate all fields are filled
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate email format
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                JOptionPane.showMessageDialog(this,
                        "Invalid email format. Please use a valid email address (e.g., user@example.com).",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate password strength
            if (password.length() < 8) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 8 characters long.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                // Check if email already exists
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT 1 FROM users WHERE email = ?")) {
                    checkStmt.setString(1, email);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this,
                                    "Email already registered. Please use a different email.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                // Start transaction
                conn.setAutoCommit(false);

                try {
                    // Insert user with profile picture if available
                    String sql = profilePictureData != null ?
                            "INSERT INTO users (name, email, password, role, rpm_points, profile_picture) VALUES (?, ?, ?, ?, ?, ?)" :
                            "INSERT INTO users (name, email, password, role, rpm_points) VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, fullName);
                        stmt.setString(2, email);
                        stmt.setString(3, password);
                        stmt.setString(4, "customer");
                        stmt.setInt(5, 0); // Trigger will add 100 points

                        if (profilePictureData != null) {
                            stmt.setBytes(6, profilePictureData);
                        }

                        int affectedRows = stmt.executeUpdate();

                        if (affectedRows == 0) {
                            throw new SQLException("Creating user failed, no rows affected.");
                        }

                        // Commit transaction
                        conn.commit();

                        JOptionPane.showMessageDialog(this,
                                "Registration successful!\nYou received 100 RPM points as a welcome bonus.");
                        new login().setVisible(true);
                        dispose();
                    }
                } catch (SQLException ex) {
                    // Rollback transaction if there's an error
                    conn.rollback();
                    handleSQLException(ex);
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                handleSQLException(ex);
            }
        });

        add(panel);
        setVisible(true);
    }

    private void handleSQLException(SQLException ex) {
        ex.printStackTrace();
        if ("45000".equals(ex.getSQLState())) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email format. Please use a valid email address.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if ("45001".equals(ex.getSQLState())) {
            JOptionPane.showMessageDialog(this,
                    "Email already registered. Please use a different email.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new register().setVisible(true);
        });
    }
}