package com.evented.ui;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.evented.R;
import com.evented.events.data.EventManager;
import com.evented.events.data.UserManager;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.Ticket;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.evented.utils.ViewUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yaaminu on 8/12/17.
 */

public class QrCodeFragment extends BaseFragment implements QRCodeReaderView.OnQRCodeReadListener {

    private static final String TAG = "QrCodeFragment";
    private static final String ARG_EVENT_ID = "eventId";

    VerifyTicketCallbacks callbacks;
    @BindView(R.id.qrdecoderview)
    QRCodeReaderView qrCodeReaderView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.tv_ticket_verified_message)
    TextView tv_ticket_verified_message;
    @BindView(R.id.camera_preview)
    View rootView;

    @BindView(R.id.gauge)
    View guage;

    private Action1<Ticket> onSuccess = new Action1<Ticket>() {
        @Override
        public void call(Ticket ticket) {
            if (getActivity() != null) {
                rootView.setClickable(true);
                ViewUtils.hideViews(progressBar, qrCodeReaderView, guage);
                tv_ticket_verified_message.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_checked_green, 0, 0);
                tv_ticket_verified_message.setText(R.string.ticket_verified_success_message);
                ViewUtils.showViews(tv_ticket_verified_message);
                callbacks.onTicketVerified(ticket);
            }
        }
    };
    private Action1<Throwable> onFailed = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            PLog.e(TAG, throwable.getMessage(), throwable);
            if (getActivity() != null) {
                rootView.setClickable(true);
                ViewUtils.hideViews(progressBar, qrCodeReaderView, guage);
                tv_ticket_verified_message.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_chekc_failed_red_24dp, 0, 0);
                tv_ticket_verified_message.setText(getString(R.string.ticket_verified_failed_message, throwable.getMessage()));
                ViewUtils.showViews(tv_ticket_verified_message);
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof VerifyTicketCallbacks, "must implement " + VerifyTicketCallbacks.class.getName());
        callbacks = ((VerifyTicketCallbacks) context);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView
                .setClickable(false);
        ViewUtils.hideViews(tv_ticket_verified_message, progressBar);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setTorchEnabled(true);
        qrCodeReaderView.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(final String text, PointF[] points) {
        ViewUtils.showViews(progressBar);

        EventManager.create(UserManager.getInstance())
                .fromQrCodeText(callbacks.getEvent().getEventId(), text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onFailed);
    }

    @Override
    public void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    public void onPause() {
        qrCodeReaderView.stopCamera();
        super.onPause();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_verify_qrcode;
    }

    @OnClick(R.id.camera_preview)
    void onClick(View view) {
        view.setClickable(false);
        ViewUtils.hideViews(tv_ticket_verified_message);
        ViewUtils.showViews(qrCodeReaderView, guage);
        callbacks.clearCurrentTicket();
    }
}
