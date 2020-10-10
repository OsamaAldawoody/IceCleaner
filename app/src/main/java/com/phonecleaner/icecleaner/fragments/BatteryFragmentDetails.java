package com.phonecleaner.icecleaner.fragments;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.phonecleaner.icecleaner.MainActivity;
import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.model.BatterySaver;
import com.phonecleaner.icecleaner.utils.KeyboardUtil;
import com.phonecleaner.icecleaner.utils.Utils;

public class BatteryFragmentDetails extends BaseFragment implements View.OnClickListener {

    private static final String KEY_BATTERY_SAVER = "battery_saver";
    private static final String KEY_TYPE_BATTERY_SAVER = "type_battery_saver";
    private static final String KEY_POSITION = "position";

    private LinearLayout mViewNameBatterySaver;
    private LinearLayout mViewButton;
    private LinearLayout mViewScreenBrightnessDescription;
    private LinearLayout mViewScreenTimeout;
    private LinearLayout mViewWifi;
    private LinearLayout mViewBluetooth;
    private LinearLayout mViewData;
    private LinearLayout mViewAutoSync;
    private LinearLayout mViewVibrate;

    private EditText mEdName;

    private TextView mTvScreenBrightnessIndex;
    private TextView mTvScreenTimeoutIndex;
    private TextView mTvPleaseInputName;

    private SwitchCompat mSwitchCompatWifi;
    private SwitchCompat mSwitchCompatBluetooth;
    private SwitchCompat mSwitchCompatData;
    private SwitchCompat mSwitchCompatAutoSync;
    private SwitchCompat mSwitchCompatVibrate;

    private SeekBar mSeekBarScreenBrightness;
    private CheckBox mCheckBoxScreenBrightness;

    private TextView[] mTextViews;

    private BatterySaver mBatterySaver;
    private int mPosition;
    private int mTypeBatterySaver;
    private ArrayList<BatterySaver> mBatterySavers = new ArrayList<>();

    private int mLengthScreenTimeOut;

    public static BatteryFragmentDetails newInstance(int position, int type,
                                                     ArrayList<BatterySaver> batterySavers) {
        BatteryFragmentDetails f = new BatteryFragmentDetails();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_BATTERY_SAVER, batterySavers);
        bundle.putInt(KEY_POSITION, position);
        bundle.putInt(KEY_TYPE_BATTERY_SAVER, type);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mPosition = getArguments().getInt(KEY_POSITION);
            mTypeBatterySaver = getArguments().getInt(KEY_TYPE_BATTERY_SAVER);
            mBatterySavers = getArguments().getParcelableArrayList(KEY_BATTERY_SAVER);
            if (mBatterySavers != null) {
                mBatterySaver = mBatterySavers.get(mPosition);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_battery_detail, container, false);
        KeyboardUtil.setupUI(view, getActivity());
        mViewNameBatterySaver = (LinearLayout) view.findViewById(R.id.viewNameBatterySaver);
        mViewButton = (LinearLayout) view.findViewById(R.id.viewButton);
        LinearLayout mViewScreenBrightness = (LinearLayout) view.findViewById(R.id.viewScreenBrightness);
        mViewScreenTimeout = (LinearLayout) view.findViewById(R.id.viewScreenTimeout);
        mViewScreenBrightnessDescription = (LinearLayout) view.findViewById(R.id.viewScreenBrightnessDescription);
        mViewWifi = (LinearLayout) view.findViewById(R.id.viewWifi);
        mViewBluetooth = (LinearLayout) view.findViewById(R.id.viewBluetooth);
        mViewData = (LinearLayout) view.findViewById(R.id.viewData);
        mViewAutoSync = (LinearLayout) view.findViewById(R.id.viewAutoSync);
        mViewVibrate = (LinearLayout) view.findViewById(R.id.viewVibrate);

        mEdName = (EditText) view.findViewById(R.id.edName);

        TextView mTvScreenBrightness = (TextView) view.findViewById(R.id.tvScreenBrightness);
        mTvScreenBrightnessIndex = (TextView) view.findViewById(R.id.tvScreenBrightnessIndex);
        TextView mTvScreenTimeout = (TextView) view.findViewById(R.id.tvScreenTimeout);
        mTvScreenTimeoutIndex = (TextView) view.findViewById(R.id.tvScreenTimeoutIndex);
        TextView mTvWifi = (TextView) view.findViewById(R.id.tvWifi);
        TextView mTvBluetooth = (TextView) view.findViewById(R.id.tvBluetooth);
        TextView mTvData = (TextView) view.findViewById(R.id.tvData);
        TextView mTvAutoSync = (TextView) view.findViewById(R.id.tvAutoSync);
        TextView mTvVibrate = (TextView) view.findViewById(R.id.tvVibrate);
        mTvPleaseInputName = (TextView) view.findViewById(R.id.tvPleaseInputName);

