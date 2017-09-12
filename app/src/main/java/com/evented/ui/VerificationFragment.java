package com.evented.ui;

import android.app.ProgressDialog;
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

public class VerificationFragment extends BaseFragment {
    @BindView(R.id.et_verification_code)
    EditText et_verification_code;
    ProgressDialog dialog;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Logging in");

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.title_verify_phone);

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_verification;
    }

    @OnClick(R.id.bt_verify)
    void clicked() {
        if (validate()) {
            dialog.show();
            UserManager.getInstance()
                    .verify(et_verification_code.getText().toString().trim())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<User>() {
                        @Override
                        public void call(User user) {
                            dialog.dismiss();
                            ((LoginActivity) getActivity())
                                    .onVerified();
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
        if (et_verification_code.getText().toString().trim().isEmpty()) {
            et_verification_code.setError(getString(R.string.phone_required));
            return false;
        }
        return true;
    }

    @OnClick(R.id.bt_change_phone_number)
    void changeNumber() {
        dialog.show();
        UserManager.getInstance()
                .logout(getContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        dialog.dismiss();
                        ((LoginActivity) getActivity())
                                .changeNumber();
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
