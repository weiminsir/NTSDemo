package com.ulang.nts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.stt_btn_local, R.id.tts_btn, R.id.nlp_btn,R.id.stt_btn_web_socket})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stt_btn_local:
                startActivity(new Intent(this, STTLocalFileActivity.class));
                break;
            case R.id.stt_btn_web_socket:
                startActivity(new Intent(this, STTWebSocketActivity.class));
                break;
            case R.id.tts_btn:
                startActivity(new Intent(this, TTSActivity.class));
                break;
            case R.id.nlp_btn:
                startActivity(new Intent(this, NLPActivity.class));
                break;
        }
    }
}
