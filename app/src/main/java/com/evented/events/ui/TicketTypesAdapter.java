package com.evented.events.ui;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.TicketType;
import com.evented.ui.BaseListViewAdapter;
import com.evented.utils.GenericUtils;

import java.util.List;

/**
 * Created by yaaminu on 8/14/17.
 */

public class TicketTypesAdapter extends BaseListViewAdapter<TicketType> {

    public TicketTypesAdapter(List<TicketType> items) {
        super(items);
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public TicketType getItem(int i) {
        if (i == 0) {
            return new TicketType(GenericUtils.getString(R.string.choose_ticket), 0, 0);
        }
        return super.getItem(i - 1);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
        }

        final TicketType item = getItem(i);
        if (i == 0) {
            ((TextView) view).setText(item.getName());
        } else {
            ((TextView) view).setText(Html.fromHtml("<b>" + item.getName() + " - " + item.getFormatedCost() + "</b>"));
        }
        return view;
    }
}
