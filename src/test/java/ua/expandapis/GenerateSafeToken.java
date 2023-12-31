package ua.expandapis;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

public class GenerateSafeToken {
    @Test
    public void generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[66]; // 66 bytes * 8 = 528 bits, a little bit more than
                                    // the 512 required bits
        //remove from result special symbols like "-"
        random.nextBytes(bytes);
        var encoder = Base64.getUrlEncoder().withoutPadding();
        System.out.println(encoder.encodeToString(bytes));
    }
}
