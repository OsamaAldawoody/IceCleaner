package com.phonecleaner.icecleaner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.AdmobHelp;



public class SplashActivity extends AppCompatActivity {
    Handler mHandler;
    Runnable r ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AdmobHelp.getInstance().init(this);

        mHandler =new Handler();
        r =  new Runnable() {
            @Override
            public void run() {

                Intent localIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(localIntent);
                finish();

            }
        };

        mHandler.postDelayed(r, 4000);

    }
    @Override
    protected void onDestroy() {
        if(mHandler!=null&&r!=null)
            mHandler.removeCallbacks(r);
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {

    }
}
