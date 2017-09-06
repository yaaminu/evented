package com.evented.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.ViewUtils;

import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 8/9/17.
 */

public class EventsListFragment extends BaseFragment {

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_OLDEST = "oldest", ARG_SHOW_ONLY_FAVORITES = "onlyFavorites";
    private Realm realm;
    private int category;
    private long oldest;

    RealmResults<Event> events;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    View emptyView;
    private EventsListAdapter adapter;
    private boolean showOnlyFavorites;

    @Override
    protected int getLayout() {
        return R.layout.fragment_events_list;
    }

    public static Fragment create(long oldest, int position) {
        return create(oldest, position, false);
    }

    public static Fragment create(long oldest, int position, boolean showOnlyFavorites) {
        Fragment fragment = new EventsListFragment();
        Bundle bundle = new Bundle(2);
        bundle.putLong(ARG_OLDEST, oldest);
        bundle.putInt(ARG_CATEGORY, position);
        bundle.putBoolean(ARG_SHOW_ONLY_FAVORITES, showOnlyFavorites);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        oldest = getArguments().getLong(ARG_OLDEST);
        category = getArguments().getInt(ARG_CATEGORY);
        showOnlyFavorites = getArguments().getBoolean(ARG_SHOW_ONLY_FAVORITES);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final RealmQuery<Event> query = realm.where(Event.class)
                .greaterThanOrEqualTo(Event.FIELD_START_DATE, oldest);

        if (category != Event.CATEGORY_ALL) {
            query.equalTo(Event.FIELD_CATEGORY, category);
        }
        if (showOnlyFavorites) {
            query.equalTo(Event.FIELD_LIKED, true);
        }
        events = query
                .findAllSortedAsync(Event.FIELD_START_DATE);
        events.addChangeListener(changeListener);
        adapter = new EventsListAdapter(delegate);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        events.removeAllChangeListeners();
        super.onDestroyView();
    }

    final RealmChangeListener<RealmResults<Event>> changeListener = new RealmChangeListener<RealmResults<Event>>() {
        @Override
        public void onChange(RealmResults<Event> o) {
            adapter.notifyDataChanged();
        }
    };

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    RecyclerViewBaseAdapter.Delegate<Event> delegate = new RecyclerViewBaseAdapter.Delegate<Event>() {
        @Override
        public void onItemClick(RecyclerViewBaseAdapter<Event, ?> adapter, View view, int position, long id) {
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            final Event item = adapter.getItem(position);
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, item.getEventId());
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, item.getName());
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(RecyclerViewBaseAdapter<Event, ?> adapter, View view, int position, long id) {
            return false;
        }

        @NonNull
        @Override
        public List<Event> dataSet() {
            com.evented.utils.ViewUtils
                    .showByFlag(!events.isEmpty(), recyclerView);
            ViewUtils.showByFlag(events.isEmpty(), emptyView);
            return events;
        }

        @NonNull
        @Override
        public Context context() {
            return getContext();
        }
    };

}
