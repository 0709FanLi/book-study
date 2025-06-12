import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encoded = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Encoded: " + encoded);
        
        // 验证
        boolean matches = encoder.matches(password, encoded);
        System.out.println("Matches: " + matches);
    }
} 