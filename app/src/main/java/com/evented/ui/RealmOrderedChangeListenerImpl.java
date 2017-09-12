package com.evented.ui;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;

/**
 * Created by yaaminu on 9/12/17.
 */

public class RealmOrderedChangeListenerImpl implements
        OrderedRealmCollectionChangeListener {

    protected RecyclerViewBaseAdapter<?, ? extends RecyclerViewBaseAdapter.Holder> getAdapter() {
        throw new UnsupportedOperationException("must implement this method");
    }

    @Override
    public void onChange(Object results, OrderedCollectionChangeSet changeSet) {
        RecyclerViewBaseAdapter<?, ?> adapter = getAdapter();
        if (changeSet == null) {
            adapter.notifyDataChanged();
        } else {
            for (OrderedCollectionChangeSet.Range range : changeSet.getInsertionRanges()) {
                adapter.notifyItemRangeInserted(range.startIndex, range.length);
            }
            for (OrderedCollectionChangeSet.Range range : changeSet.getChangeRanges()) {
                adapter.notifyItemRangeChanged(range.startIndex, range.length);
            }
            for (OrderedCollectionChangeSet.Range range : changeSet.getDeletionRanges()) {
                adapter.notifyItemRangeRemoved(range.startIndex, range.length);
            }
        }
    }
}
