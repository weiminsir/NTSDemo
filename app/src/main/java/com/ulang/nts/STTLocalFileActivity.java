package com.ulang.nts;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ulang.nts.model.STT;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * first method,download file complete ,then upload...
 */
public class STTLocalFileActivity extends AppCompatActivity implements View.OnTouchListener {

    private final static String TAG = NetRequest.TAG;
    @BindView(R.id.stt_btn_local)
    Button sttBtn;
    @BindView(R.id.stt_tv)
    TextView sttTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt);
        ButterKnife.bind(this);
        sttBtn.setOnTouchListener(this);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.dispatchKeyEvent(event);
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
//            if (grantResults.length > 0) {
//
////                Log.d(TAG, "获取录音权限成功");
//            }

        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        AudioRecordUtils utils = AudioRecordUtils.getInstance();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                utils.startRecordAndFile();
                break;
            case MotionEvent.ACTION_UP:
                utils.stopRecordAndFile();
                Log.d(TAG, "stopRecordAndFile");
                stt();
                break;
        }
        return false;
    }


    public void stt() {

        File voiceFile = new File(AudioFileUtils.getWavFilePath());
        if (!voiceFile.exists()) {
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), voiceFile);
        MultipartBody.Part file =
                MultipartBody.Part.createFormData("file", voiceFile.getName(), requestBody);


        NetRequest.sAPIClient.stt(RequestBodyUtil.getParams(), file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<STT>() {
                    @Override
                    public void call(STT result) {
                        if (result != null && result.getCount() > 0) {
                            sttTv.setText("结果: " + result.getSegments().get(0).getContent());
                        }

                    }
                });
    }
}