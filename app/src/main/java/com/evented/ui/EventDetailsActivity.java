package com.evented.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.evented.R;
import com.evented.events.ui.EventDetailsFragment;
import com.evented.utils.GenericUtils;

public class EventDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "eventId",
            EXTRA_EVENT_NAME = "eventName";

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setContentView(R.layout.activity_event_details);
        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        GenericUtils.ensureNotNull(eventId);

        android.support.v4.app.Fragment fragment = new EventDetailsFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
