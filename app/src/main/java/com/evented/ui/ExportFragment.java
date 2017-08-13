package com.evented.ui;

import android.annotation.SuppressLint;
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
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.Ticket;
import com.evented.utils.Config;
import com.evented.utils.FileUtils;
import com.evented.utils.GenericUtils;
import com.evented.utils.ThreadUtils;
import com.evented.utils.TicketCsvUtils;
import com.evented.utils.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;

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

    @Nullable
    private String path;


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
        } else if (path != null) {
            if (!FileUtils.open(getContext(), path)) {
                GenericUtils.showDialog(getContext(), getString(R.string.no_app_for_view_file_type));
            }
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
            Event event = realm.where(Event.class)
                    .equalTo(Event.FIELD_EVENT_ID, eventId)
                    .findFirst();
            String fileName = event.getName() + "-ticket-report.csv";
            try {
                RealmResults<Ticket> tickets = realm.where(Ticket.class)
                        .equalTo(Ticket.FIELD_EVENT_ID, eventId)
                        .findAllSorted(Ticket.FIELD_TICKET_NUMBER);
                return new TicketCsvUtils().generateCsv(tickets, new File(Config.getAppBinFilesBaseDir(),
                        fileName).getAbsolutePath()).getAbsolutePath();
            } catch (IOException e) {
                if (!(e instanceof InterruptedIOException)) {
                    error = e;
                }
                return null;
            } finally {
                realm.close();
            }
        }

        @Override
        protected void onCancelled() {
            onPostExecute("");
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String path) {
            if (exportFragment != null && exportFragment.getActivity() != null) {
                if (error != null) {
                    exportFragment.exportStatus.setText(error.getMessage() == null ? "An error occured" : error.getMessage());
                } else {
                    if (exportFragment.openAfterExport) {
                        if (!FileUtils.open(exportFragment.getContext(), path)) {
                            GenericUtils.showDialog(exportFragment.getContext(),
                                    GenericUtils.getString(R.string.no_app_for_view_file_type));
                        }
                    }
                    exportFragment.exportStatus.setText(GenericUtils.getString(R.string.exproted_success_message, path));
                    exportFragment.cancel_export.setText(R.string.open_file);
                    exportFragment.path = path;
                }
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
