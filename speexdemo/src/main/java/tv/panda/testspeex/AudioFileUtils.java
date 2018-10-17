package tv.panda.testspeex;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class AudioFileUtils {


	// 音频输入-麦克风
	public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

	// 说明1：采用频率为16000时请将CHANNELS设置为2，CHANNEL_CONFIG设置为AudioFormat.CHANNEL_IN_STEREO，此时spx转为wav后有点杂音
	// 说明2：采用频率为8000时请将CHANNELS设置为1，CHANNEL_CONFIG设置为AudioFormat.CHANNEL_IN_MONO，此时杂音较小
	// 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	public final static int AUDIO_SAMPLE_RATE = 8000; // 44.1KHz,普遍使用的频率

	//声道数
	public final static int CHANNELS = 1;

	public final static int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

	public final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

	// 录音输出文件
	private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
	private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";
	private final static String AUDIO_AMR_FILENAME = "FinalAudio.amr";
	private final static String AUDIO_SPX_FILENAME = "FinalAudio.spx";
	private final static String DECODE_RAW_FILENAME = "DecodeRawAudio.raw";
	private final static String DECODE_WAV_FILENAME = "DecodeWavAudio.wav";

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
	 * 获取编码后的AMR格式音频文件路径
	 * 
	 * @return
	 */
	public static String getAMRFilePath() {
		String mAudioAMRPath = "";
		if (isSdcardExit()) {
			mAudioAMRPath = getFileBasePath() + "/" + AUDIO_AMR_FILENAME;
		}
		return mAudioAMRPath;
	}

	/**
	 * 获取压缩后的spx格式音频文件路径
	 *
	 * @return
	 */
	public static String getSpxFilePath() {
		String mAudioSPXPath = "";
		if (isSdcardExit()) {
			mAudioSPXPath = getFileBasePath() + "/" + AUDIO_SPX_FILENAME;
		}
		return mAudioSPXPath;
	}

	/**
	 * 获取解码后的Wav格式音频文件路径
	 *
	 * @return
	 */
	public static String getDecodeWavFilePath() {
		String mDecodeWavPath = "";
		if (isSdcardExit()) {
			mDecodeWavPath = getFileBasePath() + "/" + DECODE_WAV_FILENAME;
		}
		return mDecodeWavPath;
	}


	/**
	 * 获取解码后的Raw格式音频文件路径
	 *
	 * @return
	 */
	public static String getDecodeRawFilePath() {
		String mDecodeRawPath = "";
		if (isSdcardExit()) {
			mDecodeRawPath = getFileBasePath() + "/" + DECODE_RAW_FILENAME;
		}
		return mDecodeRawPath;
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
	
	public static String getFileBasePath(){
		String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpeexTest/recordFile";
		File filePath = new File(fileBasePath);
		if (!filePath.exists()) {
			try {  
                //按照指定的路径创建文件夹  
				filePath.mkdirs();  
            } catch (Exception e) { } 
		}
		return fileBasePath;
	}


	/**
	 * 这里得到可播放的音频文件
	 * @param inFilename
	 * @param outFilename
	 * @param bufferSizeInBytes
     */
	public static void raw2Wav(String inFilename, String outFilename, int bufferSizeInBytes) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = AudioFileUtils.AUDIO_SAMPLE_RATE;
		int channels = CHANNELS;
		long byteRate = 16 * AudioFileUtils.AUDIO_SAMPLE_RATE * channels / 8;
		byte[] data = new byte[bufferSizeInBytes];
		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
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

	/**
	 * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。 为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
	 * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有 自己特有的头文件。
	 */
	private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
									 int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
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
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}

	/**
	 * 将raw转化为spx文件
	 * @param inFileName
	 * @param outFileName
     */
	public static void raw2spx(String inFileName, String outFileName) {

		FileInputStream rawFileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			rawFileInputStream = new FileInputStream(inFileName);
			fileOutputStream = new FileOutputStream(outFileName);
			byte[] rawbyte = new byte[320];
			byte[] encoded = new byte[160];
			//将原数据转换成spx压缩的文件，speex只能编码160字节的数据，需要使用一个循环
			int readedtotal = 0;
			int size = 0;
			int encodedtotal = 0;
			while ((size = rawFileInputStream.read(rawbyte, 0, 320)) != -1) {
				readedtotal = readedtotal + size;
				short[] rawdata = ShortByteUtil.byteArray2ShortArray(rawbyte);
				int encodesize = SpeexUtil.getInstance().encode(rawdata, 0, encoded, rawdata.length);
				fileOutputStream.write(encoded, 0, encodesize);
				encodedtotal = encodedtotal + encodesize;
			}
			fileOutputStream.close();
			rawFileInputStream.close();
		} catch (Exception e) {

		}

	}

	/**
	 * 将spx文件转化为raw文件
	 * @param inFileName
	 * @param outFileName
     */
	public static void spx2raw(String inFileName, String outFileName) {
		FileInputStream inAccessFile = null;
		FileOutputStream fileOutputStream = null;
		try {
			inAccessFile = new FileInputStream(inFileName);
			fileOutputStream = new FileOutputStream(outFileName);
			byte[] inbyte = new byte[20];
			short[] decoded = new short[160];
			int readsize = 0;
			int readedtotal = 0;
			int decsize = 0;
			int decodetotal = 0;
			while ((readsize = inAccessFile.read(inbyte, 0, 20)) != -1) {
				readedtotal = readedtotal + readsize;
				decsize = SpeexUtil.getInstance().decode(inbyte, decoded, readsize);
				fileOutputStream.write(ShortByteUtil.shortArray2ByteArray(decoded), 0, decsize*2);
				decodetotal = decodetotal + decsize;
			}
			fileOutputStream.close();
			inAccessFile.close();
		} catch (Exception e) {

		}
	}

	/**
	 * 将spx转化为wav文件
	 * @param inFileName
	 * @param outFileName
	 * @param bufferSizeInBytes
     */
	public static void spx2Wav(String inFileName, String outFileName, int bufferSizeInBytes){
		FileInputStream inAccessFile = null;
		FileOutputStream fileOutputStream = null;
		String tempFile = getFileBasePath() + "/" + "temp.raw";
		try {
			inAccessFile = new FileInputStream(inFileName);
			fileOutputStream = new FileOutputStream(tempFile);
			byte[] inbyte = new byte[20];
			short[] decoded = new short[160];
			int readsize = 0;
			int readedtotal = 0;
			int decsize = 0;
			int decodetotal = 0;
			while ((readsize = inAccessFile.read(inbyte, 0, 20)) != -1) {
				readedtotal = readedtotal + readsize;
				decsize = SpeexUtil.getInstance().decode(inbyte, decoded, readsize);
				fileOutputStream.write(ShortByteUtil.shortArray2ByteArray(decoded), 0, decsize*2);
				decodetotal = decodetotal + decsize;
			}
			fileOutputStream.close();
			inAccessFile.close();
			raw2Wav(tempFile, outFileName, bufferSizeInBytes);
		} catch (Exception e) {

		}
	}
}
