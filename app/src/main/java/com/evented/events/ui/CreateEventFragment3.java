package com.evented.events.ui;

import android.content.Context;

import com.evented.evented.R;

import butterknife.OnClick;

/**
 * Created by yaaminu on 8/8/17.
 */

public class CreateEventFragment3 extends BaseFragment {
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
        return true;
    }
}
