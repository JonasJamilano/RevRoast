import java.sql.*;

public class PrivilegeManager {

    // Simply sets the role for the current session
    public static void setSessionRole(Connection conn, String role) throws SQLException {
        String dbRole = "revandroast_" + role.toLowerCase();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET ROLE '" + dbRole + "'");
        }
    }

    // Verifies user has the required role (from users table)
    public static boolean hasRequiredRole(Connection conn, int userId, String requiredRole) throws SQLException {
        String query = "SELECT 1 FROM users WHERE user_id = ? AND role = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, requiredRole);
            return pstmt.executeQuery().next();
        }
    }
}