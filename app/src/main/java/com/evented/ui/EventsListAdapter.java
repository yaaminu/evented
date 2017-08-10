package com.evented.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.ViewGroup;

import com.evented.R;
import com.evented.events.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaaminu on 8/9/17.
 */

class EventsListAdapter extends RecyclerViewBaseAdapter<Event, EventListItemHolder> {

    private final String[] categories;

    public EventsListAdapter(Delegate<Event> delegate) {
        super(delegate);
        categories = delegate.context().getResources().getStringArray(R.array.event_cagories);

        setUpDrawables(delegate.context());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void doBindHolder(EventListItemHolder holder, int position) {
        final Event item = getItem(position);
        holder.tvEventName.setText(item.getName());
        holder.tvLocation.setText(item.getVenue());
        holder.tvStartTime.setText(DateUtils.formatDateTime(delegate.context(), item.getStartDate(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR));
        holder.tvDescription.setText(item.getDescription());
        holder.likes.setText(String.valueOf(item.getLikes()));
        holder.going.setText(String.valueOf(item.getGoing()));
        holder.flyer.setImageDrawable(drawables.get(position % 2));
    }

    @Override
    public EventListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventListItemHolder(inflater.inflate(R.layout.list_item_event, parent, false));
    }


    /******************************For testing********************************/
    private static List<Drawable> drawables;

    private static void setUpDrawables(Context context) {
        if (drawables != null) {
            return;
        }
        drawables = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
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
                return R.drawable.ocean;
            case 1:
                return R.drawable.iceberg;
            default:
                return R.drawable.ocean;
        }
    }
/**********************************************************************************************************/
}
