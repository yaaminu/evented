package com.evented.events.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.evented.evented.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yaaminu on 8/8/17.
 */

public class CreateEventFragment1 extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_create_event;
    }

    @BindView(R.id.next)
    View fab;

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
        return true;
    }

}
