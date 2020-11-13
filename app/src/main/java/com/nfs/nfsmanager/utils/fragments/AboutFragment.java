package com.nfs.nfsmanager.utils.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.UpdateCheck;
import com.nfs.nfsmanager.utils.Utils;
import com.nfs.nfsmanager.utils.activities.ChangeLogActivity;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2020
 */

public class AboutFragment extends Fragment {

    private ArrayList <RecycleViewItem> mData = new ArrayList<>();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_about, container, false);

        mData.add(new RecycleViewItem(getString(R.string.app_name), BuildConfig.VERSION_NAME, getResources().getDrawable(R.mipmap.ic_launcher_round), null));
        mData.add(new RecycleViewItem(getString(R.string.nfs_injector), NFS.getReleaseStatus(), getResources().getDrawable(R.drawable.ic_info), null));
        mData.add(new RecycleViewItem(getString(R.string.support), getString(R.string.support_summary), getResources().getDrawable(R.drawable.ic_support), "https://t.me/nfsinjector"));
        mData.add(new RecycleViewItem(getString(R.string.faq), getString(R.string.faq_summary), getResources().getDrawable(R.drawable.ic_help), "https://telegra.ph/NFS-Injector-Frequently-Asked-Questions-02-14"));
        mData.add(new RecycleViewItem(getString(R.string.source_code), getString(R.string.source_code_summary), getResources().getDrawable(R.drawable.ic_github), "https://github.com/sunilpaulmathew/NFSManager"));
        mData.add(new RecycleViewItem(getString(R.string.report_issue), getString(R.string.report_issue_summary), getResources().getDrawable(R.drawable.ic_bug), "https://github.com/sunilpaulmathew/NFSManager/issues/new"));
        mData.add(new RecycleViewItem(getString(R.string.change_logs), getString(R.string.change_logs_summary), getResources().getDrawable(R.drawable.ic_active), null));
        mData.add(new RecycleViewItem(getString(R.string.more_apps), getString(R.string.more_apps_summary), getResources().getDrawable(R.drawable.ic_playstore), "https://play.google.com/store/apps/dev?id=5836199813143882901"));
        mData.add(new RecycleViewItem(getString(R.string.update_check), getString(R.string.update_check_summary), getResources().getDrawable(R.drawable.ic_update), null));

        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanCount(requireActivity())));
        RecycleViewAdapter mRecycleViewAdapter = new RecycleViewAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);

        return mRootView;
    }

    private int getSpanCount(Activity activity) {
        return Utils.isTablet(activity) ? Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ?
                4 : 3 : Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
    }

    private static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

        private ArrayList<RecycleViewItem> data;

        public RecycleViewAdapter(ArrayList<RecycleViewItem> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_about, parent, false);
            return new RecycleViewAdapter.ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
            holder.mTitle.setText(this.data.get(position).getTitle());
            if (Utils.isDarkTheme(holder.mTitle.getContext())) {
                holder.mTitle.setTextColor(Utils.getThemeAccentColor(holder.mTitle.getContext()));
            }
            holder.mDescription.setText(this.data.get(position).getDescription());
            holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
            holder.mRVLayout.setOnClickListener(v -> {
                if (position == 0) {
                    Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                    settings.setData(uri);
                    holder.mRVLayout.getContext().startActivity(settings);
                } else if (position == 6) {
                    Intent changeLogs = new Intent(holder.mRVLayout.getContext(), ChangeLogActivity.class);
                    holder.mRVLayout.getContext().startActivity(changeLogs);
                } else if (position == 8) {
                    if (Utils.checkWriteStoragePermission(holder.mRVLayout.getContext())) {
                        UpdateCheck.manualUpdateCheck(holder.mRVLayout.getContext());
                    } else {
                        ActivityCompat.requestPermissions((Activity) holder.mRVLayout.getContext(), new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }
                } else if (this.data.get(position).getURL() != null) {
                    Utils.launchUrl(holder.mRVLayout, this.data.get(position).getURL(), holder.mRVLayout.getContext());
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageButton mIcon;
            private AppCompatTextView mTitle;
            private AppCompatTextView mDescription;
            private LinearLayout mRVLayout;

            public ViewHolder(View view) {
                super(view);
                this.mIcon = view.findViewById(R.id.icon);
                this.mTitle = view.findViewById(R.id.title);
                this.mDescription = view.findViewById(R.id.description);
                this.mRVLayout = view.findViewById(R.id.rv_about);
            }
        }
    }

    private static class RecycleViewItem implements Serializable {
        private String mTitle;
        private String mDescription;
        private Drawable mIcon;
        private String mURL;

        public RecycleViewItem(String title, String description, Drawable icon, String url) {
            this.mTitle = title;
            this.mDescription = description;
            this.mIcon = icon;
            this.mURL = url;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getDescription() {
            return mDescription;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public String getURL() {
            return mURL;
        }
    }

}