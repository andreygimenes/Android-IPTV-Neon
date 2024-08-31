package com.andreyrk.iptv.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andreyrk.iptv.adapters.IPTVPlaylist;
import com.andreyrk.iptv.adapters.IPTVPlaylistAdapter;
import com.andreyrk.iptv.MainActivity;
import com.andreyrk.iptv.R;

public class PlaylistFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        final MainActivity activity = ((MainActivity) getActivity());
        activity.getSupportActionBar().setTitle(R.string.activity_name_playlist);

        final IPTVPlaylistAdapter adapter = new IPTVPlaylistAdapter(activity, activity.playlists);
        adapter.setClickListener(new IPTVPlaylistAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final IPTVPlaylist playlist = adapter.getItem(position);

                PopupMenu popupMenu = new PopupMenu(activity, view.findViewById(R.id.card_options));
                activity.getMenuInflater().inflate(R.menu.card_playlist, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_select:
                                activity.setPlaylist(playlist);

                                Toast.makeText(activity, R.string.toast_playlist_selected, Toast.LENGTH_SHORT);

                                break;
                            case R.id.menu_edit:
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setTitle(R.string.dialog_title_edit_playlist);

                                LayoutInflater inflater = activity.getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.dialog_playlist, null);

                                TextView url = dialogView.findViewById(R.id.playlist_input_url);
                                url.setText(playlist.url);

                                TextView name = dialogView.findViewById(R.id.playlist_input_name);
                                name.setText(playlist.name);

                                builder.setView(dialogView);

                                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AlertDialog alertDialog = ((AlertDialog) dialog);

                                        String url = ((TextView)alertDialog.findViewById(R.id.playlist_input_url)).getText().toString();
                                        String name = ((TextView)alertDialog.findViewById(R.id.playlist_input_name)).getText().toString();

                                        if (url.isEmpty()) {
                                            Toast.makeText(activity, R.string.toast_url_cannot_be_empty, Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (name.isEmpty()) {
                                                name = url;
                                            }

                                            playlist.name = name;
                                            playlist.url = url;

                                            activity.savePlaylists();

                                            adapter.notifyDataSetChanged();
                                        }

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

                                break;
                            case R.id.menu_remove:
                                if (activity.playlist.id == playlist.id) {
                                    activity.setPlaylist(new IPTVPlaylist("", ""));
                                }

                                activity.removePlaylist(playlist);
                                adapter.notifyDataSetChanged();
                                break;
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.dialog_add_playlist);

                builder.setView(R.layout.dialog_playlist);

                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog alertDialog = ((AlertDialog) dialog);

                        String url = ((TextView)alertDialog.findViewById(R.id.playlist_input_url)).getText().toString();
                        String name = ((TextView)alertDialog.findViewById(R.id.playlist_input_name)).getText().toString();

                        if (url.isEmpty()) {
                            Toast.makeText(activity, R.string.toast_url_cannot_be_empty, Toast.LENGTH_SHORT).show();
                        } else {
                            if (name.isEmpty()) {
                                name = url;
                            }

                            activity.addPlaylist(new IPTVPlaylist(name, url));
                            adapter.notifyDataSetChanged();
                        }

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
            }
        });

        return view;
    }
}