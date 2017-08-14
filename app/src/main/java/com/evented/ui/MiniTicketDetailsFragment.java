package com.evented.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.Ticket;
import com.evented.utils.GenericUtils;

import java.math.BigDecimal;
import java.math.MathContext;

import butterknife.BindView;

/**
 * Created by yaaminu on 8/12/17.
 */

public class MiniTicketDetailsFragment extends BaseFragment {


    @BindView(R.id.tv_event_name)
    TextView tv_event_name;
    @BindView(R.id.tv_start_time)
    TextView tv_start_time;
    @BindView(R.id.tv_date_purchased)
    TextView tv_date_purchased;
    @BindView(R.id.tv_ticket_number)
    TextView tv_ticket_number;
    @BindView(R.id.tv_ticket_cost)
    TextView tv_ticket_cost;
    @BindView(R.id.tv_ticket_type)
    TextView tv_ticket_type;

    private VerifyTicketCallbacks callbacks;

    @Override
    protected int getLayout() {
        return R.layout.fragment_mini_ticket_details;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof VerifyTicketCallbacks, "must implement " + VerifyTicketCallbacks.class.getName());
        callbacks = (VerifyTicketCallbacks) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Ticket ticket = callbacks.getTicket();
        tv_event_name.setText(ticket.getEventName());
        tv_start_time.setText(getString(R.string.starts_at, DateUtils.formatDateTime(getContext(),
                callbacks.getEvent().getStartDate(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_ABBREV_ALL)));
        tv_ticket_cost.setText(getString(R.string.ticket_cost, "" + BigDecimal.valueOf(ticket.getTicketCost()).divide(BigDecimal.valueOf(100),
                MathContext.DECIMAL128)));
        tv_date_purchased.setText(getString(R.string.purchased_on, DateUtils.formatDateTime(getContext(),
                ticket.getDatePurchased(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_ABBREV_ALL)));
        tv_ticket_number.setText(getString(R.string.ticket_no, ticket.getTicketNumber()));
        tv_ticket_type.setText(getString(R.string.ticket_class,ticket.getType()));

    }


}
