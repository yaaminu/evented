package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.Ticket;
import com.evented.tickets.TicketDetailsActivity;
import com.evented.utils.GenericUtils;

import java.util.Collections;

import butterknife.BindView;
import butterknife.OnItemClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 8/11/17.
 */

public class TicketsListFragment extends BaseFragment {
    private final RealmChangeListener<RealmResults<Ticket>> listener = new RealmChangeListener<RealmResults<Ticket>>() {
        @Override
        public void onChange(RealmResults<Ticket> tickets) {
            adapter.refil(tickets);
        }
    };
    @BindView(R.id.ticket_list)
    ListView ticketList;
    private Realm realm;

    RealmResults<Ticket> tickets;

    TicketListAdapter adapter;
    private String eventId;


    @Override
    protected int getLayout() {
        return R.layout.fragment_ticket_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = getArguments().getString(TicketsListActivity.EXTRA_EVENT_ID);
        GenericUtils.ensureNotNull(eventId);
        realm = Realm.getDefaultInstance();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tickets = realm.where(Ticket.class)
                .equalTo(Ticket.FIELD_EVENT_ID, eventId)
                .findAllAsync();
        adapter = new TicketListAdapter(Collections.<Ticket>emptyList());
        ticketList.setAdapter(adapter);
        tickets.addChangeListener(listener);
    }

    @Override
    public void onDestroyView() {
        tickets.removeAllChangeListeners();
        super.onDestroyView();
    }


    @OnItemClick(R.id.ticket_list)
    void onItemClick(int position) {
        Intent intent = new Intent(getContext(), TicketDetailsActivity.class);
        intent.putExtra(TicketDetailsActivity.EXTRA_TICKET_ID, adapter.getItem(position).getTicketId());
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
