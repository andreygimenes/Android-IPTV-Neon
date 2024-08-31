package com.andreyrk.iptv.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andreyrk.iptv.adapters.IPTVChannel;
import com.andreyrk.iptv.adapters.IPTVChannelAdapter;
import com.andreyrk.iptv.adapters.IPTVPlaylist;
import com.andreyrk.iptv.MainActivity;
import com.andreyrk.iptv.R;
import com.andreyrk.iptv.Utilities;
import com.comcast.viper.hlsparserj.IPlaylist;
import com.comcast.viper.hlsparserj.MediaPlaylist;
import com.comcast.viper.hlsparserj.PlaylistFactory;
import com.comcast.viper.hlsparserj.PlaylistVersion;
import com.comcast.viper.hlsparserj.tags.media.ExtInf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AllChannelsFragment extends Fragment {
    public int sort = R.id.menu_original;
    public String group = "";
    public List<String> groups = new ArrayList<>();
    public List<IPTVChannel> channels = new ArrayList<>();
    public List<IPTVChannel> results = new ArrayList<>();

    IPTVChannelAdapter adapter;
    RecyclerView recyclerView;
    TextView warning;
    SwipeRefreshLayout swipeRefresh;

    PlaylistTask asyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_channels, container, false);
        setHasOptionsMenu(true);

        final MainActivity activity = ((MainActivity) getActivity());
        activity.getSupportActionBar().setTitle(R.string.activity_name_all_channels);

        adapter = new IPTVChannelAdapter(activity, results);
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
                builder.setTitle(R.string.dialog_title_favorite);
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.addFavorite(channel);

                        Toast.makeText(activity, R.string.toast_added_favorite, Toast.LENGTH_SHORT).show();

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

        warning = view.findViewById(R.id.warning);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.VISIBLE);
                warning.setVisibility(View.GONE);

                asyncTask.cancel(true);
                asyncTask = new PlaylistTask();
                asyncTask.execute(activity.playlist);
            }
        });

        if (activity.playlist != null) {
            swipeRefresh.setRefreshing(true);

            asyncTask = new PlaylistTask();
            asyncTask.execute(activity.playlist);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        asyncTask.cancel(true);

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_all_channels, menu);

        final AllChannelsFragment fragment = this;

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fragment.results.clear();
                for (IPTVChannel channel : fragment.channels) {
                    if (channel.title != null && channel.title.toLowerCase().contains(query.toLowerCase())) {
                        results.add(channel);
                    }
                }

                fragment.adapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fragment.results = new ArrayList<>(fragment.channels);
                    fragment.adapter.setData(results);
                }

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                PopupMenu popupSort = new PopupMenu(getActivity(), getActivity().findViewById(item.getItemId()));

                popupSort.inflate(R.menu.sort);

                popupSort.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        sort = item.getItemId();

                        filter(group);
                        sort(sort);

                        return true;
                    }
                });

                popupSort.show();

                break;
            case R.id.menu_filter:
                PopupMenu popupFilter = new PopupMenu(getActivity(), getActivity().findViewById(item.getItemId()));

                popupFilter.getMenu().add(0, -1, 0, R.string.activity_name_all_channels);

                int count = 0;
                for (String group : groups) {
                    popupFilter.getMenu().add(0, count, 0, group);
                    count++;
                }

                popupFilter.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case -1:
                                group = "";

                                break;
                            default:
                                group = menuItem.getTitle().toString();

                                break;
                        }

                        filter(group);
                        sort(sort);

                        return true;
                    }
                });

                popupFilter.show();

                break;
        }

        return true;
    }

    public void sort(int id) {
        switch (id) {
            case R.id.menu_alphabetical:
                Collections.sort(results, new IPTVChannel.AZComparator());

                adapter.notifyDataSetChanged();

                break;
            case R.id.menu_alphabetical_reverse:
                Collections.sort(results, new IPTVChannel.AZComparator());
                Collections.reverse(results);

                adapter.notifyDataSetChanged();

                break;
            case R.id.menu_original:
                List<IPTVChannel> original = new ArrayList<>();
                for (IPTVChannel channel : channels) {
                    if (results.contains(channel)) {
                        original.add(channel);
                    }
                }

                results = original;
                adapter.setData(results);

                break;
            case R.id.menu_original_reverse:
                List<IPTVChannel> originalReverse = new ArrayList<>();
                for (IPTVChannel channel : channels) {
                    if (results.contains(channel)) {
                        originalReverse.add(channel);
                    }
                }

                Collections.reverse(originalReverse);

                results = originalReverse;
                adapter.setData(results);

                break;
        }
    }

    public void filter(String group) {
        if (TextUtils.isEmpty(group)) {
            results = new ArrayList<>(channels);
            adapter.setData(results);
        } else {
            results.clear();
            for (IPTVChannel channel : channels) {
                if (channel.group != null && channel.group.equals(group)) {
                    results.add(channel);
                }
            }

            adapter.notifyDataSetChanged();
        }
    }

    class PlaylistTask extends AsyncTask<IPTVPlaylist, IPTVChannel, Void> {

        @Override
        protected Void doInBackground(IPTVPlaylist... playlists) {
            groups.clear();
            channels.clear();
            results.clear();

            try {
                // Create a new trust manager that trust all certificates
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                            public void checkClientTrusted(
                                    X509Certificate[] certs, String authType) {
                            }
                            public void checkServerTrusted(
                                    X509Certificate[] certs, String authType) {
                            }
                        }
                };

                // Activate the new trust manager
                try {
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                } catch (Exception e) {
                }

                URL url = new URL(Utilities.getFinalURL(playlists[0].url));
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();

                IPlaylist playlist = PlaylistFactory.parsePlaylist(PlaylistVersion.TWELVE, inputStream);
                MediaPlaylist mediaPlaylist = (MediaPlaylist) playlist;

                for (ExtInf data : mediaPlaylist.getSegments()) {
                    String group = Utilities.getAttributeOrDefault("group-title", "", data.getTag().getRawTag());
                    String logo = Utilities.getAttributeOrDefault("tvg-logo", "", data.getTag().getRawTag());

                    IPTVChannel channel = new IPTVChannel(data.getTitle(), group, data.getURI(), logo);

                    publishProgress(channel);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(IPTVChannel... values) {
            channels.add(values[0]);
            results.add(values[0]);

            adapter.notifyDataSetChanged();

            if (!groups.contains(values[0].group)) {
                groups.add((values[0].group));
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            swipeRefresh.setRefreshing(false);

            if (channels.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                warning.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
}