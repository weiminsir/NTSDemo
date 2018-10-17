package tv.panda.testspeex;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;

public class PlayerUtils implements OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnPreparedListener {

	public MediaPlayer mMediaPlayer;
	private OnPlayListener mListener;

	public PlayerUtils() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnCompletionListener(this);
	}

	/**
	 * 设置音频源并缓冲准备播放
	 * @param url
	 */
	public void setUrlPrepare(String url){
		try {
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			
		}
	}
	
	public void setOnPlayListener(OnPlayListener listener){
		this.mListener = listener;
	}
	
	/**
	 * 播放前调用setUrlPrepare
	 */
	public void play() {
		mMediaPlayer.start();
	}

	public void pause() {
		mMediaPlayer.pause();
	}

	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	/**
	 * 通过onPrepared播放
	 */
	public void onPrepared(MediaPlayer arg0) {
		if (mListener != null) {
			mListener.onPlayReady();
		}
		arg0.start();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if (mListener != null) {
			mListener.onPlayFinish();
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		
	}
	
	public interface OnPlayListener {
		
		/**
		 * 可以开始播放
		 */
		void onPlayReady();
		
		/**
		 * 语音播放结束
		 */
		void onPlayFinish();
	}
}
