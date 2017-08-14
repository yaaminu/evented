package com.evented.ui;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yaaminu on 8/11/17.
 */

public class SimpleEventListAdapter extends BaseListViewAdapter<Event> {

    public SimpleEventListAdapter(List<Event> items) {
        super(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_list_item_event, parent, false);
            convertView.setTag(new Holder(convertView));
        }
        Holder holder = ((Holder) convertView.getTag());

        holder.tv_start_time.setText(DateUtils.formatDateTime(parent.getContext(),
                item.getStartDate(), DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_TIME |
                        DateUtils.FORMAT_12HOUR));
        holder.tv_event_name.setText(item.getName());
        holder.tv_location.setText(item.getVenue().getName());
        holder.tv_going.setText(String.valueOf(item.getGoing()));

        return convertView;
    }


    static class Holder {
        @BindView(R.id.tv_location)
        TextView tv_location;
        @BindView(R.id.tv_event_name)
        TextView tv_event_name;
        @BindView(R.id.tv_start_time)
        TextView tv_start_time;
        @BindView(R.id.tv_going)
        TextView tv_going;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}