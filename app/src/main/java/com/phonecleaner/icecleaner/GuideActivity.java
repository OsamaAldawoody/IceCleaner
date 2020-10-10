package com.phonecleaner.icecleaner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class GuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_access);
        findViewById(R.id.na_guide_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
