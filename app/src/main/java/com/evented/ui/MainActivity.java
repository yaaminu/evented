package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.evented.evented.R;
import com.evented.events.CreateEventActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yaaminu on 8/7/17.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.fab_create_event)
    void onClick() {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }
}
