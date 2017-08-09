package com.evented.events.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.evented.R;
import com.evented.ui.PeriodCategorizedEvents;
import com.evented.ui.RecyclerViewBaseAdapter;

/**
 * Created by yaaminu on 8/9/17.
 */

public class HomeScreenEventsAdapter extends RecyclerViewBaseAdapter<PeriodCategorizedEvents, HomeScreenListItemHolder> {
    private static final String TAG = "HomeScreenEventsAdapter";
    private final LayoutInflater inflater;
    private final Delegate delegate;

    public HomeScreenEventsAdapter(Delegate delegate) {
        super(delegate);
        inflater = LayoutInflater.from(delegate.context());
        this.delegate = delegate;
    }

    @Override
    protected void doBindHolder(HomeScreenListItemHolder holder, int position) {
        PeriodCategorizedEvents item = getItem(position);
        holder.title.setText(item.title);
        holder.recyclerView.setAdapter(delegate.getAdapter(position));
    }

    @Override
    public HomeScreenListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeScreenListItemHolder homeScreenListItemHolder = new HomeScreenListItemHolder(inflater.inflate(R.layout.list_item_home_screen_events_list, parent,
                false));
        homeScreenListItemHolder.recyclerView.setLayoutManager(new LinearLayoutManager(delegate.context(),
                LinearLayoutManager.HORIZONTAL, false));
        return homeScreenListItemHolder;
    }


    public interface Delegate extends RecyclerViewBaseAdapter.Delegate<PeriodCategorizedEvents> {
        HomeScreenItemAdapter getAdapter(int position);
    }
}


