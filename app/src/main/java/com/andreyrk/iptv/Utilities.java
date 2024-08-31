package com.andreyrk.iptv;

import android.content.pm.PackageManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class Utilities {
    public static String getFinalURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();

        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location");
            return getFinalURL(redirectUrl);
        }

        return url;
    }

    public static String getAttributeOrDefault(String attributeName, String attributeDefault, String text) {
        Pattern p = Pattern.compile(attributeName + "=\"(.*?)\"");
        java.util.regex.Matcher m = p.matcher(text);

        if (m.find()) {
            return m.group(1);
        } else {
            return attributeDefault;
        }
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {

        boolean found = true;

        try {

            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {

            found = false;
        }

        return found;
    }
}
