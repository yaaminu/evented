package com.evented.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;

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
    @BindView(R.id.event_info_bar)
    View bar;

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
            GenericUtils.setUpDrawables(getContext());
            Event event = allEvents.first();
            tv_location.setText(event.getVenue());
            tv_event_name.setText(event.getName());
            tv_likes.setText(String.valueOf(event.getLikes()));
            tv_going.setText(String.valueOf(event.getGoing()));
            final Bitmap image = GenericUtils.getImage(getContext());
            iv_event_flyer.setImageBitmap(image);
            Palette.from(image)
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            if (getActivity() == null) {
                                return;
                            }
                            final int darkMutedColor = palette.getDarkMutedColor(ContextCompat.getColor(getContext(),
                                    R.color.colorPrimaryDark));

                            bar.setBackgroundColor(darkMutedColor);

                        }
                    });
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
