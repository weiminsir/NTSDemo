package tv.panda.testspeex;

/**
 * Created by zhangxiaobei on 2017/2/21.
 */

public class SpeexUtil {
    private static final int DEFAULT_COMPRESSION = 4;
    private static SpeexUtil speexUtil;

    static {
        try {
            System.loadLibrary("speex");
        } catch (Throwable var1) {
            var1.printStackTrace();
        }

        speexUtil = null;
    }

    SpeexUtil() {
        this.open(4);
    }

    public static SpeexUtil getInstance() {
        if(speexUtil == null) {
            Class var0 = SpeexUtil.class;
            synchronized(SpeexUtil.class) {
                if(speexUtil == null) {
                    speexUtil = new SpeexUtil();
                }
            }
        }

        return speexUtil;
    }

    public native int open(int var1);

    public native int getFrameSize();

    public native int decode(byte[] var1, short[] var2, int var3);

    public native int encode(short[] var1, int var2, byte[] var3, int var4);

    public native void close();
}
