package com.evented.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.EventManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yaaminu on 8/9/17.
 */

class EventListItemHolder extends RecyclerViewBaseAdapter.Holder {
    @BindView(R.id.tv_event_name)
    TextView tvEventName;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_likes)
    TextView likes;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.iv_event_flyer)
    ImageView flyer;
    @BindView(R.id.tv_going)
    TextView going;

    static final EventManager eventManager = EventManager.create();

    public EventListItemHolder(View v) {
        super(v);
    }

    @OnClick({R.id.tv_event_name, R.id.tv_location, R.id.tv_start_time, R.id.tv_likes, R.id.tv_going, R.id.tv_description, R.id.iv_event_flyer})
    void onClick(View view) {
        if (view.getId() == R.id.tv_likes) {
            eventManager
                    .toggleLikedAsync(((String) view.getTag()));
        } else {
            itemView.performClick();
        }
    }
}
