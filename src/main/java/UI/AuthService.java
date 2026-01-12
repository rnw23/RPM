package UI;

public class AuthService {
    public static boolean check(String username, char[] password) {
        return "admin".equals(username) && "1234".equals(new String(password));
    }
}
