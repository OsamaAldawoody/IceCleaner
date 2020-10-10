package com.phonecleaner.icecleaner.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.phonecleaner.icecleaner.R;
import com.phonecleaner.icecleaner.model.Whitelist;


public class WhiteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView btnRemove;
        private ImageView imgIconApp;


        ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            btnRemove = (TextView) itemView.findViewById(R.id.btnRemove);
            imgIconApp = (ImageView) itemView.findViewById(R.id.imgIconApp);
        }
    }

    private List<Whitelist> mWhitelists;
    private OnHandleItemClickListener mOnHandleItemClickListener;

    public WhiteListAdapter(List<Whitelist> whitelists,
                            OnHandleItemClickListener onHandleItemClickListener) {
        mWhitelists = whitelists;
        mOnHandleItemClickListener = onHandleItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_whitelist, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            Whitelist whitelist = mWhitelists.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.tvName.setText(whitelist.getApplicationName());
            viewHolder.imgIconApp.setImageDrawable(whitelist.getIcon());

            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnHandleItemClickListener.onClickRemove(position);
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
        return mWhitelists.size();
    }

    public interface OnHandleItemClickListener {
        void onClickRemove(int position);
    }
}