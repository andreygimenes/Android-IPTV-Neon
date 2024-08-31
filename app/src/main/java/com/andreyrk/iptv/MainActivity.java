package com.andreyrk.iptv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.andreyrk.iptv.adapters.IPTVChannel;
import com.andreyrk.iptv.adapters.IPTVPlaylist;
import com.andreyrk.iptv.fragments.AllChannelsFragment;
import com.andreyrk.iptv.fragments.FavoriteChannelsFragment;
import com.andreyrk.iptv.fragments.PlaylistFragment;
import com.andreyrk.iptv.fragments.RecentChannelsFragment;
import com.andreyrk.iptv.fragments.SettingsFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public IPTVPlaylist playlist;
    public List<IPTVPlaylist> playlists;
    public List<IPTVChannel> favorites;
    public List<IPTVChannel> recents;

    public NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshTheme();
        setContentView(R.layout.activity_main);

        playlist = getPlaylist();
        playlists = getPlaylists();
        favorites = getFavorites();
        recents = getRecents();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_all_channels:
                loadFragment(AllChannelsFragment.class);
                break;
            case R.id.nav_favorites:
                loadFragment(FavoriteChannelsFragment.class);
                break;
            case R.id.nav_recent:
                loadFragment(RecentChannelsFragment.class);
                break;
            case R.id.nav_playlists:
                loadFragment(PlaylistFragment.class);
                break;
            case R.id.nav_settings:
                loadFragment(SettingsFragment.class);
                break;
        }

        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }

        item.setChecked(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void loadFragment(Class<? extends Fragment> fragmentClass) {
        FragmentManager fragmentManager = getSupportFragmentManager(); // For AppCompat use getSupportFragmentManager

        try {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentClass.newInstance())
                    .commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public int prefs_getInt(String key, int defaultValue) {
        SharedPreferences prefViewer = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        return prefViewer.getInt(key, defaultValue);
    }

    public void prefs_putInt(String key, int value) {
        SharedPreferences prefViewer = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefViewer.edit();

        prefEditor.putInt(key, value);
        prefEditor.apply();
    }

    public String prefs_getString(String key, String defaultValue) {
        SharedPreferences prefViewer = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        return prefViewer.getString(key, defaultValue);
    }

    public void prefs_putString(String key, String value) {
        SharedPreferences prefViewer = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefViewer.edit();

        prefEditor.putString(key, value);
        prefEditor.apply();
    }

    public void refreshTheme() {
        int id = prefs_getInt("Theme", R.style.AppTheme_Dark_Blue);
        int theme;

        switch(id) {
            case R.style.AppTheme_Light_Red:
            case R.style.AppTheme_Light_Green:
            case R.style.AppTheme_Light_Blue:
            case R.style.AppTheme_Light_Pink:
            case R.style.AppTheme_Dark_Red:
            case R.style.AppTheme_Dark_Green:
            case R.style.AppTheme_Dark_Blue:
            case R.style.AppTheme_Dark_Pink:
                theme = id;
                break;
            default:
                theme = R.style.AppTheme_Dark_Blue;
        }

        setTheme(theme);
    }

    // Current card_playlist

    public void setPlaylist(IPTVPlaylist playlist) {
        this.playlist = playlist;
        savePlaylist();
    }

    public IPTVPlaylist getPlaylist() {
        String json = prefs_getString("Playlist", "");

        if (json.isEmpty()) {
            return new IPTVPlaylist("Empty card_playlist", "");
        } else {
            return new Gson().fromJson(json, IPTVPlaylist.class);
        }
    }

    public void savePlaylist() {
        prefs_putString("Playlist", new Gson().toJson(playlist));
    }

    // Playlists

    public void addPlaylist(IPTVPlaylist playlist) {
        playlist.id = prefs_getInt("PlaylistID", 0) + 1;
        prefs_putInt("PlaylistID", playlist.id);

        playlists.add(playlist);
        savePlaylists();
    }

    public void removePlaylist(IPTVPlaylist playlist) {
        playlists.remove(playlist);
        savePlaylists();
    }

    public List<IPTVPlaylist> getPlaylists() {
        String json = prefs_getString("Playlists", "");

        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(json, new TypeToken<List<IPTVPlaylist>>(){}.getType());
        }
    }

    public void savePlaylists() {
        prefs_putString("Playlists", new Gson().toJson(playlists));
    }

    // Favorites

    public void addFavorite(IPTVChannel channel) {
        if (favorites.contains(channel)) return;

        favorites.add(channel);
        saveFavorites();
    }

    public void removeFavorite(IPTVChannel channel) {
        favorites.remove(channel);
        saveFavorites();
    }

    public List<IPTVChannel> getFavorites() {
        String json = prefs_getString("Favorites", "");

        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(json, new TypeToken<List<IPTVChannel>>(){}.getType());
        }
    }

    public void saveFavorites() {
        prefs_putString("Favorites", new Gson().toJson(favorites));
    }

    // Recents

    public void addRecent(IPTVChannel channel) {
        if (recents.contains(channel)) return;

        recents.add(channel);
        saveRecents();
    }

    public void removeRecent(IPTVChannel channel) {
        recents.remove(channel);
        saveRecents();
    }

    public List<IPTVChannel> getRecents() {
        String json = prefs_getString("Recents", "");

        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(json, new TypeToken<List<IPTVChannel>>(){}.getType());
        }
    }

    public void saveRecents() {
        prefs_putString("Recents", new Gson().toJson(recents));
    }

    public void play(IPTVChannel channel) {
        String packageName = "";

        switch (prefs_getInt("VideoPlayer", R.id.videoPlayer_Default)) {
            case R.id.videoPlayer_VLC:
                break;
            case R.id.videoPlayer_MXPlayerFree:
                break;
            case R.id.videoPlayer_MXPlayerPro:
                break;
        }

        Uri uri = Uri.parse(channel.url);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/*");

        boolean exists = true;

        if (!packageName.isEmpty()) {
            intent.setPackage(packageName);

            exists = Utilities.isPackageInstalled(packageName, getPackageManager());
        }

        if (exists) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.toast_video_player_not_installed, Toast.LENGTH_SHORT).show();
        }
    }
}
