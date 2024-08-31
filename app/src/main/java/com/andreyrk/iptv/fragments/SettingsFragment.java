package com.andreyrk.iptv.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andreyrk.iptv.MainActivity;
import com.andreyrk.iptv.R;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        final MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.activity_name_settings);

        RadioGroup theme = view.findViewById(R.id.theme_radioGroup);
        theme.check(activity.prefs_getInt("ThemeID", R.id.theme_DarkBlue));
        theme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int theme = R.style.AppTheme_Dark_Blue;

                switch(checkedId) {
                    case R.id.theme_LightRed:
                        theme = R.style.AppTheme_Light_Red;
                        break;
                    case R.id.theme_LightGreen:
                        theme = R.style.AppTheme_Light_Green;
                        break;
                    case R.id.theme_LightBlue:
                        theme = R.style.AppTheme_Light_Blue;
                        break;
                    case R.id.theme_LightPink:
                        theme = R.style.AppTheme_Light_Pink;
                        break;
                    case R.id.theme_DarkRed:
                        theme = R.style.AppTheme_Dark_Red;
                        break;
                    case R.id.theme_DarkGreen:
                        theme = R.style.AppTheme_Dark_Green;
                        break;
                    case R.id.theme_DarkBlue:
                        theme = R.style.AppTheme_Dark_Blue;
                        break;
                    case R.id.theme_DarkPink:
                        theme = R.style.AppTheme_Dark_Pink;
                        break;
                }

                activity.prefs_putInt("ThemeID", checkedId);
                activity.prefs_putInt("Theme", theme);
                activity.refreshTheme();

                activity.finish();
                startActivity(activity.getIntent());
            }
        });

        RadioGroup videoPlayer = view.findViewById(R.id.videoPlayer_radioGroup);
        videoPlayer.check(activity.prefs_getInt("VideoPlayer", R.id.videoPlayer_Default));
        videoPlayer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                activity.prefs_putInt("VideoPlayer", checkedId);
            }
        });

        return view;
    }
}