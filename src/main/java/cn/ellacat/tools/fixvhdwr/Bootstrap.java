package cn.ellacat.tools.fixvhdwr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author wjc133
 * @edited Astrageldon
 */
public class Bootstrap {
    public static void main(String[] args) throws IOException, IndexOutOfBoundsException {
        if (args == null || args.length < 3 || args.length % 2 == 0) {
            System.out.println("Usage: fixvhdwr <mbrRawPath> <vhdPath> <maxSectors> [rawPath1] [sectorIndex1] [rawPath2] [sectorIndex2] ...");
            return;
        }
        String mbrRawPath = args[0];
        String vhdPath = args[1];
        int maxSectors = Integer.parseInt(args[2]);
        VhdWriter writer = new VhdWriter(mbrRawPath, vhdPath, maxSectors);
        for (int i=3; i<args.length; i+=2) {
            int sectorIndex = Integer.parseInt(args[i+1]);
            if (sectorIndex >= maxSectors) {
                throw new IndexOutOfBoundsException("Index " + sectorIndex + " >= " + maxSectors + ", gg.\n");
            }
            writer.update(args[i], sectorIndex);
        }
        writer.finish();
    }
}
