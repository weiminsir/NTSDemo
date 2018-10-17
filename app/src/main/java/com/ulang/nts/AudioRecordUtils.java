package com.ulang.nts;

import android.media.AudioRecord;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import okhttp3.WebSocket;
import okio.ByteString;

public class AudioRecordUtils {
    private String TAG = NetRequest.TAG;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;
    // mAudioRaw ，麦克风
    private String mAudioRaw = "";

    // mAudioWav可播放的wav音频文件
    private String mAudioWav = "";

    //压缩后的spx文件
    private String mAudioSpx = "";

    private AudioRecord audioRecord;
    private volatile boolean isRecord = false;// 设置正在录制的状态

    private static AudioRecordUtils mInstance;

    private WebSocket mWebSocket;

    private AudioRecordUtils() {

    }

    public synchronized static AudioRecordUtils getInstance() {
        if (mInstance == null)
            mInstance = new AudioRecordUtils();
        return mInstance;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.mWebSocket = webSocket;
    }

    public int startRecordAndFile() {
        Log.d("NLPService", "startRecordAndFile");

        // 判断是否有外部存储设备sdcard
        if (AudioFileUtils.isSdcardExit()) {
            if (isRecord) {
                return ErrorCode.E_STATE_RECODING;
            } else {
                if (audioRecord == null) {
                    creatAudioRecord();
                }
                audioRecord.startRecording();
                // 让录制状态为true
                isRecord = true;
                // 开启音频文件写入线程
                new Thread(new AudioRecordThread()).start();
                return ErrorCode.SUCCESS;
            }

        } else {
            return ErrorCode.E_NOSDCARD;
        }

    }

    public void stopRecordAndFile() {
        close();
    }

    public long getRecordFileSize() {
        return AudioFileUtils.getFileSize(mAudioWav);
    }

    private void close() {
        if (audioRecord != null) {
            isRecord = false;// 停止文件写入
            audioRecord.stop();
            audioRecord.release();// 释放资源
            audioRecord = null;
        }
    }

    private void creatAudioRecord() {
        // 获取音频文件路径
        mAudioRaw = AudioFileUtils.getRawFilePath();
        mAudioWav = AudioFileUtils.getWavFilePath();

        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AudioFileUtils.AUDIO_SAMPLE_RATE,
                AudioFileUtils.CHANNEL_CONFIG, AudioFileUtils.AUDIO_FORMAT);

        // 创建AudioRecord对象
        // MONO单声道，STEREO为双声道立体声
        audioRecord = new AudioRecord(AudioFileUtils.AUDIO_INPUT, AudioFileUtils.AUDIO_SAMPLE_RATE,
                AudioFileUtils.CHANNEL_CONFIG, AudioFileUtils.AUDIO_FORMAT, bufferSizeInBytes);
    }

    class AudioRecordThread implements Runnable {
        @Override
        public void run() {

            writeDateTOFile();// 往文件中写入裸数据
            AudioFileUtils.raw2Wav(mAudioRaw, mAudioWav, bufferSizeInBytes);// 给裸数据加上头文件

        }
    }

    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */



    private void writeDateTOFile() {
        Log.d("NLPService", "writeDateTOFile");
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(mAudioRaw);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (isRecord) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (fos != null)
                fos.close();// 关闭写入流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
