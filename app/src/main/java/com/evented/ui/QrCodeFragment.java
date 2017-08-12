package com.evented.ui;

import android.content.Context;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;

/**
 * Created by yaaminu on 8/12/17.
 */

public class QrCodeFragment extends BaseFragment {

    VerifyTicketCallbacks callbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof VerifyTicketCallbacks, "must implement " + VerifyTicketCallbacks.class.getName());
        callbacks = ((VerifyTicketCallbacks) context);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_verify_qrcode;
    }
}
