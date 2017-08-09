package com.evented.events.ui;

import android.content.Context;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.BillingAcount;
import com.evented.events.data.Event;
import com.evented.utils.GenericUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yaaminu on 8/8/17.
 */

public class CreateEventFragment3 extends BaseFragment {
    @BindView(R.id.et_phone)
    TextView phone;
    @BindView(R.id.et_name)
    TextView name;

    @Override
    protected int getLayout() {
        return R.layout.fragment_verify_event;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof CreateEventActivity)) {
            throw new ClassCastException("containing activity must be " + CreateEventActivity.class.getName());
        }
    }

    @OnClick(R.id.create_event)
    void createEvent() {
        if (validate()) {
            ((CreateEventActivity) getActivity()).onNext();
        }
    }

    private boolean validate() {
        final Event event = ((CreateEventActivity) getActivity())
                .getEvent();

        String name = this.name.getText().toString().trim(), phone = this.phone.getText().toString().trim();
        if (GenericUtils.isEmpty(name)) {
            this.name.setError(getString(R.string.err_name_required));
            return false;
        }
        if (GenericUtils.isEmpty(phone)) {
            this.name.setError(getString(R.string.error_phone_required));
            return false;
        }
        event.setBillingAcount(new BillingAcount(name, phone, "MTN"));

        return true;
    }
}
