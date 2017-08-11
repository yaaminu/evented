package com.evented.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.evented.R;

/**
 * Created by yaaminu on 8/11/17.
 */

public class TicketsListActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "eventId";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);
        Fragment fragment = new TicketsListFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, "ticketList")
                .commit();
    }
}
