package com.evented.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.utils.GenericUtils;

import static com.evented.ui.ExportFragment.EXTRA_OPEN_AFTER_EXPORT;

/**
 * Created by yaaminu on 8/12/17.
 */

public class ExportTicketsActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "eventId";
    private String eventId;

    BaseFragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        GenericUtils.ensureNotNull(eventId);
        currentFragment = new ExportIntroFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNext(boolean openAfterExport) {
        currentFragment = new ExportFragment();
        Bundle bundle = new Bundle(2);
        bundle.putBoolean(EXTRA_OPEN_AFTER_EXPORT, openAfterExport);
        bundle.putString(EXTRA_EVENT_ID, eventId);
        currentFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (currentFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
