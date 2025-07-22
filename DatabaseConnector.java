import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/revandroast";
    private static final String USER = "root";
    private static final String PASSWORD = "Vylett18";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connection successful!");
        } catch (Exception e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
