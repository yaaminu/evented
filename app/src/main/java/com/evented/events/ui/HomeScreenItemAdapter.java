package com.evented.events.ui;

import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.ui.RecyclerViewBaseAdapter;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by yaaminu on 8/9/17.
 */

public class HomeScreenItemAdapter extends RecyclerViewBaseAdapter<Event, Holder> {
    private static final String TAG = "HomeScreenItemAdapter";
    private final BitmapDrawable drawable;

    public HomeScreenItemAdapter(BitmapDrawable drawable, Delegate<Event> delegate) {
        super(delegate);
        this.drawable = drawable;
    }

    @Override
    protected void doBindHolder(com.evented.events.ui.Holder holder, int position) {
        Event item = getItem(position);
        holder.eventName.setText(item.getName());
        holder.location.setText(item.getVenue());
        holder.startTime.setText(DateUtils.formatDateTime(delegate.context(), item.getStartDate(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
        holder.flyer.setImageDrawable(drawable);
    }

    @Override
    public com.evented.events.ui.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new com.evented.events.ui.Holder(inflater.inflate(R.layout.event_list_item_thumbnail, parent,
                false));
    }
}


class Holder extends RecyclerViewBaseAdapter.Holder {
    @BindView(R.id.tv_event_name)
    TextView eventName;
    @BindView(R.id.tv_location)
    TextView location;
    @BindView(R.id.tv_start_time)
    TextView startTime;
    @BindView(R.id.iv_event_flyer)
    ImageView flyer;

    public Holder(View v) {
        super(v);
    }

    @OnClick({R.id.iv_event_flyer, R.id.tv_event_name, R.id.tv_start_time, R.id.tv_location})
    void onClick() {
        itemView.performClick();
    }

    @OnLongClick({R.id.iv_event_flyer, R.id.tv_event_name, R.id.tv_start_time, R.id.tv_location})
    boolean onLongClick() {
        return itemView.performClick();
    }
}