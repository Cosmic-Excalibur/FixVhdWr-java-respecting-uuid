package cn.ellacat.tools.fixvhdwr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;

/**
 * @author wjc133
 */
public class Footer {
    /**
     * 识别标志，为"conectix"，用于判断VHD是否有效
     */
    private static final String cookie = "conectix";
    /**
     * 硬盘特征
     *
     * @see cn.ellacat.tools.fixvhdwr.FeatureFlag
     */
    private int features = FeatureFlag.HD_RESERVED.getCode();
    /**
     * VHD版本，用处不大，用于结构判断，似乎更多的会用到crtr_ver
     */
    private int ffVersion = 0x00010000;
    /**
     * 描述中指下一个结构的起始绝对字节位置，如果是动态磁盘，这表明了dd_hdr（稍后会提到）的物理字节位置。
     * 如果是固定磁盘，似乎总是0xFFFFFFFF。
     */
    private long dataOffset = 0xFFFFFFFF;
    /**
     * 创建时间的时间戳。这个时间戳是指2000年1月1日00:00:00起始的秒值。和HFS对时间的描述方式一致，
     * 也就是说此处数值加上0xB492F400 (即2000/01/01 00:00:00)，即是标准的HFS时间方法对本值的解释。
     */
    private int timestamp;
    /**
     * 创建VHD的应用
     */
    private String crtrApp = "vbox";
    /**
     * 创建版本。根据版本号可实施对应的方法。似乎目前只有当创建版本号为0x00000001时，对于bitmap的操作会有不同
     */
    private int crtrVersion = 0x00050002;
    /**
     * 创建使用的操作系统
     */
    private String crtrOs = "Wi2k";
    /**
     * 创建时$虚拟磁盘大小，再强调一下，这个大小指虚拟出来的磁盘的可用寻址空间。
     * 如果是固定格式的VHD，这个大小等于$VHD文件的大小减去1扇区(尾部 hd_ftr)。
     */
    private long origSize;
    /**
     * 或许是用于vhd在线扩容后的最后大小表述，没仔细研究过。同样指$虚拟磁盘的大小，
     * 即虚拟出来的磁盘的可用寻址空间，如果没有扩容，和orig_size相同
     */
    private long currSize;
    /**
     * This field stores the cylinder, heads, and sectors per track value for the hard disk.
     * 存储每个磁道的柱面、磁头和扇区
     * 将硬盘配置为ATA硬盘时，ATA控制器使用CHS值（即Cylinder，Heads，Sectors / Track）来确定磁盘的大小。
     * 当用户创建特定大小的硬盘时，虚拟机中的硬盘映像的大小小于用户创建的大小。 这是因为从硬盘大小计算出的CHS值被舍去
     */
    private int geometry;
    /**
     * 非常重要的，表示这个VHD的类型
     */
    private int type = VhdType.HD_TYPE_FIXED.getCode();
    /**
     * 整个扇区所有字节(当然一开始不包括checksum本身)相加得到32位数，再按位取反
     */
    private int checksum;
    /**
     * 用于VHD识别号，如果是有差异磁盘，这个ID非常重要，决定了VHD间的主从关系(16byte)
     */
    private byte[] uuid;
    /**
     * 保留空间（428byte），包括了saved和hidden字段
     */
    private byte[] reserved = new byte[428];

    public static String getCookie() {
        return cookie;
    }

    public int getFeatures() {
        return features;
    }

    public Footer setFeatures(int features) {
        this.features = features;
        return this;
    }

    public int getFfVersion() {
        return ffVersion;
    }

    public Footer setFfVersion(int ffVersion) {
        this.ffVersion = ffVersion;
        return this;
    }

    public long getDataOffset() {
        return dataOffset;
    }

    public Footer setDataOffset(long dataOffset) {
        this.dataOffset = dataOffset;
        return this;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Footer setTimestamp(int timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getCrtrVersion() {
        return crtrVersion;
    }

    public Footer setCrtrVersion(int crtrVersion) {
        this.crtrVersion = crtrVersion;
        return this;
    }

    public long getOrigSize() {
        return origSize;
    }

    public Footer setOrigSize(long origSize) {
        this.origSize = origSize;
        return this;
    }

    public long getCurrSize() {
        return currSize;
    }

    public Footer setCurrSize(long currSize) {
        this.currSize = currSize;
        return this;
    }

    public int getGeometry() {
        return geometry;
    }

    public Footer setGeometry(int geometry) {
        this.geometry = geometry;
        return this;
    }

    public int getType() {
        return type;
    }

    public Footer setType(int type) {
        this.type = type;
        return this;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public String getCrtrApp() {
        return crtrApp;
    }

    public String getCrtrOs() {
        return crtrOs;
    }

    public void setUUID(byte[] uuid) { this.uuid = uuid; }

    @Override
    public String toString() {
        return "Footer{" +
                "cookie='" + cookie + '\'' +
                ", features=" + features +
                ", ffVersion=" + ffVersion +
                ", dataOffset=" + dataOffset +
                ", timestamp=" + timestamp +
                ", crtrApp='" + crtrApp + '\'' +
                ", crtrVersion=" + crtrVersion +
                ", crtrOs='" + crtrOs + '\'' +
                ", origSize=" + origSize +
                ", currSize=" + currSize +
                ", geometry=" + geometry +
                ", type=" + type +
                ", checksum=" + checksum +
                ", uuid=" + Arrays.toString(uuid) +
                ", recerved=" + Arrays.toString(reserved) +
                '}';
    }

    public byte[] toBytes(int sections) {
        // TODO: 18-4-21 生成字节数组
        Date now = new Date();
        int sec = (int) (now.getTime() / 1000);
        this.timestamp = sec - 0xB492F400;
        this.geometry = GeometryCalculator.getGeometry(sections);
        ChecksumCalculator.gen(this);

        ByteBuffer buffer = ByteBuffer.allocate(512);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(Footer.getCookie().getBytes());
        buffer.putInt(this.features);
        buffer.putInt(this.ffVersion);
        buffer.putLong(this.dataOffset);
        buffer.putInt(this.timestamp);
        buffer.put(this.crtrApp.getBytes());
        buffer.putInt(this.crtrVersion);
        buffer.put(this.crtrOs.getBytes());
        buffer.putLong(this.origSize);
        buffer.putLong(this.currSize);
        buffer.putInt(this.geometry);
        buffer.putInt(this.type);
        buffer.putInt(this.checksum);
        buffer.put(this.uuid);
        buffer.put(this.reserved);
        return buffer.array();
    }


}
