package com.ulang.nts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ulang.nts.model.NLP;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * input a sentence  and back primary word,
 * such as input "我要打电话",output ""打电话
 * */
public class NLPActivity extends AppCompatActivity {

    @BindView(R.id.nlp_content)
    EditText nlpContent;
    @BindView(R.id.btn_nlp)
    Button btnNlp;
    @BindView(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nlp);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_nlp)
    public void onViewClicked() {

        String content = nlpContent.getText().toString().trim();
        NetRequest.sAPIClient.nlp(content, "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NLP>() {
                    @Override
                    public void call(NLP nlp) {

                        StringBuilder builder = new StringBuilder("NLP结果:");

                        for (String str : nlp.getDialog().getFinalSluResult()) {
                            builder.append(str);
                        }
                        tvResult.setText(builder.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("NLPService", "throwable=" + throwable.getMessage());
                    }
                });


    }
}
