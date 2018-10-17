package tv.panda.testspeex;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2018/1/15 0015.
 */

public class RecordManager {
    String TAG = "ulangRequest";
    public static final float MinRequiredVoiceLen = 1.5f; // seconds
    public static final float MaxRequiredVoiceLen = 50.0f; // seconds
    private int samplesPerSec = 8000;//每秒采样大小单位
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int audioSource = MediaRecorder.AudioSource.MIC;
    private AudioRecord audioRecord;

    private boolean bRecordRunning = false;//正在录音中


    public boolean isRunning() {
        return bRecordRunning;
    }

    static RecordManager recordManager;

    private RecordManager() {
    }

    private int audioBufferSize = AudioRecord.getMinBufferSize(
            samplesPerSec,
            channelConfig,
            audioFormat);

    public static  RecordManager getInstance() {
        if (recordManager == null) {
            synchronized (RecordManager.class) {
                if (recordManager == null) {
                    recordManager = new RecordManager();
                }
            }
        }
        return recordManager;
    }

    public void stop() {
        Log.d(TAG, "manager stopRecord ");
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();//释放资源
            audioRecord = null;
        }
    }

    public void stopRecord() {
        Log.d(TAG, "manager stopRecord");
        bRecordRunning = false;
    }

    public interface onRecordCallBack {
        void recordStart();

        float recording(float duration);

        void recordComplete(File file, float duration);
    }


    public void record(onRecordCallBack mRecordCallBack) {
        mRecordCallBack.recordStart();
        File file;
        try {
            file = new File("voice/weimin", ".pcm");
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            int bufferSize = audioBufferSize;
            audioRecord = new AudioRecord(audioSource,
                    samplesPerSec,
                    channelConfig,
                    audioFormat,
                    audioBufferSize);
            byte[] buffer = new byte[bufferSize];
            audioRecord.startRecording();
            bRecordRunning = true;
            int length = 0;
            float duration = 0;
            while (bRecordRunning) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                if (AudioRecord.ERROR_INVALID_OPERATION != bufferReadResult || AudioRecord.ERROR_BAD_VALUE != bufferReadResult) {
                    length += bufferReadResult;
                    duration = ((float) length) / samplesPerSec;
                    mRecordCallBack.recording(duration);
                    dos.write(buffer);
                    if (duration >= MaxRequiredVoiceLen) {
                        break;
                    }
                }
            }
            Log.d(TAG, "manager new  recordComplete");
//            if (duration > MinRequiredVoiceLen) {
//            }
            stop();
            dos.close();
            mRecordCallBack.recordComplete(file, duration);
        } catch (Throwable t) {
            Log.e("AudioRecord", "Recording Failed");
        }
    }
}
