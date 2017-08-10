package com.evented.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.events.ui.HomeScreenEventsAdapter;
import com.evented.events.ui.HomeScreenItemAdapter;
import com.evented.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 8/9/17.
 */

public class HomeScreenEventsListFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.empty_view)
    View emptyView;
    private Realm realm;
    private HomeScreenAdapterDelegate delegate;
    private HomeScreenEventsAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.fragment_home_screen_events_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        delegate = new HomeScreenAdapterDelegate(this,
                populateItems());
        adapter = new HomeScreenEventsAdapter(delegate);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        realm.addChangeListener(changeListener);
    }

    final RealmChangeListener<Realm> changeListener = new RealmChangeListener<Realm>() {
        @Override
        public void onChange(Realm realm) {
            delegate.setItems(populateItems());
            adapter.notifyDataChanged();
        }
    };


    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    private List<PeriodCategorizedEvents> populateItems() {
        List<PeriodCategorizedEvents> items = new ArrayList<>(6);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //today
        items.add(getSectionItems(getString(R.string.today), calendar.getTimeInMillis()));

        //tomorrow
        items.add(getSectionItems(getString(R.string.tomorrow), TimeUnit.DAYS.toMillis(1) + calendar.getTimeInMillis()));

        //this week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        items.add(getSectionItems(getString(R.string.this_week), calendar.getTimeInMillis()));

        //this month
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        items.add(getSectionItems(getString(R.string.this_month), calendar.getTimeInMillis()));

        //next month
        int thisMonth = calendar.get(Calendar.MONTH) + 1;
        int maxMonth = calendar.getActualMaximum(Calendar.MONTH);
        calendar.set(Calendar.MONTH, thisMonth > maxMonth ? (thisMonth - maxMonth) - 1/*start over*/ : thisMonth);
        items.add(getSectionItems(getString(R.string.next_month), calendar.getTimeInMillis()));

        //more
        int afterNextMonth = calendar.get(Calendar.MONTH) + 1;
        maxMonth = calendar.getActualMaximum(Calendar.MONTH);
        calendar.set(Calendar.MONTH, afterNextMonth > maxMonth ? (afterNextMonth - maxMonth) - 1/*start over*/ : afterNextMonth);
        items.add(getSectionItems(getString(R.string.later), calendar.getTimeInMillis()));
        return items;
    }

    private PeriodCategorizedEvents getSectionItems(String title, long oldestTime) {
        RealmResults<Event> all = realm.where(Event.class)
                .greaterThanOrEqualTo(Event.FIELD_START_DATE, oldestTime)
                .findAllSorted(Event.FIELD_START_DATE);

        List<Event> highlights = new ArrayList<>(Math.min(4, all.size()));
        for (int i = 0; i < all.size() && i < 4; i++) {
            highlights.add(all.get(i));
        }

        return new PeriodCategorizedEvents(oldestTime, title, highlights);
    }

    static class HomeScreenAdapterDelegate implements HomeScreenEventsAdapter.Delegate {

        List<PeriodCategorizedEvents> items;
        private final HomeScreenEventsListFragment context;
        SparseArray<HomeScreenItemAdapter> adapters;

        public HomeScreenAdapterDelegate(HomeScreenEventsListFragment context, List<PeriodCategorizedEvents> items) {
            this.items = items;
            this.context = context;
            adapters = new SparseArray<>(6);
        }


        @Override
        public void onItemClick(RecyclerViewBaseAdapter<PeriodCategorizedEvents, ?> adapter, View view, int position, long id) {
            Toast.makeText(context.getContext(), "item at " + position + " clicked ", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(view.getContext(), PeriodEventsListActivity.class);
            intent.putExtra(PeriodEventsListActivity.EXTRA_OLDEST, adapter.getItem(position).oldestTime);
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(RecyclerViewBaseAdapter<PeriodCategorizedEvents, ?> adapter, View view, int position, long id) {
            return false;
        }

        @NonNull
        @Override
        public List<PeriodCategorizedEvents> dataSet() {
            ViewUtils.showByFlag(items.isEmpty(), context.emptyView);
            ViewUtils.showByFlag(!items.isEmpty(), context.recyclerView);
            return items;
        }

        @Override
        public HomeScreenItemAdapter getAdapter(int position) {
            HomeScreenItemAdapter adapter = null;
            if (adapters.size() >= position + 1) {
                adapter = adapters.get(position);
            }
            if (adapter == null) {
                adapter = new HomeScreenItemAdapter(new DelegateImpl(context.getContext(),
                        items.get(position)));
                adapters.put(position, adapter);
            }
            return adapter;
        }

        @NonNull
        @Override
        public Context context() {
            return context.getContext();
        }

        public void setItems(List<PeriodCategorizedEvents> items) {
            this.items = items;
            adapters.clear();
        }
    }

    static class DelegateImpl implements RecyclerViewBaseAdapter.Delegate<Event> {

        private final PeriodCategorizedEvents periodCategorizedEvents;
        private final Context context;

        public DelegateImpl(Context context
                , PeriodCategorizedEvents periodCategorizedEvents) {
            this.periodCategorizedEvents = periodCategorizedEvents;
            this.context = context;

        }

        @Override
        public void onItemClick(RecyclerViewBaseAdapter<Event, ?> adapter, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
            final Event item = adapter.getItem(position);
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, item.getEventId());
            intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME,item.getName());
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(RecyclerViewBaseAdapter<Event, ?> adapter, View view, int position, long id) {
            return false;
        }

        @NonNull
        @Override
        public List<Event> dataSet() {
            return periodCategorizedEvents.events;
        }

        @NonNull
        @Override
        public Context context() {
            return context;
        }
    }

}
