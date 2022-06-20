package com.nfs.nfsmanager.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.AsyncTasks;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;

import java.io.File;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2020
 */

public class NFSLogFragment extends Fragment {

    private LinearLayout mProgressLayout;
    private MaterialTextView mProgressMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_log_view, container, false);

        mProgressLayout = mRootView.findViewById(R.id.progress_layout);
        mProgressMessage = mRootView.findViewById(R.id.progress_text);
        MaterialTextView mLogText = mRootView.findViewById(R.id.log_text);
        FloatingActionButton mSave = mRootView.findViewById(R.id.save_button);

        mLogText.setText(NFS.readNFSLog());
        mSave.setOnClickListener(v -> exportNFSLog(requireActivity()));
        return mRootView;
    }

    @SuppressLint("StaticFieldLeak")
    public void exportNFSLog(Context context) {
        new AsyncTasks() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onPreExecute() {
                mProgressLayout.setVisibility(View.VISIBLE);
                mProgressMessage.setText(context.getString(R.string.exporting, new File(context.getExternalFilesDir("logs"),"nfs.log")) + "...");
            }

            @Override
            public void doInBackground() {
                Utils.runCommand("sleep 2");
                Utils.copy("/data/NFS/nfs.log", new File(context.getExternalFilesDir("logs"),"nfs.log").getAbsolutePath());
            }

            @Override
            public void onPostExecute() {
                mProgressLayout.setVisibility(View.GONE);
                if (Utils.exist(new File(context.getExternalFilesDir("logs"),"nfs.log").getAbsolutePath())) {
                    new MaterialAlertDialogBuilder(context)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage(context.getString(R.string.export_completed, context.getExternalFilesDir("logs")))
                            .setCancelable(false)
                            .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                            })
                            .setPositiveButton(context.getString(R.string.share), (dialog, id) -> {
                                Uri uriFile = FileProvider.getUriForFile(context,
                                        BuildConfig.APPLICATION_ID + ".provider", new File(context.getExternalFilesDir("logs"),"nfs.log"));
                                Intent shareScript = new Intent(Intent.ACTION_SEND);
                                shareScript.setType("text/x-log");
                                shareScript.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_by, context.getString(R.string.nfs_log)));
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