package com.andreyrk.iptv.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andreyrk.iptv.R;

import java.util.List;

public class IPTVPlaylistAdapter extends RecyclerView.Adapter<IPTVPlaylistAdapter.ViewHolder> {

    private Context context;
    private List<IPTVPlaylist> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    public IPTVPlaylistAdapter(Context context, List<IPTVPlaylist> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_playlist, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IPTVPlaylist data = getItem(position);

        holder.name.setText(data.name);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.card_name);

            itemView.setOnClickListener(this);
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

    public void setData(List<IPTVPlaylist> data) {
        this.mData = data;
        this.notifyDataSetChanged();
    }

    public IPTVPlaylist getItem(int id) {
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