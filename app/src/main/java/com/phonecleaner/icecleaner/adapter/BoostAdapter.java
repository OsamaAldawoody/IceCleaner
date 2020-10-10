package com.phonecleaner.icecleaner.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.model.TaskInfo;
import com.phonecleaner.icecleaner.utils.Utils;


public class BoostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgAppBoost;
        private TextView tvNameAppBoost;
        private TextView tvSizeAppBoost;
        private CheckBox checkBoxBoost;

        ViewHolder(View itemView) {
            super(itemView);
            imgAppBoost = (ImageView) itemView.findViewById(R.id.imgIconApBoost);
            tvNameAppBoost = (TextView) itemView.findViewById(R.id.tvNameBoost);
            tvSizeAppBoost = (TextView) itemView.findViewById(R.id.tvSizeBoost);
            checkBoxBoost = (CheckBox) itemView.findViewById(R.id.checkBoxBoost);
        }
    }

    private List<TaskInfo> mTaskInfos;
    private PackageManager mPackageManager;
    private OnHandleItemBoostClickListener mOnHandleItemClickListener;

    public BoostAdapter(Context context, List<TaskInfo> taskInfos,
                        OnHandleItemBoostClickListener onHandleItemClickListener) {
        this.mTaskInfos = taskInfos;
        mPackageManager = context.getPackageManager();
        mOnHandleItemClickListener = onHandleItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_boost, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            TaskInfo taskInfo = mTaskInfos.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.imgAppBoost.setImageDrawable(taskInfo.getAppinfo().loadIcon(mPackageManager));
            viewHolder.tvNameAppBoost.setText(taskInfo.getAppinfo().loadLabel(mPackageManager));
            viewHolder.tvSizeAppBoost.setText(Utils.formatSize(taskInfo.getMem()));
            viewHolder.checkBoxBoost.setChecked(taskInfo.isChceked());
            viewHolder.checkBoxBoost.setOnClickListener(new View.OnClickListener() {
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
        return mTaskInfos.size();
    }

    public interface OnHandleItemBoostClickListener {
        void onSelectedItem(int position);
    }
}