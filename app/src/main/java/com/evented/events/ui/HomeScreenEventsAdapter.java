package com.evented.events.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evented.ui.PeriodCategorizedEvents;
import com.evented.ui.RecyclerViewBaseAdapter;

/**
 * Created by yaaminu on 8/9/17.
 */

public class HomeScreenEventsAdapter extends RecyclerViewBaseAdapter<PeriodCategorizedEvents, HomeScreenListItemHolder> {

    private final LayoutInflater inflater;

    public HomeScreenEventsAdapter(Delegate delegate) {
        super(delegate);
        inflater = LayoutInflater.from(delegate.context());
    }

    @Override
    protected void doBindHolder(HomeScreenListItemHolder holder, int position) {
        ((TextView) holder.itemView).setText(getItem(position).title);
    }

    @Override
    public HomeScreenListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeScreenListItemHolder(inflater.inflate(android.R.layout.simple_list_item_1, parent,
                false));
    }


    public interface Delegate extends RecyclerViewBaseAdapter.Delegate<PeriodCategorizedEvents> {

    }
}


