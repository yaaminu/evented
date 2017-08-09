package com.evented.events.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.ui.EventDetailsActivity;
import com.evented.utils.GenericUtils;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;

/**
 * Created by yaaminu on 8/8/17.
 */

public class EventDetailsFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_event_details;
    }


    Realm realm;
    Event event;

    @BindView(R.id.event)
    TextView eventTv;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String eventId = getArguments().getString(EventDetailsActivity.EXTRA_EVENT_ID);
        GenericUtils.assertThat(eventId != null, "event id required");

        event = realm.where(Event.class)
                .equalTo(Event.FEILD_EVENT_ID, eventId)
                .findFirstAsync();
        event.addChangeListener(changeListener);
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    final RealmChangeListener<RealmObject> changeListener = new RealmChangeListener<RealmObject>() {
        @Override
        public void onChange(RealmObject o) {
            eventTv.setText(o.toString());
        }
    };
}
