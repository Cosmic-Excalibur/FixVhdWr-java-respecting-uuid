package cn.ellacat.tools.fixvhdwr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author wjc133
 */
public class ChecksumCalculator {
    public static void gen(Footer footer) {
        int checksum = 0;
        footer.setChecksum(0);
        int value = getIntValue(footer.getCookie());
        checksum += value;
        checksum += footer.getFeatures();
        checksum += footer.getFfVersion();
        checksum += footer.getDataOffset();
        checksum += footer.getTimestamp();
        checksum += getIntValue(footer.getCrtrApp());
        checksum += footer.getCrtrVersion();
        checksum += getIntValue(footer.getCrtrOs());
        checksum += footer.getOrigSize();
        checksum += footer.getCurrSize();
        checksum += footer.getGeometry();
        checksum += footer.getType();
        checksum += footer.getChecksum();
        footer.setChecksum(~checksum);
    }

    private static int getIntValue(String str) {
        byte[] bytes = str.getBytes();
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    }
}
