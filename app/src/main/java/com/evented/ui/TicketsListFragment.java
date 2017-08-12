package com.evented.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.Ticket;
import com.evented.tickets.TicketDetailsActivity;
import com.evented.utils.GenericUtils;

import java.util.List;

import butterknife.BindView;
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
            adapter.notifyDataChanged();
        }
    };

    @BindView(R.id.ticket_list)
    RecyclerView ticketList;
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
        adapter = new TicketListAdapter(delegate);
        final GridLayoutManager layoutManger = new GridLayoutManager(getContext(), 2);
        ticketList.setLayoutManager(layoutManger);
        ticketList.setAdapter(adapter);
        tickets.addChangeListener(listener);
    }

    @Override
    public void onDestroyView() {
        tickets.removeAllChangeListeners();
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    final RecyclerViewBaseAdapter.Delegate<Ticket> delegate = new RecyclerViewBaseAdapter.Delegate<Ticket>() {
        @Override
        public void onItemClick(RecyclerViewBaseAdapter<Ticket, ?> adapter, View view, int position, long id) {
            Intent intent = new Intent(getContext(), TicketDetailsActivity.class);
            intent.putExtra(TicketDetailsActivity.EXTRA_TICKET_ID, adapter.getItem(position).getTicketId());
            getActivity().startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(RecyclerViewBaseAdapter<Ticket, ?> adapter, View view, int position, long id) {
            return false;
        }

        @NonNull
        @Override
        public List<Ticket> dataSet() {
            return tickets;
        }

        @NonNull
        @Override
        public Context context() {
            return getActivity();
        }
    };
}
