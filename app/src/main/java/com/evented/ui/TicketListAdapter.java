package com.evented.ui;

import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evented.R;
import com.evented.tickets.Ticket;

import java.math.BigDecimal;
import java.math.MathContext;

import butterknife.BindView;

import static com.evented.utils.GenericUtils.getString;

/**
 * Created by yaaminu on 8/11/17.
 */

public class TicketListAdapter extends RecyclerViewBaseAdapter<Ticket, TicketListAdapter.Holder> {

    public TicketListAdapter(Delegate<Ticket> delegate) {
        super(delegate);
    }


    @Override
    protected void doBindHolder(Holder holder, int position) {
        final Ticket item = getItem(position);
        holder.tv_date_purchased.setText(getString(R.string.date, DateUtils.formatDateTime(delegate.context(),
                item.getDatePurchased(), DateUtils.FORMAT_SHOW_DATE |
                        DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_TIME |
                        DateUtils.FORMAT_12HOUR)));
        holder.tv_event_name.setText(item.getEventName());
        holder.tv_ticket_cost.setText(getString(R.string.ticket_cost, "" + BigDecimal.valueOf(
                item.getTicketCost()).divide(BigDecimal.valueOf(100),
                MathContext.DECIMAL128).longValue()));
        holder.tv_ticket_number.setText(getString(R.string.ticket_no, item.getTicketNumber()));

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater
                .inflate(R.layout.list_item_ticket_grid, parent, false));
    }

    static class Holder extends RecyclerViewBaseAdapter.Holder {
        @BindView(R.id.tv_ticket_cost)
        TextView tv_ticket_cost;
        @BindView(R.id.tv_event_name)
        TextView tv_event_name;
        @BindView(R.id.tv_date_purchased)
        TextView tv_date_purchased;
        @BindView(R.id.tv_ticket_number)
        TextView tv_ticket_number;

        Holder(View view) {
            super(view);
        }
    }
}
