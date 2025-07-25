import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserManagement extends JFrame {
    private Connection conn;
    private DefaultTableModel tableModel;
    private String adminUsername;

    public UserManagement(String adminUsername) {
        this.adminUsername = adminUsername;
        initializeUI();
        connectToDatabase();
        loadUserData();
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setTitle("User Management");
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(
                new Object[]{"User ID", "Name", "Email", "Role", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only role is editable
            }
        };

        JTable userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton updateRoleBtn = new JButton("Update Role");
        JButton deleteUserBtn = new JButton("Delete User");
        JButton backBtn = new JButton("Back to Dashboard");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(updateRoleBtn);
        buttonPanel.add(deleteUserBtn);
        buttonPanel.add(backBtn);

        // Button actions
        refreshBtn.addActionListener(e -> loadUserData());
        
        updateRoleBtn.addActionListener(e -> updateUserRole(userTable));
        
        deleteUserBtn.addActionListener(e -> deleteUser(userTable));

        backBtn.addActionListener(e -> {
            new AdminHome(adminUsername);
            dispose();
        });

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadUserData() {
        try {
            tableModel.setRowCount(0);
            
            String query = "SELECT user_id, name, email, role, created_at " +
                         "FROM users ORDER BY created_at DESC";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateUserRole(JTable userTable) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 3);
        
        String newRole = (String) JOptionPane.showInputDialog(this,
                "Select new role for user ID " + userId,
                "Update Role",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"admin", "staff", "customer"},
                currentRole);
        
        if (newRole != null) {
            try {
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET role = ? WHERE user_id = ?"
                );
                stmt.setString(1, newRole);
                stmt.setInt(2, userId);
                
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "User role updated successfully");
                    loadUserData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating role: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void deleteUser(JTable userTable) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user '" + userName + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM users WHERE user_id = ?"
                );
                stmt.setInt(1, userId);
                
                int deleted = stmt.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully");
                    loadUserData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dispose() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }
}