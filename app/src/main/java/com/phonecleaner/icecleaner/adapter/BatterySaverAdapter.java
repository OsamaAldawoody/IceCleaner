package com.phonecleaner.icecleaner.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.model.BatterySaver;


public class BatterySaverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout viewSaver;
        private LinearLayout viewDescriptionBattery;

        private RadioButton radioButtonBattery;

        private TextView tvTitleBattery;
        private TextView tvTimeOffScreen;

        private ImageView imgArrowBattery;
        private ImageView imgBrightness;
        private ImageView imgWifi;
        private ImageView imgBluetooth;
        private ImageView imgData;
        private ImageView imgRotate;
        private ImageView imgVibration;


        ViewHolder(View itemView) {
            super(itemView);
            viewSaver = (LinearLayout) itemView.findViewById(R.id.viewSaver);
            viewDescriptionBattery = (LinearLayout) itemView.findViewById(R.id.viewDescriptionBattery);
            radioButtonBattery = (RadioButton) itemView.findViewById(R.id.radioButtonBattery);
            tvTitleBattery = (TextView) itemView.findViewById(R.id.tvTitleBattery);
            tvTimeOffScreen = (TextView) itemView.findViewById(R.id.tvTimeOffScreen);
            imgArrowBattery = (ImageView) itemView.findViewById(R.id.imgArrowBattery);
            imgBrightness = (ImageView) itemView.findViewById(R.id.imgBrightness);
            imgWifi = (ImageView) itemView.findViewById(R.id.imgWifi);
            imgBluetooth = (ImageView) itemView.findViewById(R.id.imgBluetooth);
            imgData = (ImageView) itemView.findViewById(R.id.imgData);
            imgRotate = (ImageView) itemView.findViewById(R.id.imgRotate);
            imgVibration = (ImageView) itemView.findViewById(R.id.imgVibration);
        }
    }

    private List<BatterySaver> mBatterySavers;
    private Context mContext;
    private OnHandleItemClickListener mOnHandleItemClickListener;

    public BatterySaverAdapter(Context context, List<BatterySaver> batterySavers,
                               OnHandleItemClickListener onHandleItemClickListener) {
        mContext = context;
        this.mBatterySavers = batterySavers;
        mOnHandleItemClickListener = onHandleItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_battery, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            BatterySaver batterySaver = mBatterySavers.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.radioButtonBattery.setChecked(batterySaver.isSelected());
            viewHolder.tvTitleBattery.setText(batterySaver.getTitle());
            setTextScreenTimeOut(mContext, viewHolder.tvTimeOffScreen,
                    batterySaver.getLenghtScreenTimeOut());

            //expand
            if (batterySaver.isExpand()) {
                viewHolder.imgArrowBattery.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_500_36dp);
                viewHolder.viewDescriptionBattery.setVisibility(View.GONE);
            } else {
                viewHolder.imgArrowBattery.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_500_36dp);
                viewHolder.viewDescriptionBattery.setVisibility(View.VISIBLE);
            }
            // brightness
            if (batterySaver.isAutoScreenBrightness()) {
                viewHolder.imgBrightness.setImageResource(R.drawable.ic_brightness_auto_teal_500_18dp);
            } else {
                if (batterySaver.getLenghtScreenBrightness() != 0) {
                    viewHolder.imgBrightness.setImageResource(R.drawable.ic_brightness_medium_teal_500_18dp);
                } else {
                    viewHolder.imgBrightness.setImageResource(R.drawable.ic_brightness_low_grey_400_18dp);
                }
            }
            // wifi
            if (batterySaver.isWifi()) {
                viewHolder.imgWifi.setImageResource(R.drawable.ic_wifi_teal_500_18dp);
            } else {
                viewHolder.imgWifi.setImageResource(R.drawable.ic_wifi_grey_400_18dp);
            }
            // Bluetooth
            if (batterySaver.isBluetooth()) {
                viewHolder.imgBluetooth.setImageResource(R.drawable.ic_bluetooth_teal_500_18dp);
            } else {
                viewHolder.imgBluetooth.setImageResource(R.drawable.ic_bluetooth_grey_400_18dp);
            }
            // Data
            if (batterySaver.isData()) {
                viewHolder.imgData.setImageResource(R.drawable.ic_swap_vert_teal_500_18dp);
            } else {
                viewHolder.imgData.setImageResource(R.drawable.ic_swap_vert_grey_400_18dp);
            }
            // rotate
            if (batterySaver.isAutoSync()) {
                viewHolder.imgRotate.setImageResource(R.drawable.ic_autorenew_teal_500_18dp);
            } else {
                viewHolder.imgRotate.setImageResource(R.drawable.ic_autorenew_grey_400_18dp);
            }
            // vibration
            if (batterySaver.isVibration()) {
                viewHolder.imgVibration.setImageResource(R.drawable.ic_vibration_teal_500_18dp);
            } else {
                viewHolder.imgVibration.setImageResource(R.drawable.ic_vibration_grey_400_18dp);
            }

            viewHolder.viewSaver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnHandleItemClickListener.onClickItemSaver(position);
                }
            });
            viewHolder.viewDescriptionBattery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnHandleItemClickListener.onClickItemDescription(position);
                }
            });
            viewHolder.radioButtonBattery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnHandleItemClickListener.onSelectedItem(position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mBatterySavers.size();
    }

    private void setTextScreenTimeOut(Context context, TextView textView, int time) {
        if (time < 60000) {
            textView.setText(String.format(context.getString(R.string.lenght_screen_timout_s), time / 1000));
        } else {
            textView.setText(String.format(context.getString(R.string.lenght_screen_timout_m), time / (1000 * 60)));
        }
    }

    public interface OnHandleItemClickListener {
        void onClickItemSaver(int position);

        void onClickItemDescription(int position);

        void onSelectedItem(int position);
    }
}