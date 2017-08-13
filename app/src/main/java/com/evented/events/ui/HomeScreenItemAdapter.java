package com.evented.events.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.ui.RecyclerViewBaseAdapter;
import com.evented.utils.GenericUtils;

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
        setUpDrawables(delegate.context());
        today = Calendar.getInstance();
        thatDay = Calendar.getInstance();
    }

    @Override
    protected void doBindHolder(com.evented.events.ui.Holder holder, int position) {
        Event item = getItem(position);
        holder.eventName.setText(item.getName());
        holder.location.setText(item.getVenue());
        thatDay.setTimeInMillis(item.getStartDate());
        holder.startTime.setText(GenericUtils.formatDateTime(delegate.context(), today, thatDay));
        holder.flyer.setImageDrawable(drawables.get(position % drawables.size()));
    }

    @Override
    public com.evented.events.ui.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new com.evented.events.ui.Holder(inflater.inflate(R.layout.event_list_item_thumbnail, parent,
                false));
    }

    /*******************************************for testing*************************************/
    public static void setUpDrawables(Context context) {

        if (!drawables.isEmpty()) {
            return;
        }
        for (int i = 0; i < 10; i++) {
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
                return R.drawable.flyer;
            case 1:
                return R.drawable.flyer2;
            case 2:
                return R.drawable.flyer20;
            case 3:
                return R.drawable.flyer3;
            case 4:
                return R.drawable.flyer4;
            case 5:
                return R.drawable.flyer8;
            case 6:
                return R.drawable.flyer11;
            case 7:
                return R.drawable.flyer14;
            case 8:
                return R.drawable.flyer18;
            case 9:
                return R.drawable.flyer21;
            default:
                return R.drawable.flyer18;
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