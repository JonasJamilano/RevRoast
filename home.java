import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class home extends JFrame {

    public home() {
        setTitle("Rev & Roast - Home");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new BackgroundPanel("src/f1bg.jpg");
        mainPanel.setBackground(new Color(50, 50, 50));
        mainPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Rev & Roast", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(new Color(252, 65, 17));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Panel for vertical buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 300, 50, 300)); // padding

        JButton browseBtn = new JButton("Browse Menu");
        JButton cartBtn = new JButton("View Cart");
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        Dimension buttonSize = new Dimension(200, 40);
        browseBtn.setMaximumSize(buttonSize);
        cartBtn.setMaximumSize(buttonSize);
        loginBtn.setMaximumSize(buttonSize);
        registerBtn.setMaximumSize(buttonSize);

        // Center text
        browseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add spacing between buttons
        buttonPanel.add(browseBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(cartBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(loginBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(registerBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("© 2025 Rev & Roast", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new login();
                dispose();
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new register();
                dispose();
            }
        });

        browseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new menu();
                dispose();
            }
        });

        cartBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new cart();
                dispose();
            }
        });

        setVisible(true);
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
        new home();
    }
}