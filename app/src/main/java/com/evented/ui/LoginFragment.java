package com.evented.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.evented.R;
import com.evented.events.data.User;
import com.evented.events.data.UserManager;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yaaminu on 8/13/17.
 */

public class LoginFragment extends BaseFragment {

    @BindView(R.id.et_phone)
    EditText et_phone;

    ProgressDialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof LoginActivity, "can only be embeded in "
                + LoginActivity.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Logging in");

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.title_add_phone_number);

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.bt_login)
    void login() {
        if (validate()) {
            dialog.show();
            UserManager.getInstance()
                    .login(et_phone.getText().toString().trim())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<User>() {
                        @Override
                        public void call(User user) {
                            dialog.dismiss();
                            ((LoginActivity) getActivity())
                                    .onLoggedIn(user);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            dialog.dismiss();
                            GenericUtils.showDialog(getContext(), throwable.getMessage());
                        }
                    });
        }
    }

    private boolean validate() {
        if (et_phone.getText().toString().trim().isEmpty()) {
            et_phone.setError(getString(R.string.phone_required));
            return false;
        }
        return true;
    }
}
