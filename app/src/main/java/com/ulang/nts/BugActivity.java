package com.ulang.nts;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



public class BugActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug);
        TextView textView = (TextView) findViewById(R.id.bugTextView);
        textView.setText(getIntent().getStringExtra("stackTrace"));
    }

}
