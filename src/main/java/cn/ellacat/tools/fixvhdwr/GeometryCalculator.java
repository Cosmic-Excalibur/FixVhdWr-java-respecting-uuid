package cn.ellacat.tools.fixvhdwr;

/**
 * @author wjc133
 */
public class GeometryCalculator {

    public static int getGeometry(int totalSectors) {
        int cylinders;
        int heads;
        int sectorsPerTrack;
        int cylinderTimesHeads;
        if (totalSectors > 65535 * 16 * 255) {
            totalSectors = 65535 * 16 * 255;
        }

        if (totalSectors >= 65535 * 16 * 63) {
            sectorsPerTrack = 255;
            heads = 16;
            cylinderTimesHeads = totalSectors / sectorsPerTrack;
        } else {
            sectorsPerTrack = 17;
            cylinderTimesHeads = totalSectors / sectorsPerTrack;

            heads = (cylinderTimesHeads + 1023) / 1024;

            if (heads < 4) {
                heads = 4;
            }
            if (cylinderTimesHeads >= (heads * 1024) || heads > 16) {
                sectorsPerTrack = 31;
                heads = 16;
                cylinderTimesHeads = totalSectors / sectorsPerTrack;
            }
            if (cylinderTimesHeads >= (heads * 1024)) {
                sectorsPerTrack = 63;
                heads = 16;
                cylinderTimesHeads = totalSectors / sectorsPerTrack;
            }
        }
        cylinders = cylinderTimesHeads / heads;

        return (cylinders << 16) | (heads << 8) | sectorsPerTrack;
    }
}
