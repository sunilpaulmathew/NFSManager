package com.nfs.nfsmanager.utils.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;

import java.io.File;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2020
 */

public class MagiskLogFragment extends Fragment {

    private LinearLayout mProgressLayout;
    private AppCompatTextView mProgressMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_log_view, container, false);

        mProgressLayout = mRootView.findViewById(R.id.progress_layout);
        mProgressMessage = mRootView.findViewById(R.id.progress_text);
        AppCompatTextView mLogText = mRootView.findViewById(R.id.log_text);
        FloatingActionButton mSave = mRootView.findViewById(R.id.save_button);

        mLogText.setText(NFS.readMagiskLog());
        mSave.setOnClickListener(v -> exportMagiskLog(requireActivity()));
        return mRootView;
    }

    @SuppressLint("StaticFieldLeak")
    public void exportMagiskLog(Context context) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressLayout.setVisibility(View.VISIBLE);
                mProgressMessage.setText(context.getString(R.string.exporting, Utils.getInternalDataStorage() + "/magisk.log") + "...");
            }
            @Override
            protected Void doInBackground(Void... voids) {
                NFS.makeAppFolder();
                Utils.runCommand("sleep 2");
                Utils.copy("/cache/magisk.log", Utils.getInternalDataStorage() + "/magisk.log");
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgressLayout.setVisibility(View.GONE);
                if (Utils.exist(Utils.getInternalDataStorage() + "/magisk.log")) {
                    new AlertDialog.Builder(context)
                            .setMessage(context.getString(R.string.export_completed, Utils.getInternalDataStorage()))
                            .setCancelable(false)
                            .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(context.getString(R.string.share), (dialog, id) -> {
                                Uri uriFile = FileProvider.getUriForFile(context,
                                        BuildConfig.APPLICATION_ID + ".provider", new File(Utils.getInternalDataStorage() + "/magisk.log"));
                                Intent shareScript = new Intent(Intent.ACTION_SEND);
                                shareScript.setType("text/x-log");
                                shareScript.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_by, context.getString(R.string.magisk_log)));
                                shareScript.putExtra(Intent.EXTRA_STREAM, uriFile);
                                shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                context.startActivity(Intent.createChooser(shareScript, context.getString(R.string.share_with)));
                            })
                            .show();
                }
            }
        }.execute();
    }

}