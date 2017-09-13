package com.evented.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.EventManager;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.ViewUtils;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yaaminu on 8/10/17.
 */

public class SimpleEventListFragment extends BaseFragment {

    @BindView(R.id.search_results_list_view)
    ListView event_list;
    @BindView(R.id.empty_view)
    View empty_view;

    @BindView(R.id.empty_view_text)
    TextView emptyViewText;

    @BindView(R.id.loading_progress)
    ProgressBar progressBar;

    private SimpleEventListAdapter adapter;
    private Subscription
            subscription;

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
        subscription = EventManager.create()
                .loadEventsForCurrentUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new SimpleEventListAdapter(Collections.<Event>emptyList());
        event_list.setEmptyView(empty_view);
        event_list.setAdapter(adapter);
        ViewUtils.showViews(progressBar);
        ViewUtils.hideViews(emptyViewText);
    }

    @Override
    public void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
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


    private final Observer<Object> observer = new Observer<Object>() {
        @Override
        public void onCompleted() {
            //do nothing
        }

        @Override
        public void onError(Throwable e) {
            ViewUtils.showViews(emptyViewText);
            ViewUtils.hideViews(progressBar);
            //show dialog
        }

        @Override
        public void onNext(Object o) {
            //stop loading
            ViewUtils.showViews(emptyViewText);
            ViewUtils.hideViews(progressBar);
        }
    };


}
