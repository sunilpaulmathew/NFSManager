package com.nfs.nfsmanager.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.activities.ApplyModeActivity;
import com.nfs.nfsmanager.adapters.DashBoardAdapter;
import com.nfs.nfsmanager.utils.SerializableItems;
import com.nfs.nfsmanager.utils.AsyncTasks;
import com.nfs.nfsmanager.utils.Common;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2020
 */
public class DashBoardFragment extends Fragment {

    private RecyclerView mRecyclerView;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanCount(requireActivity())));
        DashBoardAdapter mRecycleViewAdapter = new DashBoardAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (position != NFS.getNFSMode()) {
                if (position == 0) {
                    showUpdateModeDialog(0, getString(R.string.battery));
                } else if (position == 1) {
                    showUpdateModeDialog(1, getString(R.string.balanced));
                } else if (position == 2) {
                    showUpdateModeDialog(2, getString(R.string.ultra));
                } else if (position == 3) {
                    showUpdateModeDialog(3, getString(R.string.gaming));
                }
            }
        });

        return mRootView;
    }

    private ArrayList<SerializableItems> getData() {
        ArrayList<SerializableItems> mData = new ArrayList<>();
        mData.add(new SerializableItems(getString(R.string.battery), null, ContextCompat.getDrawable(requireActivity(), R.drawable.ic_battery), null));
        mData.add(new SerializableItems(getString(R.string.balanced), null, ContextCompat.getDrawable(requireActivity(), R.drawable.ic_balanced), null));
        mData.add(new SerializableItems(getString(R.string.ultra), null, ContextCompat.getDrawable(requireActivity(), R.drawable.ic_ultra), null));
        mData.add(new SerializableItems(getString(R.string.gaming), null, ContextCompat.getDrawable(requireActivity(), R.drawable.ic_game), null));
        return mData;
    }

    @SuppressLint("StaticFieldLeak")
    private void updateNFSMode(int value, String message) {
        new AsyncTasks() {

            @Override
            public void onPreExecute() {
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                Common.isApplying(true);
                Common.getOutput().clear();
                Common.getOutput().add(getString(R.string.mode_message, message));
                Common.getOutput().add("================================================");
                Intent applyMode = new Intent(requireActivity(), ApplyModeActivity.class);
                startActivity(applyMode);
            }

            @Override
            public void doInBackground() {
                NFS.setNFSMode(value);
                Utils.runAndGetLiveOutput("sh /data/adb/modules/injector/service.sh", Common.getOutput());
            }

            @Override
            public void onPostExecute() {
                Common.getOutput().add("================================================");
                Common.getOutput().add(getString(R.string.mode_applied, message));
                Common.isApplying(false);
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                mRecyclerView.setAdapter(new DashBoardAdapter(getData()));
            }
        }.execute();
    }

    private void showUpdateModeDialog(int value, String message) {
        new MaterialAlertDialogBuilder(requireActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.mode_change_question, message))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .setPositiveButton(R.string.apply, (dialog, which) -> updateNFSMode(value, message)).show();
    }

    private int getSpanCount(Activity activity) {
        return Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;
    }

}