package com.evented.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;

import butterknife.BindView;

/**
 * Created by yaaminu on 8/12/17.
 */

public class EmptyInstructionsFragment extends BaseFragment {
    @BindView(R.id.tv_instruction_text)
    TextView tv_instruction_text;

    private VerifyTicketCallbacks callbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof VerifyTicketCallbacks, "must implement " + VerifyTicketCallbacks.class.getName());
        callbacks = ((VerifyTicketCallbacks) context);
    }

    @Override
    protected int getLayout() {
        return R.layout.layout_empty_instruction;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_instruction_text.setText(callbacks.getVerificationStrategy() == VerifyTicketCallbacks.NFC ?
                R.string.nfc_instructions : R.string.qr_code_instructions);
    }

}
