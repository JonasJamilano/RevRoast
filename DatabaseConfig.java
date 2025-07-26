import java.io.*;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    // Default username/password
    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    // Role-based username/password
    public static String getUsernameByRole(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return properties.getProperty("db.admin.username");
            case "staff":
                return properties.getProperty("db.staff.username");
            case "customer":
                return properties.getProperty("db.customer.username");
            default:
                return getDbUsername();
        }
    }

    public static String getPasswordByRole(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return properties.getProperty("db.admin.password");
            case "staff":
                return properties.getProperty("db.staff.password");
            case "customer":
                return properties.getProperty("db.customer.password");
            default:
                return getDbPassword();
        }
    }
}
