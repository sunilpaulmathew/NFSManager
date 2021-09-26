package com.nfs.nfsmanager.utils.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.AsyncTasks;
import com.nfs.nfsmanager.utils.Common;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;
import com.nfs.nfsmanager.utils.activities.ApplyModeActivity;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2020
 */

public class DashBoardFragment extends Fragment {

    private final ArrayList <RecycleViewItem> mData = new ArrayList<>();
    private RecyclerView mRecyclerView;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mData.add(new RecycleViewItem(getString(R.string.battery), getResources().getDrawable(R.drawable.ic_battery)));
        mData.add(new RecycleViewItem(getString(R.string.balanced), getResources().getDrawable(R.drawable.ic_balanced)));
        mData.add(new RecycleViewItem(getString(R.string.ultra), getResources().getDrawable(R.drawable.ic_ultra)));
        mData.add(new RecycleViewItem(getString(R.string.gaming), getResources().getDrawable(R.drawable.ic_game)));

        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanCount(requireActivity())));
        RecycleViewAdapter mRecycleViewAdapter = new RecycleViewAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (position == 0) {
                showUpdateModeDialog(0, getString(R.string.battery));
            } else if (position == 1) {
                showUpdateModeDialog(1, getString(R.string.balanced));
            } else if (position == 2) {
                showUpdateModeDialog(2, getString(R.string.ultra));
            } else if (position == 3) {
                showUpdateModeDialog(3, getString(R.string.gaming));
            }
        });

        return mRootView;
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
                mRecyclerView.setAdapter(new RecycleViewAdapter(mData));
            }
        }.execute();
    }

    private void showUpdateModeDialog(int value, String message) {
        new MaterialAlertDialogBuilder(requireActivity())
                .setMessage(getString(R.string.mode_change_question, message))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .setPositiveButton(R.string.apply, (dialog, which) -> updateNFSMode(value, message)).show();
    }

    private int getSpanCount(Activity activity) {
        return Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;
    }

    private static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

        private final ArrayList<RecycleViewItem> data;

        private static ClickListener clickListener;

        public RecycleViewAdapter(ArrayList<RecycleViewItem> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_nfsmode, parent, false);
            return new ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
            try {
                holder.mTitle.setText(this.data.get(position).getTitle());
                holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
                if (NFS.getNFSMode() == 0) {
                    if (position == 0) {
                        holder.mLinearLayout.setBackgroundColor(Utils.getThemeAccentColor(holder.mLinearLayout.getContext()));
                    } else {
                        holder.mLinearLayout.setBackgroundColor(Utils.getCardBackground(holder.mLinearLayout.getContext()));
                    }
                } else if (NFS.getNFSMode() == 1) {
                    if (position == 1) {
                        holder.mLinearLayout.setBackgroundColor(Utils.getThemeAccentColor(holder.mLinearLayout.getContext()));
                    } else {
                        holder.mLinearLayout.setBackgroundColor(Utils.getCardBackground(holder.mLinearLayout.getContext()));
                    }
                } else if (NFS.getNFSMode() == 2) {
                    if (position == 2) {
                        holder.mLinearLayout.setBackgroundColor(Utils.getThemeAccentColor(holder.mLinearLayout.getContext()));
                    } else {
                        holder.mLinearLayout.setBackgroundColor(Utils.getCardBackground(holder.mLinearLayout.getContext()));
                    }
                } else {
                    if (position == 3) {
                        holder.mLinearLayout.setBackgroundColor(Utils.getThemeAccentColor(holder.mLinearLayout.getContext()));
                    } else {
                        holder.mLinearLayout.setBackgroundColor(Utils.getCardBackground(holder.mLinearLayout.getContext()));
                    }
                }
            } catch (NullPointerException ignored) {}
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final AppCompatImageButton mIcon;
            private final MaterialTextView mTitle;
            private final LinearLayout mLinearLayout;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.mIcon = view.findViewById(R.id.icon);
                this.mTitle = view.findViewById(R.id.title);
                this.mLinearLayout = view.findViewById(R.id.rv_layout);
            }

            @Override
            public void onClick(View view) {
                clickListener.onItemClick(getAdapterPosition(), view);
            }
        }

        public void setOnItemClickListener(ClickListener clickListener) {
            RecycleViewAdapter.clickListener = clickListener;
        }

        public interface ClickListener {
            void onItemClick(int position, View v);
        }

    }

    private static class RecycleViewItem implements Serializable {
        private final String mTitle;
        private final Drawable mIcon;

        public RecycleViewItem(String title, Drawable icon) {
            this.mTitle = title;
            this.mIcon = icon;
        }

        public String getTitle() {
            return mTitle;
        }

        public Drawable getIcon() {
            return mIcon;
        }
    }

}