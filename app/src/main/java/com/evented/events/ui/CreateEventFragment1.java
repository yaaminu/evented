package com.evented.events.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.evented.evented.R;
import com.evented.events.data.Event;
import com.evented.utils.PLog;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * Created by yaaminu on 8/8/17.
 */

public class CreateEventFragment1 extends BaseFragment {
    private static final String TAG = "CreateEventFragment1";

    @Override
    protected int getLayout() {
        return R.layout.fragment_create_event;
    }

    @BindView(R.id.next)
    View fab;

    @BindViews({R.id.et_event_name, R.id.location, R.id.description})
    List<EditText> textFields;

    @BindViews({R.id.start_date, R.id.start_time, R.id.end_date, R.id.end_time})
    List<TextView> dateTextViews;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof CreateEventActivity)) {
            throw new ClassCastException("containing activity must be " + CreateEventActivity.class.getName());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.next)
    public void next() {
        if (validate()) {
            ((CreateEventActivity) getActivity()).onNext();
        }
    }


    private boolean validate() {

        Event event = ((CreateEventActivity) getActivity()).getEvent();
        if (textFields.get(0).getText().toString().trim().isEmpty()) {
            textFields.get(0).requestFocus();
            textFields.get(0).setError(getString(R.string.error_event_required));
            return false;
        }
        event.setName(textFields.get(0).getText().toString().trim());


        if (textFields.get(1).getText().toString().trim().isEmpty()) {
            textFields.get(1).requestFocus();
            textFields.get(1).setError(getString(R.string.error_location_requred));
            return false;
        }
        event.setVenue(textFields.get(1).getText().toString().trim());

        if (textFields.get(2).getText().toString().trim().isEmpty()) {
            textFields.get(2).requestFocus();
            textFields.get(2).setError(getString(R.string.error_description_requred));
            return false;
        }
        event.setDescription(textFields.get(2).getText().toString().trim());

        if (event.getStartDate() <= 0) {
            showDialog(getString(R.string.error_start_date_required));
            return false;
        }
        if (event.getStartDate() < System.currentTimeMillis()) {
            showDialog(getString(R.string.error_start_date_past));
            return false;
        }
        if (event.getEndDate() <= 0) {
            showDialog(getString(R.string.error_end_date_required));
            return false;
        }
        if (event.getEndDate() <= event.getStartDate()) {
            showDialog(getString(R.string.error_end_date_before_start));
            return false;
        }
        return true;
    }


    private void showDialog(CharSequence message) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    @IdRes
    private int selectedDateItem;

    @OnClick({R.id.start_date, R.id.start_time, R.id.end_date, R.id.end_time})
    void datePickers(View v) {
        selectedDateItem = v.getId();
        switch (selectedDateItem) {
            case R.id.end_date:
            case R.id.start_date:
                showDatePicker(new GregorianCalendar());
                break;
            case R.id.end_time:
            case R.id.start_time:
                showTimePicker(new GregorianCalendar());
                break;
            default:
                throw new AssertionError();
        }
    }

    private void showDatePicker(Calendar date) {
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = new GregorianCalendar();

                TextView item = getView().findViewById(selectedDateItem);
                Event event = getEvent();

                if (selectedDateItem == R.id.start_date) {
                    if (event.getStartDate() > 0) {
                        calendar.setTimeInMillis(event.getStartDate());
                    }
                    calendar.set(Calendar.YEAR, i);
                    calendar.set(Calendar.MONTH, i1);
                    calendar.set(Calendar.DAY_OF_MONTH, i2);
                    event.setStartDate(calendar.getTimeInMillis());

                    ((TextView) getView().findViewById(R.id.start_time)).setText(
                            DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(),
                                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
                } else if (selectedDateItem == R.id.end_date) {
                    if (event.getEndDate() > 0) {
                        calendar.setTimeInMillis(event.getEndDate());
                    }
                    calendar.set(Calendar.YEAR, i);
                    calendar.set(Calendar.MONTH, i1);
                    calendar.set(Calendar.DAY_OF_MONTH, i2);
                    event.setEndDate(calendar.getTimeInMillis());

                    ((TextView) getView().findViewById(R.id.end_time)).setText(
                            DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(),
                                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
                } else {
                    throw new AssertionError();
                }

                item.setText(
                        DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(),
                                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                                        | DateUtils.FORMAT_ABBREV_ALL));
                selectedDateItem = -1;

                PLog.d(TAG, i + ": " + i1 + ":" + i2);
            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void showTimePicker(Calendar date) {
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                PLog.d(TAG, i + ": " + i1);

                final Event event = getEvent();
                Calendar calendar = new GregorianCalendar();

                TextView item = getView().findViewById(selectedDateItem);

                if (selectedDateItem == R.id.end_time) {

                    if (event.getEndDate() > 0) {
                        calendar.setTime(new Date(event.getEndDate()));
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, i);
                    calendar.set(Calendar.MINUTE, i1);
                    event.setEndDate(calendar.getTimeInMillis());
                } else if (selectedDateItem == R.id.start_time) {
                    if (event.getStartDate() > 0) {
                        calendar.setTime(new Date(event.getStartDate()));
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, i);
                    calendar.set(Calendar.MINUTE, i1);
                    event.setStartDate(calendar.getTimeInMillis());
                } else {
                    throw new AssertionError();
                }
                item.setText(
                        DateUtils.formatDateTime(getContext(), calendar.getTimeInMillis(),
                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR));
                selectedDateItem = -1;
            }
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), false)
                .show();
    }


    public Event getEvent() {
        return ((CreateEventActivity) getActivity()).getEvent();
    }
}
