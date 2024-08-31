package com.andreyrk.iptv.adapters;

import java.util.Comparator;

public class IPTVChannel {
    public String title;
    public String group;
    public String url;
    public String logo;

    public IPTVChannel(String title, String group, String url, String logo) {
        this.title = title;
        this.group = group;
        this.url = url;
        this.logo = logo;
    }

    public static class AZComparator implements Comparator<IPTVChannel> {
        public int compare(IPTVChannel channel_1, IPTVChannel channel_2) {
            String compare_1 = channel_1.title == null ? "" : channel_1.title.toUpperCase();
            String compare_2 = channel_2.title == null ? "" : channel_2.title.toUpperCase();

            return compare_1.compareTo(compare_2);
        }
    }

    public static class AZReverseComparator implements Comparator<IPTVChannel> {
        public int compare(IPTVChannel channel_1, IPTVChannel channel_2) {
            String compare_1 = channel_1.title == null ? "" : channel_1.title.toUpperCase();
            String compare_2 = channel_2.title == null ? "" : channel_2.title.toUpperCase();

            return compare_2.compareTo(compare_1);
        }
    }
}
