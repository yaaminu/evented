package com.evented.events.ui;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.ui.RecyclerViewBaseAdapter;
import com.evented.utils.GenericUtils;
import com.evented.utils.ViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by yaaminu on 8/9/17.
 */

public class HomeScreenItemAdapter extends RecyclerViewBaseAdapter<Event, Holder> {
    private static final String TAG = "HomeScreenItemAdapter";
    public static final ArrayList<BitmapDrawable> drawables = new ArrayList<>();

    private Calendar today;
    private Calendar thatDay;

    public HomeScreenItemAdapter(Delegate<Event> delegate) {
        super(delegate);
        GenericUtils.setUpDrawables(delegate.context());
        today = Calendar.getInstance();
        thatDay = Calendar.getInstance();
    }

    @Override
    protected void doBindHolder(final com.evented.events.ui.Holder holder, int position) {
        Event item = getItem(position);
        holder.eventName.setText(item.getName());
        holder.location.setText(item.getVenue().getName());
        thatDay.setTimeInMillis(item.getStartDate());
        holder.startTime.setText(GenericUtils.formatDateTime(delegate.context(), today, thatDay));
        ViewUtils.showViews(holder.progressBar);
        if (item.getFlyers().length > 0) {
            Picasso.with(delegate.context())
                    .load(item.getFlyers()[0])
                    .into(holder.flyer, new Callback() {
                        @Override
                        public void onSuccess() {
                            ViewUtils.hideViews(holder.progressBar);
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
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
    @BindView(R.id.image_loading_progress)
    ProgressBar progressBar;

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