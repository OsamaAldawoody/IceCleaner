package com.phonecleaner.icecleaner.service;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.asyncTask.TaskMemoryBoost;
import com.phonecleaner.icecleaner.utils.Utils;

public class ChatHeadService extends Service {
    private static final String EXTRA_MSG = "extra_msg";

    private WindowManager windowManager;
    private RelativeLayout chatheadView;
    private int x_init_cord;
    private int y_init_cord;
    private int x_init_margin;
    private int y_init_margin;
    private Point szWindow = new Point();
    private String sMsg = "";
    private RelativeLayout mViewBoostChatHead;
    private ImageView mImgProgressChatHead;
    private TextView mTvProgressChatHead;

    private Animation mAnimationRotate;

    private boolean isShowToast;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        isShowToast = false;
        mAnimationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate_image);
        mAnimationRotate.setRepeatCount(Animation.INFINITE);
    }

    private void handleStart() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        paramRemove.gravity = Gravity.TOP | Gravity.START;

        chatheadView = (RelativeLayout) View.inflate(getApplicationContext(), R.layout.chathead, null);
        mViewBoostChatHead = (RelativeLayout) chatheadView.findViewById(R.id.viewBoostChatHead);
        ImageView mImgBoostChatHead = (ImageView) chatheadView.findViewById(R.id.imgBoostChatHead);
        mImgProgressChatHead = (ImageView) chatheadView.findViewById(R.id.imgChatheadProgress);
        mTvProgressChatHead = (TextView) chatheadView.findViewById(R.id.tvProgressChatHead);
        mImgBoostChatHead.startAnimation(mAnimationRotate);
        mImgProgressChatHead.setVisibility(View.VISIBLE);
        mTvProgressChatHead.setVisibility(View.VISIBLE);
        mViewBoostChatHead.setVisibility(View.GONE);
        boost(getApplicationContext(), false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;
        windowManager.addView(chatheadView, params);

        chatheadView.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    isLongclick = true;
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams)
                        chatheadView.getLayoutParams();

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 600);
                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if (isLongclick) {
                            int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                                inBounded = true;


                                windowManager.updateViewLayout(chatheadView, layoutParams);
                                break;
                            } else {
                                inBounded = false;
                            }

                        }


                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(chatheadView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        isLongclick = false;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if (inBounded) {
                            stopService(new Intent(ChatHeadService.this, ChatHeadService.class));
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300) {
                                chatheadClick();
                            }
                        }


                        x_cord_Destination = x_init_margin + x_diff;
                        y_cord_Destination = y_init_margin + y_diff;

                        int x_start;
                        x_start = x_cord_Destination;


                        int BarHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight);
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        resetPosition(x_start);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (layoutParams.y + (chatheadView.getHeight() + getStatusBarHeight()) > szWindow.y) {
                layoutParams.y = szWindow.y - (chatheadView.getHeight() + getStatusBarHeight());
                windowManager.updateViewLayout(chatheadView, layoutParams);
            }

            if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                resetPosition(szWindow.x);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (layoutParams.x > szWindow.x) {
                resetPosition(szWindow.x);
            }
        }
    }

    private void resetPosition(int x_cord_now) {
        int w = chatheadView.getWidth();
        if (x_cord_now + w / 2 <= szWindow.x / 2) {
            moveToLeft(x_cord_now);

        } else if (x_cord_now + w / 2 > szWindow.x / 2) {
            moveToRight(x_cord_now);
        }
    }

    private void moveToLeft(int x_cord_now) {
        final int x = x_cord_now;
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = (int) (double) bounceValue(step, x);
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                windowManager.updateViewLayout(chatheadView, mParams);
            }
        }.start();
    }

    private void moveToRight(int x_cord_now) {
        final int x = x_cord_now;
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = szWindow.x + (int) (double) bounceValue(step, x) - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - chatheadView.getWidth();
                windowManager.updateViewLayout(chatheadView, mParams);
            }
        }.start();
    }

    private double bounceValue(long step, long scale) {
        return scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private void chatheadClick() {
        mImgProgressChatHead.setVisibility(View.GONE);
        mTvProgressChatHead.setVisibility(View.GONE);
        mViewBoostChatHead.setVisibility(View.VISIBLE);
        boost(getApplicationContext(), true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent != null) {
            Bundle bd = intent.getExtras();
            if (bd != null)
                sMsg = bd.getString(EXTRA_MSG);
            if (sMsg != null && sMsg.length() > 0) {
                if (startId == Service.START_STICKY) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                        }
                    }, 300);
                }
            }
        }
        handleStart();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (chatheadView != null) {
            windowManager.removeView(chatheadView);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void boost(final Context context, boolean isDelay) {
        new TaskMemoryBoost(context, isDelay, new TaskMemoryBoost.OnTaskMemoryBoostListener() {
            @Override
            public void onMemoryBoostSuccess(boolean result, long freeRam) {
                mImgProgressChatHead.setVisibility(View.VISIBLE);
                mTvProgressChatHead.setVisibility(View.VISIBLE);
                mViewBoostChatHead.setVisibility(View.GONE);
                ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                ((ActivityManager) context.getSystemService(
                        Context.ACTIVITY_SERVICE)).getMemoryInfo(memInfo);
                final long availMem = memInfo.availMem;
                float totalRam = Utils.getTotalRam();
                int percentRam = (int) (((double) (totalRam - availMem) / (double) totalRam) * 100);
                mTvProgressChatHead.setText(String.valueOf(percentRam));
                if (isShowToast) {
                    if (result) {
                        Toast.makeText(context, String.format(context.getString(R.string.free_ram),
                                Utils.formatSize(freeRam)), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.device_has_been_boosted),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    isShowToast = true;
                }
            }
        }).execute();
    }
}
