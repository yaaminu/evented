package com.evented.events.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;

import java.math.BigDecimal;
import java.math.MathContext;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by yaaminu on 8/8/17.
 */

public class CreateEventFragment2 extends BaseFragment {

    @BindView(R.id.publicity_description)
    TextView publicityDescription;
    private String[] descriptions;

    @BindView(R.id.entrance_fee)
    EditText entranceFee;
    @BindView(R.id.available_tickets)
    EditText availableSeats;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof CreateEventActivity)) {
            throw new ClassCastException("containing activity must be " + CreateEventActivity.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        descriptions = getResources().getStringArray(R.array.publicity_options_des);
    }

    @Override
    protected int getLayout() {
        return R.layout.event_attendance_layout;
    }

    @OnClick(R.id.next)
    void next() {
        if (validate()) {
            ((CreateEventActivity) getActivity())
                    .onNext();
        }
    }

    @OnItemSelected(R.id.sp_publicity)
    void onPublicityChanged(int selectedItem) {
        getEvent().setPublicity(selectedItem);
        publicityDescription.setText(descriptions[selectedItem]);
    }

    private Event getEvent() {
        return ((CreateEventActivity) getActivity()).getEvent();
    }

    private boolean validate() {
        String entranceFee = this.entranceFee.getText().toString().trim();
        if (TextUtils.isEmpty(entranceFee.trim())) {
            entranceFee = "0";
        }
        try {
            long amount =
                    BigDecimal.valueOf(Double.parseDouble(entranceFee))
                            .multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128).longValue();
            getEvent().setEntranceFee(amount);
        } catch (NumberFormatException e) {
            this.entranceFee.setError(getString(R.string.invalid_amount) + entranceFee);
            return false;
        }
        try {
            int maxSeats = Integer.parseInt(availableSeats.getText().toString().trim(), 10);
            getEvent().setMaxSeats(maxSeats);
        } catch (NumberFormatException e) {
            this.availableSeats.setError(getString(R.string.invalid_num) + availableSeats.getText().toString());
            return false;
        }
        return true;
    }
}
