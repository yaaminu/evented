package com.evented.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.events.ui.HomeScreenItemAdapter;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by yaaminu on 8/13/17.
 */

public class HiglightsFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.layout_higlight_item;
    }

    Realm realm;

    @BindView(R.id.tv_likes)
    TextView tv_likes;
    @BindView(R.id.tv_location)
    TextView tv_location;
    @BindView(R.id.tv_going)
    TextView tv_going;
    @BindView(R.id.tv_event_name)
    TextView tv_event_name;
    @BindView(R.id.iv_event_flyer)
    ImageView iv_event_flyer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RealmResults<Event> allEvents = realm.where(Event.class)
                .findAllSorted(Event.FIELD_GOING, Sort.DESCENDING);
        if (!allEvents.isEmpty()) {
            HomeScreenItemAdapter.setUpDrawables(getContext());
            Event event = allEvents.first();
            tv_location.setText(event.getVenue());
            tv_event_name.setText(event.getName());
            tv_likes.setText(String.valueOf(event.getLikes()));
            tv_going.setText(String.valueOf(event.getGoing()));
            iv_event_flyer.setImageDrawable(getImage(getContext()));
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private BitmapDrawable getImage(Context context) {
        int res = R.drawable.flyer18;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),
                res, options);
        final int requiredHeight = context.getResources().getDimensionPixelSize(R.dimen.highlights_height);
        final int requiredWidth = context.getResources().getDisplayMetrics().widthPixels;

        options.inSampleSize = Math.max(options.outWidth / requiredWidth, options.outHeight / requiredHeight);
        if (options.inSampleSize == 0) {
            options.inSampleSize = 1;
        } else if (options.inSampleSize < 1) {
            options.inSampleSize = (int) Math.ceil(1 / options.inSampleSize);
        }
        options.inJustDecodeBounds = false;
        return new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(),
                res, options));
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
