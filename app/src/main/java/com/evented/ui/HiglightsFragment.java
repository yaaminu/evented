package com.evented.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;
import com.evented.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by yaaminu on 8/13/17.
 */

public class HiglightsFragment extends BaseFragment implements Target {
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
    @BindView(R.id.image_loading_progress)
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    private final RealmChangeListener<Realm> changeListener = new RealmChangeListener<Realm>() {
        @Override
        public void onChange(Realm o) {
            updateHighlightsView(getView());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (highlightEvent == null) {
            realm.addChangeListener(changeListener);
        }
    }

    @Override
    public void onPause() {
        realm.removeChangeListener(changeListener);
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv_event_flyer.setImageResource(R.drawable.place_holder_image_background);
        ViewUtils.showViews(progressBar);
        updateHighlightsView(view);
    }

    @Nullable
    Event highlightEvent;

    private void updateHighlightsView(View view) {
        final RealmResults<Event> allEvents = realm.where(Event.class)
                .findAllSorted(Event.FIELD_GOING, Sort.DESCENDING);

        if (!allEvents.isEmpty() && highlightEvent == null) {
            realm.removeChangeListener(changeListener);
            GenericUtils.setUpDrawables(getContext());
            highlightEvent = allEvents.first();
            tv_location.setText(highlightEvent.getVenue().getName());
            tv_event_name.setText(highlightEvent.getName());
            tv_likes.setText(String.valueOf(highlightEvent.getLikes()));
            tv_going.setText(String.valueOf(highlightEvent.getGoing()));
            loadImage();
            view.setVisibility(View.VISIBLE);
        } else {
            realm.addChangeListener(changeListener);
            view.setVisibility(View.GONE);
        }
    }

    private void loadImage() {
        if (highlightEvent != null) {
            Picasso.with(getContext())
                    .load(highlightEvent.getFlyers()[0])
                    .placeholder(R.drawable.place_holder_image_background)
                    .error(R.drawable.place_holder_image_background)
                    .into(this);
        }
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @Override
    public void onBitmapLoaded(Bitmap image, Picasso.LoadedFrom from) {
        if (getActivity() == null) return;
        ViewUtils.hideViews(progressBar);
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
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
