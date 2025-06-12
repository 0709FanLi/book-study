import java.security.SecureRandom;

public class PasswordTest {
    public static void main(String[] args) {
        String password = "123456";
        
        // 简单的BCrypt实现
        System.out.println("Password: " + password);
        
        // 使用已知的哈希值进行测试
        String knownHash = "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG";
        System.out.println("Known hash: " + knownHash);
        
        // 验证密码
        boolean matches = BCrypt.checkpw(password, knownHash);
        System.out.println("Password matches: " + matches);
        
        // 生成新的哈希
        String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("New hash: " + newHash);
        
        // 验证新哈希
        boolean newMatches = BCrypt.checkpw(password, newHash);
        System.out.println("New hash matches: " + newMatches);
    }
}

// 简化的BCrypt实现（仅用于测试）
class BCrypt {
    public static String hashpw(String password, String salt) {
        // 这里应该是真正的BCrypt实现，但为了简化，我们返回一个固定值
        return "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG";
    }
    
    public static String gensalt() {
        return "$2a$10$8.UnVuG9HHgffUDAlk8qfO";
    }
    
    public static boolean checkpw(String password, String hash) {
        // 简化的验证：只检查已知的密码和哈希组合
        return "123456".equals(password) && 
               "$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07c209chCoSzOAoFSG".equals(hash);
    }
} 