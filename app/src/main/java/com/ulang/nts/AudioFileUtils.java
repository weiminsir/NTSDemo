package com.ulang.nts;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class AudioFileUtils {


    // 音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    // 说明1：采用频率为16000时请将CHANNELS设置为2，CHANNEL_CONFIG设置为AudioFormat.CHANNEL_IN_STEREO，此时spx转为wav后有点杂音
    // 说明2：采用频率为8000时请将CHANNELS设置为1，CHANNEL_CONFIG设置为AudioFormat.CHANNEL_IN_MONO，此时杂音较小
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public final static int AUDIO_SAMPLE_RATE = 8000; // 44.1KHz,普遍使用的频率


    public final static int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    public final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // 录音输出文件
    private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
    private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";//本地录音
    private final static String DECODE_WAV_FILENAME = "DownWavAudio.wav";//下载音频

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取麦克风输入的原始音频流文件路径
     *
     * @return
     */
    public static String getRawFilePath() {
        String mAudioRawPath = "";
        if (isSdcardExit()) {
            mAudioRawPath = getFileBasePath() + "/" + AUDIO_RAW_FILENAME;
        }

        return mAudioRawPath;
    }

    /**
     * 获取编码后的WAV格式音频文件路径
     *
     * @return
     */
    public static String getWavFilePath() {
        String mAudioWavPath = "";
        if (isSdcardExit()) {
            mAudioWavPath = getFileBasePath() + "/" + AUDIO_WAV_FILENAME;
        }
        return mAudioWavPath;
    }


    /**
     * 获取解码后的Wav格式音频文件路径
     *
     * @return
     */
    public static String getDownWavFilePath() {
        String mDecodeWavPath = "";
        if (isSdcardExit()) {
            mDecodeWavPath = getFileBasePath() + "/" + DECODE_WAV_FILENAME;
        }
        return mDecodeWavPath;
    }


    /**
     * 获取文件大小
     *
     * @param path,文件的绝对路径
     * @return
     */
    public static long getFileSize(String path) {
        File mFile = new File(path);
        if (!mFile.exists())
            return -1;
        return mFile.length();
    }

    public static String getFileBasePath() {
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NTS/audioFile";
        File filePath = new File(fileBasePath);
        if (!filePath.exists()) {
            try {
                //按照指定的路径创建文件夹  
                filePath.mkdirs();
            } catch (Exception e) {
            }
        }
        return fileBasePath;
    }


    /**
     * 这里得到可播放的音频文件
     *
     * @param inFilename
     * @param outFilename
     * @param bufferSizeInBytes
     */

    public static void raw2Wav(String inFilename, String outFilename, int bufferSizeInBytes) {
        Log.d("NLPService", "raw2Wav");
        FileInputStream in = null;
        RandomAccessFile out = null;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new RandomAccessFile(outFilename, "rw");
            fixWavHeader(out, AUDIO_SAMPLE_RATE, 1, AudioFormat.ENCODING_PCM_16BIT);

            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fixWavHeader(RandomAccessFile file, int rate, int channels, int format) {
        try {
            int blockAlign;
            if (format == AudioFormat.ENCODING_PCM_16BIT)
                blockAlign = channels * 2;
            else
                blockAlign = channels;

            int bitsPerSample;
            if (format == AudioFormat.ENCODING_PCM_16BIT)
                bitsPerSample = 16;
            else
                bitsPerSample = 8;

            long dataLen = file.length() - 44;

            // hard coding
            byte[] header = new byte[44];
            header[0] = 'R'; // RIFF/WAVE header
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            header[4] = (byte) ((dataLen + 36) & 0xff);
            header[5] = (byte) (((dataLen + 36) >> 8) & 0xff);
            header[6] = (byte) (((dataLen + 36) >> 16) & 0xff);
            header[7] = (byte) (((dataLen + 36) >> 24) & 0xff);
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';
            header[12] = 'f'; // 'fmt ' chunk
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            header[16] = 16; // 4 bytes: size of 'fmt ' chunk
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            header[20] = 1; // format = 1
            header[21] = 0;
            header[22] = (byte) channels;
            header[23] = 0;
            header[24] = (byte) (rate & 0xff);
            header[25] = (byte) ((rate >> 8) & 0xff);
            header[26] = (byte) ((rate >> 16) & 0xff);
            header[27] = (byte) ((rate >> 24) & 0xff);
            header[28] = (byte) ((rate * blockAlign) & 0xff);
            header[29] = (byte) (((rate * blockAlign) >> 8) & 0xff);
            header[30] = (byte) (((rate * blockAlign) >> 16) & 0xff);
            header[31] = (byte) (((rate * blockAlign) >> 24) & 0xff);
            header[32] = (byte) (blockAlign); // block align
            header[33] = 0;
            header[34] = (byte) bitsPerSample; // bits per sample
            header[35] = 0;
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            header[40] = (byte) (dataLen & 0xff);
            header[41] = (byte) ((dataLen >> 8) & 0xff);
            header[42] = (byte) ((dataLen >> 16) & 0xff);
            header[43] = (byte) ((dataLen >> 24) & 0xff);

            file.seek(0);
            file.write(header, 0, 44);
        } catch (Exception e) {

        } finally {

        }
    }
}
