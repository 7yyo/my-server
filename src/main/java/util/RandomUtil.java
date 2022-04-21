package util;

import java.util.Random;

public class RandomUtil {

    static byte[] AZ = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'R',
            'S', 'T', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    public static byte[] randomBytes(int n) {
        byte[] rs = new byte[n];
        int min = 0;
        int max = AZ.length - 1;
        Random random = new Random();
        for (int i = 0; i < rs.length; i++) {
            int index = random.nextInt(max + min) + min;
            rs[i] = AZ[index];
        }
        return rs;
    }
}
