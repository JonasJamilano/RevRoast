import java.sql.*;
import java.io.*;
import java.util.Properties;

public class DatabaseConnector {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver missing!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("src/config.properties")) {
            props.load(input);

            String url = props.getProperty("db.url") + "?useSSL=false&serverTimezone=UTC";
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, pass);
        } catch (IOException e) {
            throw new SQLException("Failed to load config", e);
        }
    }
}
