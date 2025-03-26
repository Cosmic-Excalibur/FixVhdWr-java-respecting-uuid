package cn.ellacat.tools.fixvhdwr;

/**
 * @author wjc133
 */
public enum VhdType {
    HD_TYPE_NONE(0),
    HD_TYPE_FIXED(2),    // fixed-allocation disk
    HD_TYPE_DYNAMIC(3),  // dynamic disk
    HD_TYPE_DIFF(4);     // differencing disk

    private int code;

    VhdType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
