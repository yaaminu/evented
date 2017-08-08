package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.evented.evented.R;
import com.evented.events.ui.CreateEventActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by yaaminu on 8/7/17.
 */

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fab_create_event)
    View fab;

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

    @OnTouch(R.id.empty_view)
    boolean onTouch() {
        Toast.makeText(this, "touched", Toast.LENGTH_LONG).show();
        return false;
    }
}
