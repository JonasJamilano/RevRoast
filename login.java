import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class login extends JFrame {
    public login() {
        setTitle("Rev & Roast - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center window

        // Main panel
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

        // Username label and field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE); // White font
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // White font
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(252, 17, 17));
        loginBtn.setForeground(Color.WHITE);
        panel.add(loginBtn, gbc);

        // Register clickable label
        gbc.gridy++;
        JLabel registerLabel = new JLabel("Don't have an account? Click here to register", SwingConstants.CENTER);
        registerLabel.setForeground(new Color(255, 200, 150)); 
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add mouse listener to simulate a link
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose(); // close login window
                new register(); // open register window
            }

            public void mouseEntered(MouseEvent e) {
                registerLabel.setForeground(new Color(255, 180, 120)); 
            }

            public void mouseExited(MouseEvent e) {
                registerLabel.setForeground(new Color(255, 200, 150));
            }
        });

        panel.add(registerLabel, gbc);

        // Dummy login action (to be replaced later with DB logic)
        loginBtn.addActionListener(e -> {
            String user = usernameField.getText();
            char[] pass = passwordField.getPassword();
            JOptionPane.showMessageDialog(this, "Welcome " + user + "!");
        });

        // Back button
        gbc.gridy++;
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(new Color(139, 69, 19));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));

        backButton.addActionListener(e -> {
            new home(); // open home window
            dispose(); // close login window
        });

        panel.add(backButton, gbc);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(login::new);
    }
}