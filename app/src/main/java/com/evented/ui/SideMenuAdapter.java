package com.evented.ui;

import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.evented.R;

/**
 * Created by yaaminu on 8/11/17.
 */

public class SideMenuAdapter extends BaseAdapter {

    private final String[] titles;
    private final int[] sideIcons;

    SideMenuAdapter(String[] titles, @DrawableRes int[] sideIcons) {
        this.titles = titles;
        this.sideIcons = sideIcons;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public String getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_list_item, viewGroup, false);
        }
        ((TextView) convertView).setText(titles[i]);
        ((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        ((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(sideIcons[i], 0, R.drawable.ic_chevron_right_black_24dp, 0);
        return convertView;
    }
}
