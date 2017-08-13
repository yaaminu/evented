package com.evented.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.evented.R;
import com.evented.events.ui.BaseFragment;

import butterknife.OnClick;

/**
 * Created by yaaminu on 8/13/17.
 */

public class LoginIntroFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.login_intro;
    }

    @OnClick(R.id.bt_add_phone)
    void onClick() {
        ((LoginActivity) getActivity())
                .onAddPhone();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.welcome);

    }
}
