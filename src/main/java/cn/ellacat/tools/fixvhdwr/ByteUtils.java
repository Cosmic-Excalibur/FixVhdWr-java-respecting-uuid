package cn.ellacat.tools.fixvhdwr;

/**
 * @author wjc133
 * @edited Astrageldon
 */
public class ByteUtils {
    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static String toUUID(byte[] uuid) {
        if (uuid == null || uuid.length != 16) {
            return null;
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : uuid) {
            hex.append(String.format("%02x", b));
        }
        String hexString = hex.toString();

        return String.format("%s-%s-%s-%s-%s",
                hexString.substring(0, 8),
                hexString.substring(8, 12),
                hexString.substring(12, 16),
                hexString.substring(16, 20),
                hexString.substring(20));
    }


}
