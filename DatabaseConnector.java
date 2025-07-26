import java.sql.*;
import java.io.*;
import java.util.Properties;

public class DatabaseConnector {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/config.properties")) {
            props.load(input);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    // Default root connection (for login verification)
    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url") + "?useSSL=false&serverTimezone=UTC";
        String user = props.getProperty("db.username");
        String pass = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }

    // Role-based connection
    public static Connection getConnection(String role) throws SQLException {
        String url = props.getProperty("db.url") + "?useSSL=false&serverTimezone=UTC";
        String user = "";
        String pass = "";

        switch (role.toLowerCase()) {
            case "admin":
                user = props.getProperty("db.admin.username");
                pass = props.getProperty("db.admin.password");
                break;
            case "staff":
                user = props.getProperty("db.staff.username");
                pass = props.getProperty("db.staff.password");
                break;
            case "customer":
                user = props.getProperty("db.customer.username");
                pass = props.getProperty("db.customer.password");
                break;
            default:
                throw new SQLException("Unknown role: " + role);
        }

        return DriverManager.getConnection(url, user, pass);
    }
}
