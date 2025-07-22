import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/revandroast";
    private static final String USER = "root"; // Change if you have a different username
    private static final String PASSWORD = "ccinfom123"; // Add password

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}



