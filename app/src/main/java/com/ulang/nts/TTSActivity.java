package com.ulang.nts;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Text to speech convert wav audio,Okhttp download wav,AudioTrack play
 */
public class TTSActivity extends AppCompatActivity {


    private final static String TAG = "NLPService";
    private int mOutput = AudioManager.STREAM_SYSTEM;
    private int mSamplingRate = 16000;
    @BindView(R.id.tts_content_et)
    EditText ttsContent;
    @BindView(R.id.tts_btn)
    Button ttsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        ButterKnife.bind(this);
        ttsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {
                        play();
                    }
                }.start();

            }
        });

    }

    public void play() {
        OkHttpClient client = NetRequest.getOkHttpClient();
        Request request = new Request.Builder().url(NetRequest.BASE_URL + "api/tts?text=今天是星期三").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    final byte[] data = response.body().bytes();

                    try {
                        Log.d(TAG, "audioTrack start ");
                        AudioTrack audioTrack = new AudioTrack(mOutput, mSamplingRate,
                                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                                data.length, AudioTrack.MODE_STATIC);
                        audioTrack.write(data, 0, data.length);
                        audioTrack.play();
                        while (audioTrack.getPlaybackHeadPosition() < (data.length / 2)) {
                            Thread.yield();
                        }
                        audioTrack.stop();
                        audioTrack.release();
                    } catch (IllegalArgumentException e) {

                    } catch (IllegalStateException e) {
                    }
                }


            }
        });
    }

    public boolean DownloadSmallFile(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://192.168.1.21:8088/wav/test.wav").build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }

            ResponseBody body = response.body();
            long contentLength = body.contentLength();
            Log.d(TAG, "contentLength: " + contentLength);
            BufferedSource source = body.source();
            File file = new File(AudioFileUtils.getDownWavFilePath());
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(source);
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

//    public void speak(final String text) {
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
////        params.put("user", mUuid);
////        params.put("lang", mLang);
//        params.put("text", text);
//        Log.d(TAG, "text: " + text);
//        String apiAddress = String.format(NetRequest.BASE_URL + "api/tts?text=今天是星期三");
//        Log.d(TAG, "server: " + apiAddress);
//        client.get(this, apiAddress, params,
//                new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        Log.d(TAG, "scode: " + statusCode);
//                        final byte[] data = responseBody;
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Log.d(TAG, "audioTrack start ");
//                                    AudioTrack audioTrack = new AudioTrack(mOutput, mSamplingRate,
//                                            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
//                                            data.length, AudioTrack.MODE_STATIC);
//                                    audioTrack.write(data, 0, data.length);
//                                    audioTrack.play();
//                                    while (audioTrack.getPlaybackHeadPosition() < (data.length / 2)) {
//                                        Thread.yield();
//                                    }
//                                    audioTrack.stop();
//                                    audioTrack.release();
//                                } catch (IllegalArgumentException e) {
//                                } catch (IllegalStateException e) {
//                                }
//                            }
//                        }).start();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        Log.e(TAG, "scode: " + statusCode, error);
//                    }
//                });
//    }

}
