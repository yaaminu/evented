package com.evented.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.NearbyEvents;
import com.evented.events.data.UserManager;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.evented.utils.ViewUtils;
import com.rey.slidelayout.SlideLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yaaminu on 8/7/17.
 */

public class MainActivity extends AppCompatActivity implements SimpleEventListFragment.callbacks {


    private static final String TAG_EVENT_LIST = "eventList";
    private static final String TAG_SEARCH = "search";
    private HomeScreenEventsListFragment homeScreenEventListFragment;
    private Fragment currentFragment;


    @BindView(R.id.search_suggestion_list_view)
    ListView searchSuggestionListView;

    @Nullable
    SuggestionsArrayAdapter adapter;

    @BindView(R.id.search_suggestion)
    View suggestionPane;
    @BindView(R.id.clear_btn)
    ImageButton searchOrClear;
    @BindView(R.id.back)
    ImageButton back;

    @BindView(R.id.et_search)
    EditText searchEditText;
    Realm realm;
    @BindView(R.id.slide_layout)
    SlideLayout slide_layout;
    @BindView(R.id.side_menu_list)
    ListView sideMenu;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        homeScreenEventListFragment = new HomeScreenEventsListFragment();
        currentFragment = homeScreenEventListFragment;
        ViewUtils.hideViews(suggestionPane);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, currentFragment, TAG_EVENT_LIST)
                .commit();
        slide_layout.setOnStateChangedListener(stateChangedListener);
        int[] icons = {
                R.drawable.ic_trending_events_24dp,
                R.drawable.ic_nearby_events_24dp,
                R.drawable.ic_favorite_events_24dp,
                R.drawable.ic_my_events_24dp,
                R.drawable.ic_add_alert_24dp,
                R.drawable.ic_settings_24dp
        };
        sideMenu.setAdapter(new SideMenuAdapter(getResources().getStringArray(R.array.side_menu_titles),
                icons));
        ((TextView) findViewById(R.id.app_name_menu_title))
                .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bop.ttf"));
    }


    private final SlideLayout.OnStateChangedListener stateChangedListener = new SlideLayout.OnStateChangedListener() {
        @Override
        public void onStateChanged(View v, int old_state, int new_state) {
            PLog.d(TAG, "old state %d, new state %d", old_state, new_state);
            if (new_state == 0 && searchEditText.getText().length() == 0) {
                back.setImageResource(R.drawable.ic_menu_black_24dp);
            } else if (new_state == 16) {
                back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            }
        }

        @Override
        public void onOffsetChanged(View v, float offsetX, float offsetY, int state) {

        }
    };


    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @OnClick({R.id.clear_btn, R.id.back})
    void onClick(View view) {
        if (view.getId() == R.id.clear_btn) {
            searchEditText.setText("");
        } else {
            if (searchEditText.getText().length() > 0) {
                searchEditText.setText("");
            } else {
                slide_layout.openLeftMenu(true);
                back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            }
        }
    }

    @OnTextChanged(R.id.et_search)
    void onTextChanged(Editable text) {
        if (text.length() > 0) {
            slide_layout.setDragEnable(false);
            if (adapter == null) {
                adapter = new SuggestionsArrayAdapter(this, Collections.<Event>emptyList());
                searchSuggestionListView.setAdapter(adapter);
            }
            searchOrClear.setImageResource(R.drawable.ic_clear_black_24dp);
            back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            currentFragment = new SimpleEventListFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, currentFragment, TAG_SEARCH)
                    .commit();
            if (text.toString().trim().length() > 0) {
                predictSuggestions();
            }
        } else {
            slide_layout.setDragEnable(true);
            adapter = null;
            searchSuggestionListView.setAdapter(null);
            searchEditText.clearFocus();
            searchOrClear.setImageResource(R.drawable.ic_search_black_24dp);
            back.setImageResource(R.drawable.ic_menu_black_24dp);
            com.evented.utils.ViewUtils.hideViews(suggestionPane);
            currentFragment = homeScreenEventListFragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, currentFragment, TAG_EVENT_LIST)
                    .commit();
        }
    }

    long lastPosted = 0;

    private void predictSuggestions() {
        final long uptimeMillis = SystemClock.uptimeMillis();
        if (uptimeMillis - lastPosted < 1500) {
            return;
        }
        lastPosted = uptimeMillis;

        searchEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                String text = searchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    RealmResults<Event> results = realm.where(Event.class)
                            .beginsWith(Event.FIELD_NAME, text, Case.INSENSITIVE)
                            .findAll();
                    if (results.isEmpty()) {
                        com.evented.utils.ViewUtils.hideViews(suggestionPane);
                        return;
                    }
                    com.evented.utils.ViewUtils.showViews(suggestionPane);
                    int min = Math.min(results.size(), 5);
                    List<Event> searchSuggestion = new ArrayList<>(min);

                    for (int i = 0; i < min; i++) {
                        searchSuggestion.add(realm.copyFromRealm(results.get(i)));
                    }
                    if (adapter != null) {
                        adapter.refill(searchSuggestion);
                    }
                }
            }
        }, 750);
    }


    @OnEditorAction(R.id.et_search)
    boolean onEditorAction() {
        ViewUtils.hideViews(suggestionPane);
        searchEditText.clearFocus();
        if (searchEditText.getText().toString().length() > 0) {
            doSearch();
        }
        return true;
    }

    private void doSearch() {
        RealmResults<Event> events = realm.where(Event.class)
                .beginsWith(Event.FIELD_NAME, searchEditText.getText().toString().trim(), Case.INSENSITIVE)
                .findAllSorted(Event.FIELD_NAME);
        if (currentFragment instanceof SimpleEventListFragment) {
            ((SimpleEventListFragment) currentFragment).updateResults(events);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == homeScreenEventListFragment) {
            if (slide_layout.getState() == 0) {
                super.onBackPressed();
            } else {
                slide_layout.closeLeftMenu(true);
            }
        } else {
            searchEditText.setText("");
        }
    }

    @OnItemClick(R.id.search_suggestion_list_view)
    void onSuggestionItemClicked(AdapterView<?> adapterView, View view, int position, long id) {
        Event event = (Event) adapterView.getItemAtPosition(position);
        Intent intent = new Intent(view.getContext(), EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event.getName());
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.getEventId());
        startActivity(intent);
    }

    @OnItemClick(R.id.side_menu_list)
    void onMenuItemClick(int position) {
        if (position == 0) {
            startActivity(new Intent(this, TrendingEventsActivity.class));
        } else if (position == 3) {
            Intent intent = new Intent(this, MyEventListActivity.class);
            startActivity(intent);
        } else if (position == 1) {
            startActivity(new Intent(this, NearbyEvents.class));
        } else if (position == 2) {
            startActivity(new Intent(this, FavoriteEventsActivity.class));
        } else if (position == 5) {
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
        }
        slide_layout.closeLeftMenu(true);
    }

    @Override
    public void onItemClicked(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event.getName());
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.getEventId());
        startActivity(intent);
    }

    @OnClick(R.id.log_out)
    void logout() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Logging out");
        dialog.setCancelable(false);
        dialog.show();
        UserManager.getInstance()
                .logout(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        GenericUtils.showDialog(MainActivity.this, throwable.getMessage());
                    }
                });
    }

}
