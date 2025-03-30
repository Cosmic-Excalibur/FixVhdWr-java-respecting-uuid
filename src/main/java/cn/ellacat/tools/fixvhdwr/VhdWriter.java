package cn.ellacat.tools.fixvhdwr;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author wjc133
 * @edited Astrageldon
 */
public class VhdWriter {
    /**
     * 写入数据到VHD文件
     *
     * @param rawFilePath 原始二进制数据文件路径(由nasm生成的二进制文件)
     * @param vhdFilePath VHD文件的路径
     * @param maxSector   最大写入扇区数
     */

    private byte[] filebuffer;
    private Footer footer;
    File vhdFile;

    VhdWriter(String mbrRawFilePath, String vhdFilePath, int maxSectors) throws IOException {
        File mbrRawFile = getRawFile(mbrRawFilePath);
        System.out.println("mbrRawFile is " + mbrRawFile.length() + " bytes.");
        vhdFile = getVhdFile(vhdFilePath);
        filebuffer = getMbrRawData(mbrRawFile, maxSectors);
        if (mbrRawFile.length() < 512) {
            filebuffer[510] = (byte) 0x55;
            filebuffer[511] = (byte) 0xAA;
            System.out.println("Automatically supplied magic footer for sector 0.");
        }
        footer = buildFooter(maxSectors);
        if (!vhdFile.exists()) {
            createNewFile(vhdFile);
            String hexStr = UUID.randomUUID().toString().replaceAll("-", "");
            footer.setUUID(ByteUtils.toBytes(hexStr));
        } else {
            byte[] vhd_uuid = getVhdUUID(vhdFile);
            if (vhd_uuid == null) {
                String hexStr = UUID.randomUUID().toString().replaceAll("-", "");
                footer.setUUID(ByteUtils.toBytes(hexStr));
            } else {
                System.out.println("Reusing already existing VHD uuid " + ByteUtils.toUUID(vhd_uuid));
                footer.setUUID(vhd_uuid);
            }
        }
    }

    public void update(String rawFilePath, int sectorIndex) throws IOException, IndexOutOfBoundsException {
        System.out.println("Writing " + rawFilePath + " to sector " + sectorIndex);
        File rawFile = getRawFile(rawFilePath);
        byte[] buffer = getRawData(rawFile);
        System.arraycopy(buffer, 0, filebuffer, sectorIndex * 512, buffer.length);
    }

    public void finish() throws IOException{
        writeRawData2Vhd(filebuffer, vhdFile);
        writeFooter2Vhd(footer, vhdFile);
        System.out.println("Task finished, vhdFile is " + vhdFile.length() + " bytes.");
    }

    private Footer buildFooter(int maxSectors) {
        Footer footer = new Footer();
        long size = maxSectors * 512;
        footer.setCurrSize(size);
        footer.setOrigSize(size);
        return footer;
    }

    private void writeRawData2Vhd(byte[] rawData, File vhdFile) throws IOException {
        FileOutputStream stream = new FileOutputStream(vhdFile);
        stream.write(rawData);
        stream.flush();
        stream.close();
    }

    private File getVhdFile(String vhdFilePath) throws IOException {
        return new File(vhdFilePath);
    }

    private void createNewFile(File file) throws IOException {
        boolean success = file.createNewFile();
        if (!success) {
            throw new IllegalArgumentException("Permission denied.");
        }
    }

    private byte[] getVhdUUID(File vhdFile) throws IOException {
        if (vhdFile.length() < 512) return null;
        byte[] buffer = new byte[512];
        RandomAccessFile input = new RandomAccessFile(vhdFile, "r");
        input.seek(vhdFile.length() - 512);
        input.read(buffer, 0, 512);
        input.close();
        if (!Arrays.equals(buffer, 0, 8, Footer.getCookie().getBytes(), 0, 8)) {
            return null;
        }
        byte[] uuid = new byte[16];
        System.arraycopy(buffer, 68, uuid, 0, 16);
        return uuid;
    }

    private byte[] getMbrRawData(File mbrRawFile, int maxSectors) throws IOException {
        byte[] buffer = new byte[maxSectors * 512];
        FileInputStream input = new FileInputStream(mbrRawFile);
        input.read(buffer);
        input.close();
        return buffer;
    }

    private byte[] getRawData(File mbrRawFile) throws IOException {
        FileInputStream input = new FileInputStream(mbrRawFile);
        byte[] buffer = new byte[((int)mbrRawFile.length() + 511) >> 9 << 9];
        input.read(buffer);
        input.close();
        return buffer;
    }

    private File getRawFile(String rawFilePath) throws FileNotFoundException {
        File file = new File(rawFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Raw file " + rawFilePath + " does not exist.");
        }
        return file;
    }

    private void writeFooter2Vhd(Footer footer, File vhdFile) throws IOException {
        long length = vhdFile.length();
        int sections = (int) (length % 512 == 0 ? length / 512 : length / 512 + 1);
        byte[] footerBytes = footer.toBytes(sections);
        FileOutputStream stream = new FileOutputStream(vhdFile, true);
        stream.write(footerBytes);
        stream.flush();
        stream.close();
    }
}
