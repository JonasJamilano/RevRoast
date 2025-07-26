import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserManagement extends JFrame {
    private String username;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagement(String username) {
        this.username = username;
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setTitle("User Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Manage Users", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Playfair Display", Font.BOLD, 28));
        headerLabel.setForeground(new Color(252, 65, 17));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Email", "Role"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton addBtn = createStyledButton("Add User");
        JButton editBtn = createStyledButton("Edit User");
        JButton deleteBtn = createStyledButton("Delete User");
        JButton backBtn = createStyledButton("Back");

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(backBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Button Actions
        addBtn.addActionListener(e -> addUser());
        editBtn.addActionListener(e -> editUser());
        deleteBtn.addActionListener(e -> deleteUser());
        backBtn.addActionListener(e -> {
            dispose();
            new AdminHome(username);
        });
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(252, 17, 17));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

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

    private void loadUsers() {
        tableModel.setRowCount(0); // Clear table first
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, name, email, role FROM users")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ADD USER
    private void addUser() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        String[] roles = {"admin", "staff", "customer"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)")) {

                stmt.setString(1, nameField.getText());
                stmt.setString(2, emailField.getText());
                stmt.setString(3, new String(passwordField.getPassword())); // Store plain for now
                stmt.setString(4, roleBox.getSelectedItem().toString());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User added successfully.");
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // EDIT USER
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 2);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 3);

        JTextField nameField = new JTextField(currentName);
        JTextField emailField = new JTextField(currentEmail);
        String[] roles = {"admin", "staff", "customer"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setSelectedItem(currentRole);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE users SET name=?, email=?, role=? WHERE user_id=?")) {

                stmt.setString(1, nameField.getText());
                stmt.setString(2, emailField.getText());
                stmt.setString(3, roleBox.getSelectedItem().toString());
                stmt.setInt(4, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User updated successfully.");
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // DELETE USER
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE user_id=?")) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                loadUsers();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}