package com.andreyrk.iptv.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreyrk.iptv.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class IPTVChannelAdapter extends RecyclerView.Adapter<IPTVChannelAdapter.ViewHolder> {

    private Context context;
    private List<IPTVChannel> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    public IPTVChannelAdapter(Context context, List<IPTVChannel> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_list_channel, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IPTVChannel data = getItem(position);

        Glide.with(context).load(data.logo).into(holder.image);
        holder.title.setText(data.title);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView image;
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.card_image);
            title = itemView.findViewById(R.id.card_name);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) return mLongClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }
    }

    public void setData(List<IPTVChannel> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    public IPTVChannel getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }
}