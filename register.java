import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public class register extends JFrame {
    private JLabel imagePreview;

    public register() {
        setTitle("Rev & Roast - Register");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

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

        // Full Name
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Upload profile picture
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel pictureLabel = new JLabel("Profile Picture:");
        pictureLabel.setForeground(Color.WHITE);
        panel.add(pictureLabel, gbc);

        gbc.gridx = 1;
        JButton uploadBtn = new JButton("Upload Image");
        panel.add(uploadBtn, gbc);

        // Preview area
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
        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(252, 17, 17));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn, gbc);

        // Back to Login clickable label
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

        // Upload button action
        uploadBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imagePreview.setIcon(new ImageIcon(img));
            }
        });

        // Register button logic (temporary)
        registerBtn.addActionListener(e -> {
            String fullName = nameField.getText();
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Simulate database save
            System.out.println("Registering user:");
            System.out.println("Full Name: " + fullName);
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);

            JOptionPane.showMessageDialog(this, "Registered successfully as " + username + "!");

            new home().setVisible(true);
            dispose();
        });

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new register().setVisible(true);
        });
    }
}