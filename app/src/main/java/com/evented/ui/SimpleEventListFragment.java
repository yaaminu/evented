package com.evented.ui;

import android.content.Context;
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

public class SimpleEventListFragment extends BaseFragment {
    private List<Event> searchResults;

    @BindView(R.id.search_results_list_view)
    ListView event_list;
    private SimpleEventListAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof callbacks)) {
            throw new ClassCastException("containing actvitiy must implement " + callbacks.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResults = Collections.emptyList();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new SimpleEventListAdapter(Collections.<Event>emptyList());
        event_list.setAdapter(adapter);
    }

    @OnItemClick(R.id.search_results_list_view)
    void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ((callbacks) getActivity()).onItemClicked((Event) adapterView.getItemAtPosition(position));
    }

    public void updateResults(List<Event> searchResults) {
        adapter.refill(searchResults);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_simple_event_list;
    }


    interface callbacks {
        void onItemClicked(Event event);
    }

}
