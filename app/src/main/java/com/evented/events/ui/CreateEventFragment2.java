package com.evented.events.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.utils.GenericUtils;
import com.evented.utils.TaskManager;
import com.evented.utils.ThreadUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by yaaminu on 8/8/17.
 */

public class CreateEventFragment2 extends BaseFragment {

    @BindView(R.id.publicity_description)
    TextView publicityDescription;
    private String[] descriptions;

    @BindView(R.id.entrance_fee)
    EditText entranceFee;
    @BindView(R.id.available_tickets)
    EditText availableSeats;

    @BindView(R.id.et_event_name)
    TextView et_event_name;

    @BindView(R.id.iv_event_flyer)
    ImageView iv_event_flyer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof CreateEventActivity)) {
            throw new ClassCastException("containing activity must be " + CreateEventActivity.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        descriptions = getResources().getStringArray(R.array.publicity_options_des);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        et_event_name.setText(((CreateEventActivity) getActivity()).getEvent().getName());
        loadImage(((CreateEventActivity) getActivity())
                        .getEvent().getFlyers(), getResources().getDisplayMetrics().widthPixels,
                getResources().getDimensionPixelSize(R.dimen.add_flyer_height));
    }


    private void loadImage(final String path, final int width, final int height) {

        if (!GenericUtils.isEmpty(path) && new File(path).exists()) {
            TaskManager.executeNow(new Runnable() {
                Bitmap bitmap;

                @Override
                public void run() {
                    if (ThreadUtils.isMainThread() && getActivity() != null) {
                        if (bitmap != null) {
                            iv_event_flyer.setImageBitmap(bitmap);
                            Palette palette = Palette.from(bitmap)
                                    .generate();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getActivity().getWindow().setStatusBarColor(palette.getDarkMutedColor(ContextCompat.getColor(getContext(),
                                        R.color.colorPrimaryDark)));
                            }
                        } else {
                            GenericUtils.showDialog(getContext(), getString(R.string.failed_to_load_flyer));
                        }
                    } else {
                        bitmap = GenericUtils.loadBitmap(path, width, height);
                        TaskManager.executeOnMainThread(this);
                    }
                }
            }, true);
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

    @OnItemSelected(R.id.sp_publicity)
    void onPublicityChanged(int selectedItem) {
        getEvent().setPublicity(selectedItem);
        publicityDescription.setText(descriptions[selectedItem]);
    }

    private Event getEvent() {
        return ((CreateEventActivity) getActivity()).getEvent();
    }

    private boolean validate() {
        String entranceFee = this.entranceFee.getText().toString().trim();
        if (TextUtils.isEmpty(entranceFee.trim())) {
            entranceFee = "0";
        }
        try {
            long amount =
                    BigDecimal.valueOf(Double.parseDouble(entranceFee))
                            .multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128).longValue();
            getEvent().setEntranceFee(amount);
        } catch (NumberFormatException e) {
            this.entranceFee.setError(getString(R.string.invalid_amount) + entranceFee);
            return false;
        }
        try {
            int maxSeats = Integer.parseInt(availableSeats.getText().toString().trim(), 10);
            getEvent().setMaxSeats(maxSeats);
        } catch (NumberFormatException e) {
            this.availableSeats.setError(getString(R.string.invalid_num) + availableSeats.getText().toString());
            return false;
        }
        return true;
    }
}
