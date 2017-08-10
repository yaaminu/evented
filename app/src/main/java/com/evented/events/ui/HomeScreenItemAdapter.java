package com.evented.events.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.ui.RecyclerViewBaseAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by yaaminu on 8/9/17.
 */

public class HomeScreenItemAdapter extends RecyclerViewBaseAdapter<Event, Holder> {
    private static final String TAG = "HomeScreenItemAdapter";
    private static final ArrayList<BitmapDrawable> drawables = new ArrayList<>(6);

    public HomeScreenItemAdapter(Delegate<Event> delegate) {
        super(delegate);
        setUpDrawables(delegate.context());
    }

    @Override
    protected void doBindHolder(com.evented.events.ui.Holder holder, int position) {
        Event item = getItem(position);
        holder.eventName.setText(item.getName());
        holder.location.setText(item.getVenue());
        holder.startTime.setText(DateUtils.formatDateTime(delegate.context(), item.getStartDate(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
        holder.flyer.setImageDrawable(drawables.get(position % drawables.size()));
    }

    @Override
    public com.evented.events.ui.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new com.evented.events.ui.Holder(inflater.inflate(R.layout.event_list_item_thumbnail, parent,
                false));
    }

    /*******************************************for testing*************************************/
    private static void setUpDrawables(Context context) {

        if (!drawables.isEmpty()) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            int res = getResIdentifier(i);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),
                    res, options);
            final int requiredHeight = context.getResources().getDimensionPixelSize(R.dimen.home_screen_item_flyer_height);
            final int requiredWidth = context.getResources().getDimensionPixelSize(R.dimen.home_screen_item_width);

            options.inSampleSize = Math.max(options.outWidth / requiredWidth, options.outHeight / requiredHeight);
            if (options.inSampleSize == 0) {
                options.inSampleSize = 1;
            } else if (options.inSampleSize < 1) {
                options.inSampleSize = (int) Math.ceil(1 / options.inSampleSize);
            }
            options.inJustDecodeBounds = false;

            drawables.add(new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(),
                    res, options)));
        }
    }

    private static int getResIdentifier(int i) {
        switch (i) {
            case 0:
                return R.drawable.wating;
            case 1:
                return R.drawable.denu;
            case 2:
                return R.drawable.xoli;
            case 3:
                return R.drawable.iceberg;
            case 4:
                return R.drawable.ocean;
            case 5:
                return R.drawable.iceberg;
            default:
                return R.drawable.ocean;
        }
    }
    /****************************************for testing****************************************/
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