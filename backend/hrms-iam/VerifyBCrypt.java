import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class VerifyBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String password = "password123";
        
        boolean matches = encoder.matches(password, hash);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Matches: " + matches);
        
        // 生成新的 hash
        String newHash = encoder.encode(password);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
    }
}
