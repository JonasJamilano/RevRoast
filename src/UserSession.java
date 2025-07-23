public class UserSession {
    private static String username;
    private static String role;

    public static void setUser(String uname, String urole) {
        username = uname;
        role = urole;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static void clear() {
        username = null;
        role = null;
    }

    public static boolean isLoggedIn() {
        return username != null;
    }
}
