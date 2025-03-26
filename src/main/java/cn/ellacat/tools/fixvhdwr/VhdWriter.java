package cn.ellacat.tools.fixvhdwr;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author wjc133
 */
public class VhdWriter {
    /**
     * 写入数据到VHD文件
     *
     * @param rawFilePath 原始二进制数据文件路径(由nasm生成的二进制文件)
     * @param vhdFilePath VHD文件的路径。如果路径为空，默认使用user目录下生成raw.vhd，若路径不存在，则创建
     * @param maxSector   最大写入扇区数，0为不限制
     */
    public void write(String rawFilePath, String vhdFilePath, int maxSector) throws IOException {
        File rawFile = getRawFile(rawFilePath);
        System.out.println("Write start, rawFile is " + rawFile.length() + " bytes.");
        File vhdFile = getVhdFile(vhdFilePath);
        byte[] rawData = getRawData(rawFile, maxSector);
        if (rawFile.length() < 512) {
            rawData[510] = (byte)0x55;
            rawData[511] = (byte)0xAA;
        }
        Footer footer = buildFooter(rawFile, vhdFile);
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
                System.out.println("Reuse already existing VHD uuid " + ByteUtils.toUUID(vhd_uuid));
                footer.setUUID(vhd_uuid);
            }
        }
        writeRawData2Vhd(rawData, vhdFile);
        writeFooter2Vhd(footer, vhdFile);
        System.out.println("Write finish, vhdFile is " + vhdFile.length() + " bytes.");
    }

    private Footer buildFooter(File rawFile, File vhdFile) {
        Footer footer = new Footer();
        long size = rawFile.length() > 512 ? rawFile.length() : 512;
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
            throw new IllegalArgumentException("权限不足");
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

    private byte[] getRawData(File rawFile, int maxSector) throws IOException {
        byte[] buffer = new byte[maxSector * 512];
        FileInputStream input = new FileInputStream(rawFile);
        input.read(buffer);
        input.close();
        return buffer;
    }

    private File getRawFile(String rawFilePath) throws FileNotFoundException {
        File file = new File(rawFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException("raw file is not exist");
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
