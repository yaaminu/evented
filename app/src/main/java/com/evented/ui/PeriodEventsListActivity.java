package com.evented.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.evented.R;
import com.evented.utils.GenericUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yaaminu on 8/9/17.
 */

public class PeriodEventsListActivity extends AppCompatActivity {
    public static final String EXTRA_OLDEST = "oldest";
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.tab_layout)
    TabLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periodic_events_screen);
        ButterKnife.bind(this);
        long oldest = getIntent().getLongExtra(EXTRA_OLDEST, 0);
        GenericUtils.assertThat(oldest > 0, "invalid oldest value passed");
        pager.setAdapter(new PagerAdapterImpl(getSupportFragmentManager(), oldest,
                getResources().getStringArray(R.array.event_cagories)));
        layout.setupWithViewPager(pager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class PagerAdapterImpl extends FragmentStatePagerAdapter {

        private final String[] titles;
        private final long oldest;

        public PagerAdapterImpl(FragmentManager fm, long oldest, String[] titles) {
            super(fm);
            this.titles = titles;
            this.oldest = oldest;
        }

        @Override
        public Fragment getItem(int position) {
            return EventsListFragment.create(oldest, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }
}
