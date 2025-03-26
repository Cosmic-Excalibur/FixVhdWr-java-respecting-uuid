package cn.ellacat.tools.fixvhdwr;

/**
 * @author wjc133
 */
public enum FeatureFlag {
    HD_NO_FEATURES(0x00000000),
    HD_TEMPORARY(0x00000001),  // disk can be deleted on shutdown
    HD_RESERVED(0x00000002);   // NOTE: must always be set

    private int code;

    FeatureFlag(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "FeatureFlag{" +
                "code=" + code +
                '}';
    }
}
