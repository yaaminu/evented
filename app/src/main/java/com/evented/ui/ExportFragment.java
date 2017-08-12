package com.evented.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.Ticket;
import com.evented.utils.Config;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.evented.utils.ThreadUtils;
import com.evented.utils.ViewUtils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 8/12/17.
 */

public class ExportFragment extends BaseFragment {
    static final String EXTRA_OPEN_AFTER_EXPORT = "openAfterExport";

    @Nullable
    ExportTask exportTask;
    private String eventId;
    boolean openAfterExport;
    @BindView(R.id.cancel_export)
    Button cancel_export;

    @BindView(R.id.status)
    TextView exportStatus;


    @Override
    protected int getLayout() {
        return R.layout.fragment_export_tickets;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = getArguments().getString(ExportTicketsActivity.EXTRA_EVENT_ID);
        openAfterExport = getArguments().getBoolean(EXTRA_OPEN_AFTER_EXPORT);
        GenericUtils.ensureNotNull(eventId);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beginExport();
    }

    @OnClick(R.id.cancel_export)
    void onClick() {
        if (exportTask != null) {
            exportTask.cancel(true);
        }
    }

    private void beginExport() {
        ThreadUtils.ensureMain();
        if (exportTask == null) {
            exportTask = new ExportTask(this);
            exportTask.execute(eventId);
        }
    }

    private static class ExportTask extends AsyncTask<String, String, String> {

        private static final String TAG = "ExportTask";
        @Nullable
        private ExportFragment exportFragment;

        Exception error;

        public ExportTask(@NonNull ExportFragment exportFragment) {
            this.exportFragment = exportFragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (exportFragment != null) {
                ViewUtils.showViews(exportFragment.cancel_export);
            }
            onProgressUpdate("0%");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (this.exportFragment != null) {
                this.exportFragment.exportStatus.setText(exportFragment
                        .getString(R.string.export_progress, values[0]));
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            // TODO: 8/12/17 actually produce the spreadsheet
            String eventId = strings[0];
            Realm realm = Realm.getDefaultInstance();
            String fileName = "fileName.txt";
            FileOutputStream outputStream = null;
            try {
                RealmResults<Ticket> tickets = realm.where(Ticket.class)
                        .equalTo(Ticket.FIELD_EVENT_ID, eventId)
                        .findAllSorted(Ticket.FIELD_TICKET_NUMBER);
                outputStream = new FileOutputStream(new File(Config.getAppBinFilesBaseDir(), fileName));
                for (int i = 0; i < tickets.size(); i++) {
                    if (isCancelled()) {
                        throw new IOException("cancelled");
                    }
                    publishProgress(String.valueOf(((i + 1) * 100) / tickets.size()));
                    Thread.sleep(2000);
                    PLog.d(TAG, "%s has benn processed", tickets.get(i));
                }
            } catch (IOException | InterruptedException e) {
                error = e;
                return null;
            } finally {
                IOUtils.closeQuietly(outputStream);
                realm.close();
            }
            return fileName;
        }

        @Override
        protected void onCancelled() {
            onPostExecute("");
        }

        @Override
        protected void onPostExecute(String s) {
            if (exportFragment != null) {
                if (error != null) {
                    exportFragment.exportStatus.setText(error.getMessage() == null ? "An error occured" : error.getMessage());
                } else {
                    exportFragment.exportStatus.setText(GenericUtils.getString(R.string.exproted_success_message, s));
                }
                ViewUtils.hideViews(exportFragment.cancel_export);
                exportFragment.exportTask = null;
            }

        }
    }

    @Override
    public boolean onBackPressed() {
        if (exportTask != null) {
            new AlertDialog.Builder(getContext())
                    .setMessage("Export in progress, Do want to cancel it?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (exportTask != null) {
                                exportTask.exportFragment = null;
                                exportTask.cancel(true);
                                exportTask = null;
                                getActivity().finish();
                            }
                        }
                    }).setNegativeButton(android.R.string.cancel, null)
                    .create().show();
            return true;
        }
        return false;
    }
}
