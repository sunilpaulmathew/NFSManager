package com.nfs.nfsmanager.utils.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2020
 */

public class NFSFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_nfs_settings, container, false);

        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        if (NFS.supported()) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), getSpanCount(requireActivity())));
            mRecyclerView.setAdapter(new RecycleViewAdapter(getData(requireContext())));
        }

        return mRootView;
    }

    private int getSpanCount(Activity activity) {
        return Utils.isTablet(activity) ? Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1 : 1;
    }

    private static ArrayList <NFSFragment.RecycleViewItems> getData(Context context) {
        ArrayList <RecycleViewItems> mData = new ArrayList<>();
        if (NFS.supported()) {
            if (NFS.getGOVs() != null) {
                mData.add(new RecycleViewItems(context.getString(R.string.governor), NFS.getGOV()));
            }
            if (NFS.getSchedulers() != null) {
                mData.add(new RecycleViewItems(context.getString(R.string.scheduler), NFS.getSched()));
            }
            if (NFS.hasDNSMode()) {
                mData.add(new RecycleViewItems(context.getString(R.string.dns), NFS.getDNSMode(context)));
            }
            if (NFS.hasSELinux()) {
                mData.add(new RecycleViewItems(context.getString(R.string.selinux), NFS.getSELinuxMode(context)));
            }
            if (NFS.hasSync()) {
                mData.add(new RecycleViewItems(context.getString(R.string.sync), NFS.getOnOff(context.getString(R.string.sync), context)));
            }
            if (NFS.hasTT()) {
                mData.add(new RecycleViewItems(context.getString(R.string.thermal_throttle), NFS.getOnOff(context.getString(R.string.thermal_throttle), context)));
            }
            if (NFS.availableTCPs() != null) {
                mData.add(new RecycleViewItems(context.getString(R.string.tcp), NFS.getTCP()));
            }
            if (NFS.hasDozeMode()) {
                mData.add(new RecycleViewItems(context.getString(R.string.doze), NFS.getDozeMode(context)));
            }
            if (NFS.hasShield()) {
                mData.add(new RecycleViewItems(context.getString(R.string.shield), NFS.getShield(context)));
            }
            if (NFS.hasAds()) {
                mData.add(new RecycleViewItems(context.getString(R.string.add_disabler), NFS.getAds(context)));
            }
            if (NFS.hasOW()) {
                mData.add(new RecycleViewItems(context.getString(R.string.overwatch_engine), NFS.getEnableDisable(context.getString(R.string.overwatch_engine), context)));
            }
            if (NFS.hasSF()) {
                mData.add(new RecycleViewItems(context.getString(R.string.super_sampling), NFS.getOnOff(context.getString(R.string.super_sampling), context)));
            }
            if (NFS.hasZygote()) {
                mData.add(new RecycleViewItems(context.getString(R.string.zygote), NFS.getEnableDisable(context.getString(R.string.zygote), context)));
            }
        }
        return mData;
    }

    public static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

        private ArrayList<RecycleViewItems> data;

        public RecycleViewAdapter(ArrayList<RecycleViewItems> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_nfssettings, parent, false);
            return new ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
            try {
                holder.mTitle.setText(this.data.get(position).getTitle());
                holder.mValue.setText(this.data.get(position).getDescription());
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private AppCompatTextView mTitle, mValue;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.mTitle = view.findViewById(R.id.title);
                this.mValue = view.findViewById(R.id.value);
            }

            @Override
            public void onClick(View view) {
                CharSequence mItem = this.mTitle.getText();
                if (mItem.equals(view.getContext().getString(R.string.governor))) {
                    NFS.setGOV(this.mValue, mValue, view.getContext());
                } else if (mItem.equals(view.getContext().getString(R.string.scheduler))) {
                    NFS.setScheduler(this.mValue, mValue);
                } else if (mItem.equals(view.getContext().getString(R.string.tcp))) {
                    NFS.setTCP(this.mValue, mValue);
                } else if (mItem.equals(view.getContext().getString(R.string.doze))) {
                    NFS.setDozeMode(this.mValue, mValue, view.getContext());
                } else if (mItem.equals(view.getContext().getString(R.string.shield))) {
                    NFS.setShield(this.mValue, mValue, view.getContext());
                } else if (mItem.equals(view.getContext().getString(R.string.add_disabler))) {
                    NFS.setAds(this.mValue, mValue, view.getContext());
                } else if (mItem.equals(view.getContext().getString(R.string.dns))) {
                    NFS.setDNSMode(this.mValue, mValue, view.getContext());
                } else if (mItem.equals(view.getContext().getString(R.string.selinux))) {
                    NFS.setSELinuxMode(this.mValue, mValue, view.getContext());
                } else if (mItem.equals(view.getContext().getString(R.string.overwatch_engine)) || mItem.equals(view.getContext().getString(R.string.zygote))) {
                    NFS.setEnableDisableMenu(this.mValue, mItem.toString(), mValue, view.getContext());
                } else if (mItem.equals(view.getContext().getString(R.string.thermal_throttle)) || mItem.equals(view.getContext().getString(R.string.sync))
                        || mItem.equals(view.getContext().getString(R.string.super_sampling))) {
                    NFS.setOnOffMenu(this.mValue, mItem.toString(), mValue, view.getContext());
                }
            }
        }

    }

    public static class RecycleViewItems implements Serializable {

        private String mTitle;
        private String mDescription;

        public RecycleViewItems(String title, String description) {
            this.mTitle = title;
            this.mDescription = description;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getDescription() {
            return mDescription;
        }
    }

}