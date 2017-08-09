package com.evented.events.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.evented.R;
import com.evented.ui.RecyclerViewBaseAdapter;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by yaaminu on 8/9/17.
 */

class HomeScreenListItemHolder extends RecyclerViewBaseAdapter.Holder {
    @BindView(R.id.tv_title)
    TextView title;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    public HomeScreenListItemHolder(View v) {
        super(v);
    }

    @OnClick({R.id.see_all, R.id.tv_title})
    void onItemClicked() {
        itemView.performClick();
    }

    @OnLongClick({R.id.see_all, R.id.tv_title})
    boolean onItemLongClicked() {
        return itemView.performLongClick();
    }
}
