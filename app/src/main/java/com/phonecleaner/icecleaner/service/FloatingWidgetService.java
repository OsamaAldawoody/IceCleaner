package com.phonecleaner.icecleaner.service;


import android.app.Service;

import android.content.Intent;

import android.graphics.PixelFormat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;


import com.phonecleaner.icecleaner.R;


public class FloatingWidgetService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingWidget;
    View videoLayout;
    View rootContainer;
    VideoView rock_video;

    int FLOATING_FLAG;

    public FloatingWidgetService() {
        Log.v("FloatingServiceWidget", "floating service started");
    }






    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();




        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            FLOATING_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            FLOATING_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                FLOATING_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidget, params);
        final View collapsedView = mFloatingWidget.findViewById(R.id.collapse_view);
        final View expandedView = mFloatingWidget.findViewById(R.id.expanded_view);
        ImageView closeButtonCollapsed = (ImageView) mFloatingWidget.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        ImageView closeButton = (ImageView) mFloatingWidget.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });


        rootContainer = mFloatingWidget.findViewById(R.id.root_container);
        videoLayout = mFloatingWidget.findViewById(R.id.rocket_video_layout);


        rootContainer.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingWidget, params);
                        return true;
                }
                return false;
            }
        });


        rock_video = mFloatingWidget.findViewById(R.id.rock_video);
        rock_video.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.rock_2));
        rock_video.start();
        rock_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoLayout.setVisibility(View.GONE);
            }
        });


    }


    private boolean isViewCollapsed() {
        return mFloatingWidget == null || mFloatingWidget.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidget != null) mWindowManager.removeView(mFloatingWidget);
        Log.v("FloatingWidgetService", "destroyed");

    }
}