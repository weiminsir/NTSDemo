package com.ulang.nts;

import android.Manifest;
import android.media.AudioRecord;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * second method,socket transfer pcm and convert
 */
public class STTWebSocketActivity extends AppCompatActivity implements View.OnTouchListener {

    private AudioRecord audioRecord;
    private int bufferSizeInBytes = 0;
    private volatile boolean isRecord = false;// 设置正在录制的状态
    private WebSocket webSocket;
    private MyWebSocketListener socketListener;
    private final String TAG = NetRequest.TAG;
    private final String sttApi = NetRequest.BASE_URL + "api/sttstream/text/admin/123456";


    @BindView(R.id.stt_btn_local)
    Button sttBtn;
    @BindView(R.id.stt_tv)
    TextView sttTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        ButterKnife.bind(this);
        sttBtn.setOnTouchListener(this);
        socketListener = new MyWebSocketListener();
    }

    public int startRecordAndFile() {
        Log.d("NLPService", "startRecordAndFile");
        // 判断是否有外部存储设备sdcard
        if (AudioFileUtils.isSdcardExit()) {
            if (isRecord) {
                return ErrorCode.E_STATE_RECODING;
            } else {
                if (audioRecord == null) {
                    createAudioRecord();
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


    private void createAudioRecord() {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AudioFileUtils.AUDIO_SAMPLE_RATE,
                AudioFileUtils.CHANNEL_CONFIG, AudioFileUtils.AUDIO_FORMAT);

        // 创建AudioRecord对象
        // MONO单声道，STEREO为双声道立体声
        audioRecord = new AudioRecord(AudioFileUtils.AUDIO_INPUT, AudioFileUtils.AUDIO_SAMPLE_RATE,
                AudioFileUtils.CHANNEL_CONFIG, AudioFileUtils.AUDIO_FORMAT, bufferSizeInBytes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,}, 1
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {


        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                createWebSocket();
                break;
            case MotionEvent.ACTION_UP:
                stopRecordAndFile();
                Log.d(TAG, "stopRecordAndFile");
//                stt();
                break;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.dispatchKeyEvent(event);
    }

    private void stopRecordAndFile() {
        if (audioRecord != null) {
            isRecord = false;// 停止文件写入
            audioRecord.stop();
            audioRecord.release();// 释放资源
            audioRecord = null;
        }

    }

    private void createWebSocket() {
        Request request = new Request.Builder().url(sttApi).build();
        NetRequest.getOkHttpClient().newWebSocket(request, socketListener);
    }

    class AudioRecordThread implements Runnable {

        @Override
        public void run() {
            ByteBuffer audioBuffer = ByteBuffer.allocateDirect(bufferSizeInBytes * 100).order(ByteOrder.LITTLE_ENDIAN);
            int readSize = 0;
            Log.d(TAG, "isRecord=" + isRecord);
            while (isRecord) {
                readSize = audioRecord.read(audioBuffer, audioBuffer.capacity());
                if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize == AudioRecord.ERROR_BAD_VALUE) {
                    Log.d("NLPService", "Could not read audio data.");
                    break;
                }
                boolean send = webSocket.send(ByteString.of(audioBuffer));
                Log.d("NLPService", "send=" + send);
                audioBuffer.clear();
            }
            webSocket.send("close");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.cancel();
            webSocket = null;
        }
    }

    class MyWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            output("onOpen: " + "webSocket connect success");
            STTWebSocketActivity.this.webSocket = webSocket;
            startRecordAndFile();
        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sttTv.setText("Stt result:" + text);
                }
            });

            output("onMessage1: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("onMessage2 byteString: " + bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            output("onClosing: " + code + "/" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            output("onClosed: " + code + "/" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("onFailure: " + t.getMessage());
        }

        private void output(String s) {
            Log.d("NLPService", s);
        }

    }


}
