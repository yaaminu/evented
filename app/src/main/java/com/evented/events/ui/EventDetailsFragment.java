package com.evented.events.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evented.BuildConfig;
import com.evented.R;
import com.evented.events.data.Event;
import com.evented.ui.EventDetailsActivity;
import com.evented.ui.LauncherActivity;
import com.evented.ui.TicketsListActivity;
import com.evented.utils.GenericUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * Created by yaaminu on 8/8/17.
 */

public class EventDetailsFragment extends BaseFragment {

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_view_tickets) {
                Intent intent = new Intent(getContext(), TicketsListActivity.class);
                intent.putExtra(TicketsListActivity.EXTRA_EVENT_ID, event.getEventId());
                intent.putExtra(TicketsListActivity.EXTRA_EVENT_TITLE, getString(R.string.tickets_list, event.getName()));
                getActivity().startActivity(intent);
                return true;
            }
            return false;
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.fragment_event_details;
    }


    Realm realm;
    Event event;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_location)
    TextView location;

    @BindView(R.id.tv_description)
    TextView tv_description;

    @BindView(R.id.tv_going)
    TextView tv_going;

    @BindView(R.id.tv_start_time)
    TextView tv_start_time;

    @BindView(R.id.iv_event_flyer)
    ImageView iv_event_flyer;

    @BindView(R.id.tv_likes)
    TextView tv_likes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String eventId = getArguments().getString(EventDetailsActivity.EXTRA_EVENT_ID),
                eventName = getArguments().getString(EventDetailsActivity.EXTRA_EVENT_NAME);

        GenericUtils.assertThat(eventId != null, "event id required");

        event = realm.where(Event.class)
                .equalTo(Event.FIELD_EVENT_ID, eventId)
                .findFirstAsync();
        collapsingToolbarLayout.setTitle(eventName);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        toolbar.inflateMenu(R.menu.event_details);
        toolbar.getMenu().findItem(R.id.action_view_tickets)
                .setVisible(!BuildConfig.FLAVOR.equals(LauncherActivity.FLAVOR_ATTENDEES));
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        event.addChangeListener(changeListener);

    }

    @Override
    public void onDestroyView() {
        event.removeAllChangeListeners();
        super.onDestroyView();
    }

    @OnClick(R.id.book_ticket)
    void bookTicket() {
        BottomSheetDialogFragment dialogFragment = new BookTicketDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(BookTicketDialogFragment.ARG_EVENT_ID, event.getEventId());
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getChildFragmentManager(), "bookTicket");
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    final RealmChangeListener<Event> changeListener = new RealmChangeListener<Event>() {
        @Override
        public void onChange(Event event) {
            collapsingToolbarLayout.setTitle(EventDetailsFragment.this.event.getName());
            location.setText(event.getVenue());
            tv_description.setText(event.getDescription());
            tv_going.setText(getString(R.string.attending, event.getGoing(), event.getMaxSeats()));
            tv_start_time.setText(DateUtils.formatDateTime(getContext(), event.getStartDate(),
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL));
            tv_likes.setText(String.valueOf(event.getLikes()));
        }
    };

    @OnClick(R.id.iv_event_flyer)
    void onClick() {
        // TODO: 8/10/17 launch an image view to view image
        Toast.makeText(getContext(), "Showing image", Toast.LENGTH_SHORT).show();
    }

}
