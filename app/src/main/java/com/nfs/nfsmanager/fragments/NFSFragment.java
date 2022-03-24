package com.nfs.nfsmanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.adapters.NFSAdapter;
import com.nfs.nfsmanager.utils.SerializableItems;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;

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
            mRecyclerView.setAdapter(new NFSAdapter(getData(requireContext())));
        }

        return mRootView;
    }

    private int getSpanCount(Activity activity) {
        return Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
    }

    private static ArrayList <SerializableItems> getData(Context context) {
        ArrayList <SerializableItems> mData = new ArrayList<>();
        if (NFS.supported()) {
            if (NFS.getGOVs() != null) {
                mData.add(new SerializableItems(context.getString(R.string.governor), NFS.getGOV(), null, null));
            }
            if (NFS.getSchedulers() != null) {
                mData.add(new SerializableItems(context.getString(R.string.scheduler), NFS.getSched(), null, null));
            }
            if (NFS.hasDNSMode()) {
                mData.add(new SerializableItems(context.getString(R.string.dns), NFS.getDNSMode(context), null, null));
            }
            if (NFS.hasSELinux()) {
                mData.add(new SerializableItems(context.getString(R.string.selinux), NFS.getSELinuxMode(context), null, null));
            }
            if (NFS.hasSync()) {
                mData.add(new SerializableItems(context.getString(R.string.sync), NFS.getOnOff(context.getString(R.string.sync), context), null, null));
            }
            if (NFS.hasTT()) {
                mData.add(new SerializableItems(context.getString(R.string.thermal_throttle), NFS.getOnOff(context.getString(R.string.thermal_throttle), context), null, null));
            }
            if (NFS.availableTCPs() != null) {
                mData.add(new SerializableItems(context.getString(R.string.tcp), NFS.getTCP(), null, null));
            }
            if (NFS.hasDozeMode()) {
                mData.add(new SerializableItems(context.getString(R.string.doze), NFS.getDozeMode(context), null, null));
            }
            if (NFS.hasLMK()) {
                mData.add(new SerializableItems(context.getString(R.string.ram_level), NFS.getLMK(context), null, null));
            }
            if (NFS.hasShield()) {
                mData.add(new SerializableItems(context.getString(R.string.shield), NFS.getShield(context), null, null));
            }
            if (NFS.hasAds()) {
                mData.add(new SerializableItems(context.getString(R.string.add_disabler), NFS.getAds(context), null, null));
            }
            if (NFS.hasOW()) {
                mData.add(new SerializableItems(context.getString(R.string.overwatch_engine), NFS.getEnableDisable(context.getString(R.string.overwatch_engine), context), null, null));
            }
            if (NFS.hasSF()) {
                mData.add(new SerializableItems(context.getString(R.string.super_sampling), NFS.getOnOff(context.getString(R.string.super_sampling), context), null, null));
            }
            if (NFS.hasZygote()) {
                mData.add(new SerializableItems(context.getString(R.string.zygote), NFS.getEnableDisable(context.getString(R.string.zygote), context), null, null));
            }
        }
        return mData;
    }

}