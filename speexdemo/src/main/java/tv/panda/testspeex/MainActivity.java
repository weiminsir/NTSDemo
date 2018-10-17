package tv.panda.testspeex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    Button mPlayBtn;

    Button mRecordBtn;

    PlayerUtils mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    void  initView(){
        mPlayBtn = (Button) findViewById(R.id.play_btn);
        mRecordBtn = (Button) findViewById(R.id.record_btn);
        mPlayBtn.setOnClickListener(this);
        mRecordBtn.setOnTouchListener(this);
        mPlayer = new PlayerUtils();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_btn:
                try{
                    mPlayer.setUrlPrepare(AudioFileUtils.getDecodeWavFilePath());
                    mPlayer.play();
                }catch (Exception e){

                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AudioRecordUtils utils = AudioRecordUtils.getInstance();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                utils.startRecordAndFile();
                break;
            case  MotionEvent.ACTION_UP:
                utils.stopRecordAndFile();
                break;
        }
        return false;
    }
}
