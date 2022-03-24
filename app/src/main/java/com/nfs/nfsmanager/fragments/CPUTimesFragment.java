package com.nfs.nfsmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.Common;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 */

public class CPUTimesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_cputime, container, false);

        Common.initializeRecyclerView(mRootView, R.id.recycler_view);
        Common.getRecyclerView().setLayoutManager(new LinearLayoutManager(requireActivity()));
        Common.getRecyclerView().setAdapter(Common.getRecycleViewAdapter());

        return mRootView;
    }

}