package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * Created by yaaminu on 8/10/17.
 */

public class SearchResultsFragment extends BaseFragment {
    private List<Event> searchResults;

    @BindView(R.id.search_results_list_view)
    ListView event_list;
    private SearchResultsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResults = Collections.emptyList();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new SearchResultsAdapter(Collections.<Event>emptyList());
        event_list.setAdapter(adapter);
    }

    @OnItemClick(R.id.search_results_list_view)
    void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Event event = (Event) adapterView.getItemAtPosition(position);
        Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event.getName());
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.getEventId());
        startActivity(intent);
    }

    public void updateResults(List<Event> searchResults) {
        adapter.refill(searchResults);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_search_results;
    }


}
