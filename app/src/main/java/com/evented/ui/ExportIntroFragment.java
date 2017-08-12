package com.evented.ui;

import android.widget.CheckBox;

import com.evented.R;
import com.evented.events.ui.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yaaminu on 8/12/17.
 */

public class ExportIntroFragment extends BaseFragment {
    @BindView(R.id.cb_open_after_export)
    CheckBox openAfterExport;

    @Override
    protected int getLayout() {
        return R.layout.fragment_export_tickets_intro;
    }

    @OnClick(R.id.begin_export)
    void onClick() {
        ((ExportTicketsActivity) getActivity()).onNext(openAfterExport.isChecked());
    }
}
