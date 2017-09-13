package com.evented.ui;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;
import android.view.ViewGroup;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.utils.GenericUtils;
import com.evented.utils.ViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by yaaminu on 8/9/17.
 */

class EventsListAdapter extends RecyclerViewBaseAdapter<Event, EventListItemHolder> {

    private final String[] categories;

    public EventsListAdapter(Delegate<Event> delegate) {
        super(delegate);
        categories = delegate.context().getResources().getStringArray(R.array.event_cagories);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void doBindHolder(final EventListItemHolder holder, int position) {
        final Event item = getItem(position);
        holder.tvEventName.setText(item.getName());
        holder.tvLocation.setText(item.getVenue().getName());
        holder.tvStartTime.setText(DateUtils.formatDateTime(delegate.context(), item.getStartDate(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR));
        holder.tvDescription.setText(item.getDescription());
        holder.likes.setText(String.valueOf(item.getLikes()));
        holder.likes.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.likes.setCompoundDrawablesWithIntrinsicBounds(
                item.isLiked() ? R.drawable.ic_favorite_black_fill_24dp : R.drawable.ic_favorite_border_black_24dp, 0, 0, 0
        );
        holder.going.setText(String.valueOf(item.getGoing()));
        holder.going.setCompoundDrawablesWithIntrinsicBounds(
                item.isCurrentUserGoing() ? R.drawable.ic_attendance_colored_24dp : R.drawable.ic_attendance_white_24dp, 0, 0, 0);
        holder.flyer.setImageBitmap(GenericUtils.getImage(delegate.context()));
        holder.likes.setTag(item.getEventId());
        ViewUtils.showViews(holder.progressBar);

        if (item.getFlyers().length > 0) {
            Picasso.with(delegate.context())
                    .load(item.getFlyers()[0])
                    .placeholder(R.drawable.place_holder_image_background)
                    .error(R.drawable.place_holder_image_background)
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
    public EventListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventListItemHolder(inflater.inflate(R.layout.list_item_event, parent, false));
    }

}
