package com.andreyrk.iptv.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andreyrk.iptv.adapters.IPTVChannel;
import com.andreyrk.iptv.adapters.IPTVChannelAdapter;
import com.andreyrk.iptv.MainActivity;
import com.andreyrk.iptv.R;

public class RecentChannelsFragment extends Fragment {
    IPTVChannelAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_channels, container, false);
        setHasOptionsMenu(true);

        final MainActivity activity = ((MainActivity) getActivity());
        activity.getSupportActionBar().setTitle(R.string.activity_name_recent);

        adapter = new IPTVChannelAdapter(activity, activity.recents);
        adapter.setClickListener(new IPTVChannelAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                IPTVChannel channel = adapter.getItem(position);

                activity.addRecent(channel);
                activity.play(channel);
            }
        });
        adapter.setLongClickListener(new IPTVChannelAdapter.ItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                final IPTVChannel channel = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.dialog_remove_recent);
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.removeRecent(channel);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(activity, R.string.toast_recent_removed, Toast.LENGTH_SHORT).show();

                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return false;
            }
        });
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_recent_channels, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_remove_all:
                MainActivity activity = (MainActivity) getActivity();

                activity.recents.clear();
                activity.saveRecents();

                adapter.notifyDataSetChanged();

                return true;
        }

        return false;
    }
}