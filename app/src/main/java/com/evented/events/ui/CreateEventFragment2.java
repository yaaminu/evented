package com.evented.events.ui;

import android.content.Context;

import com.evented.evented.R;

import butterknife.OnClick;

/**
 * Created by yaaminu on 8/8/17.
 */

public class CreateEventFragment2 extends BaseFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof CreateEventActivity)) {
            throw new ClassCastException("containing activity must be " + CreateEventActivity.class.getName());
        }
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

    private boolean validate() {
        return true;
    }
}
