package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.evented.R;
import com.evented.events.data.User;
import com.evented.events.data.UserManager;
import com.evented.utils.Config;

/**
 * Created by yaaminu on 8/13/17.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    int stage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserManager.getInstance().isCurrentUserVerified()) {
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_login);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginIntroFragment())
                    .commit();
        }
    }

    public void onLoggedIn(User user) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new VerificationFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.login_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_skip)
                .setVisible(!Config.isManagement());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_skip) {
            Intent intent = new Intent(this, LauncherActivity.class);
            intent.putExtra(LauncherActivity.EXTRA_SKIP_LOGIN, true);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Fragment next() {
        final UserManager instance = UserManager.getInstance();
        if (instance.getCurrentUser() == null) {
            return new LoginFragment();
        } else if (!instance.isCurrentUserVerified()) {
            return new LoginFragment();
        } else {
            throw new AssertionError();
        }
    }

    public void onAddPhone() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, next())
                .commit();
    }

    public void onVerified() {
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeNumber() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, next())
                .commit();
    }
}
