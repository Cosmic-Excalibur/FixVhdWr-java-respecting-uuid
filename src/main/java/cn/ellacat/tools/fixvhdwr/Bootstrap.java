package cn.ellacat.tools.fixvhdwr;

import java.io.IOException;

/**
 * @author wjc133
 * @edited Astrageldon
 */
public class Bootstrap {
    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 3) {
            System.out.println("Usage: fixvhdwr <rawPath> <vhdPath> <maxSector>");
            return;
        }
        String rawPath = args[0];
        String vhdPath = args[1];
        String maxSector = args[2];
        VhdWriter writer = new VhdWriter();
        writer.write(rawPath, vhdPath, Integer.valueOf(maxSector));
        System.out.println("DONE.");
    }
}