        mTextViews = new TextView[]{mTvScreenBrightness, mTvScreenBrightnessIndex, mTvScreenTimeout,
                mTvScreenTimeoutIndex, mTvWifi, mTvBluetooth, mTvData, mTvAutoSync, mTvVibrate};

        mSwitchCompatWifi = (SwitchCompat) view.findViewById(R.id.switchCompatWifi);
        mSwitchCompatBluetooth = (SwitchCompat) view.findViewById(R.id.switchCompatBluetooth);
        mSwitchCompatData = (SwitchCompat) view.findViewById(R.id.switchCompatData);
        mSwitchCompatAutoSync = (SwitchCompat) view.findViewById(R.id.switchCompatAutoSync);
        mSwitchCompatVibrate = (SwitchCompat) view.findViewById(R.id.switchCompatVibrate);

        mSeekBarScreenBrightness = (SeekBar) view.findViewById(R.id.seekBarScreenBrightness);
        mCheckBoxScreenBrightness = (CheckBox) view.findViewById(R.id.checkBoxScreenBrightness);

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnSave = (Button) view.findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        mViewScreenBrightness.setOnClickListener(this);
        mViewScreenTimeout.setOnClickListener(this);
        mViewWifi.setOnClickListener(this);
        mViewBluetooth.setOnClickListener(this);
        mViewData.setOnClickListener(this);
        mViewAutoSync.setOnClickListener(this);
        mViewVibrate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null == mBatterySaver) {
            return;
        }
        mLengthScreenTimeOut = mBatterySaver.getLenghtScreenTimeOut();
        setValueSwitch(mTypeBatterySaver);
        switch (mTypeBatterySaver) {
            case BatterySaver.TYPE_SUPPER_SAVING:
                mViewNameBatterySaver.setVisibility(View.GONE);
                mViewButton.setVisibility(View.GONE);
                setColorText(ContextCompat.getColor(getActivity(), R.color.grey_500));
                mViewScreenTimeout.setClickable(false);
                break;
            case BatterySaver.TYPE_NORMAL:
                mViewNameBatterySaver.setVisibility(View.GONE);
                mViewButton.setVisibility(View.GONE);
                setColorText(ContextCompat.getColor(getActivity(), R.color.grey_500));
                mViewScreenTimeout.setClickable(false);
                break;
            case BatterySaver.TYPE_ADD:
            default:
                KeyboardUtil.showKeyboard(getActivity(), mEdName);
                mViewNameBatterySaver.setVisibility(View.VISIBLE);
                mViewButton.setVisibility(View.VISIBLE);
                setColorText(ContextCompat.getColor(getActivity(), R.color.black));
                mViewScreenTimeout.setClickable(true);
                break;
        }
        if (mTypeBatterySaver == BatterySaver.TYPE_ADD) {
            mEdName.setHint(getString(R.string.enter_name));
            mEdName.setText("");
        } else {
            mEdName.setText(mBatterySaver.getTitle());
        }

        if (mBatterySaver.isAutoScreenBrightness()) {
            mTvScreenBrightnessIndex.setText(getString(R.string.auto));
            mSeekBarScreenBrightness.setEnabled(false);
        } else {
            mSeekBarScreenBrightness.setEnabled(true);
            mTvScreenBrightnessIndex.setText(String.format(getString(R.string.percent),
                    mBatterySaver.getLenghtScreenBrightness()));
        }
        Utils.setTextScreenTimeOut(getActivity(),
                mTvScreenTimeoutIndex, mBatterySaver.getLenghtScreenTimeOut());

        mSeekBarScreenBrightness.setProgress(mBatterySaver.getLenghtScreenBrightness());
        mCheckBoxScreenBrightness.setChecked(mBatterySaver.isAutoScreenBrightness());
        mCheckBoxScreenBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSeekBarScreenBrightness.setEnabled(false);
                    mTvScreenBrightnessIndex.setText(getString(R.string.auto));
                } else {
                    mSeekBarScreenBrightness.setEnabled(true);
                    mTvScreenBrightnessIndex.setText(String.format(getString(R.string.percent),
                            mSeekBarScreenBrightness.getProgress()));
                }
            }
        });
        mSeekBarScreenBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvScreenBrightnessIndex.setText(String.format(getString(R.string.percent),
                        progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //no op
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //no op
            }
        });
    }

    private void setColorText(int color) {
        for (TextView textView : mTextViews) {
            textView.setTextColor(color);
        }
    }

    private void setValueSwitch(int type) {
        mSwitchCompatWifi.setChecked(mBatterySaver.isWifi());
        mSwitchCompatBluetooth.setChecked(mBatterySaver.isBluetooth());
        mSwitchCompatData.setChecked(mBatterySaver.isData());
        mSwitchCompatAutoSync.setChecked(mBatterySaver.isData());
        mSwitchCompatVibrate.setChecked(mBatterySaver.isVibration());
        switch (type) {
            case BatterySaver.TYPE_SUPPER_SAVING:
            case BatterySaver.TYPE_NORMAL:
                mViewWifi.setClickable(false);
                mViewBluetooth.setClickable(false);
                mViewData.setClickable(false);
                mViewAutoSync.setClickable(false);
                mViewVibrate.setClickable(false);

                mSeekBarScreenBrightness.setEnabled(false);
                mCheckBoxScreenBrightness.setClickable(false);
                break;
            default:
                mViewWifi.setClickable(true);
                mViewBluetooth.setClickable(true);
                mViewData.setClickable(true);
                mViewAutoSync.setClickable(true);
                mViewVibrate.setClickable(true);

                mSeekBarScreenBrightness.setEnabled(true);
                mCheckBoxScreenBrightness.setClickable(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewScreenBrightness:
                if (mViewScreenBrightnessDescription.getVisibility() == View.VISIBLE) {
                    mViewScreenBrightnessDescription.setVisibility(View.GONE);
                } else {
                    mViewScreenBrightnessDescription.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
            case R.id.btnSave:
                if (mEdName.getText().toString().equals("")) {
                    mTvPleaseInputName.setVisibility(View.VISIBLE);
                    return;
                }
                if (mTypeBatterySaver == BatterySaver.TYPE_CUSTOM
                        || mTypeBatterySaver == BatterySaver.TYPE_NEW) {
                    mPreferenceUtil.removeAll(getActivity());

                    mBatterySaver.setLenghtScreenTimeOut(mLengthScreenTimeOut);
                    mBatterySaver.setLenghtScreenBrightness(mSeekBarScreenBrightness.getProgress());
                    mBatterySaver.setAutoScreenBrightness(mCheckBoxScreenBrightness.isChecked());
                    mBatterySaver.setExpand(true);
                    mBatterySaver.setTitle(mEdName.getText().toString());
                    mBatterySaver.setWifi(mSwitchCompatWifi.isChecked());
                    mBatterySaver.setBluetooth(mSwitchCompatBluetooth.isChecked());
                    mBatterySaver.setData(mSwitchCompatData.isChecked());
                    mBatterySaver.setAutoSync(mSwitchCompatAutoSync.isChecked());
                    mBatterySaver.setVibration(mSwitchCompatVibrate.isChecked());

                    mBatterySavers.remove(mPosition);
                    mBatterySavers.add(mPosition, mBatterySaver);
                } else {
                    BatterySaver batterySaverAdd = new BatterySaver();
                    batterySaverAdd.setLenghtScreenTimeOut(mLengthScreenTimeOut);
                    batterySaverAdd.setLenghtScreenBrightness(mSeekBarScreenBrightness.getProgress());
                    batterySaverAdd.setAutoScreenBrightness(mCheckBoxScreenBrightness.isChecked());
                    batterySaverAdd.setExpand(true);
                    batterySaverAdd.setTitle(mEdName.getText().toString());
                    batterySaverAdd.setWifi(mSwitchCompatWifi.isChecked());
                    batterySaverAdd.setBluetooth(mSwitchCompatBluetooth.isChecked());
                    batterySaverAdd.setData(mSwitchCompatData.isChecked());
                    batterySaverAdd.setAutoSync(mSwitchCompatAutoSync.isChecked());
                    batterySaverAdd.setVibration(mSwitchCompatVibrate.isChecked());
                    batterySaverAdd.setType(BatterySaver.TYPE_NEW);
                    mBatterySavers.add(batterySaverAdd);
                }
                mPreferenceUtil.saveBatterySaver(getActivity(), mBatterySavers);
                onBackPressed();
                break;
            case R.id.viewWifi:
                mSwitchCompatWifi.performClick();
                break;
            case R.id.viewBluetooth:
                mSwitchCompatBluetooth.performClick();
                break;
            case R.id.viewData:
                mSwitchCompatData.performClick();
                break;
            case R.id.viewAutoSync:
                mSwitchCompatAutoSync.performClick();
                break;
            case R.id.viewVibrate:
                mSwitchCompatVibrate.performClick();
                break;
            case R.id.viewScreenTimeout:
                selectScreenTimeOut();
                break;
            default:
                break;
        }
    }

    private void selectScreenTimeOut() {
        final CharSequence[] options = {getString(R.string.seconds_15), getString(R.string.seconds_30),
                getString(R.string.minute_1), getString(R.string.minute_2), getString(R.string.minute_5),
                getString(R.string.minute_10)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.screen_timeout));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Utils.setTextScreenTimeOut(getActivity(),
                        mTvScreenTimeoutIndex, BatterySaver.LENGTH_SCREEN_TIMEOUT[item]);
                mLengthScreenTimeOut = BatterySaver.LENGTH_SCREEN_TIMEOUT[item];
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog;
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTypeBatterySaver == BatterySaver.TYPE_ADD) {
            setHeader(getString(R.string.add_mode), MainActivity.HeaderBarType.TYPE_CLEAN);
        } else {
            setHeader(mBatterySaver.getTitle(), MainActivity.HeaderBarType.TYPE_CLEAN);
        }
    }
}
