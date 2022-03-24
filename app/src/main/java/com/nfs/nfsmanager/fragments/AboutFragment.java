package com.nfs.nfsmanager.fragments;

import android.app.Activity;
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

import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.adapters.AboutAdapter;
import com.nfs.nfsmanager.utils.SerializableItems;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.UpdateCheck;
import com.nfs.nfsmanager.utils.Utils;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2020
 */

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_about, container, false);

        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanCount(requireActivity())));
        AboutAdapter mRecycleViewAdapter = new AboutAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);

        return mRootView;
    }

    private ArrayList<SerializableItems> getData() {
        ArrayList<SerializableItems> mData = new ArrayList<>();
        mData.add(new SerializableItems(getString(R.string.app_name), BuildConfig.VERSION_NAME, ContextCompat.getDrawable(requireActivity(), R.mipmap.ic_launcher_round), null));
        mData.add(new SerializableItems(getString(R.string.nfs_injector), NFS.getReleaseStatus(), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_info), null));
        mData.add(new SerializableItems(getString(R.string.support), getString(R.string.support_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_support), "https://t.me/nfsinjector"));
        mData.add(new SerializableItems(getString(R.string.faq), getString(R.string.faq_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_help), "https://telegra.ph/NFS-Injector-Frequently-Asked-Questions-02-14"));
        mData.add(new SerializableItems(getString(R.string.source_code), getString(R.string.source_code_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_github), "https://github.com/sunilpaulmathew/NFSManager"));
        mData.add(new SerializableItems(getString(R.string.report_issue), getString(R.string.report_issue_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_bug), "https://github.com/sunilpaulmathew/NFSManager/issues/new"));
        mData.add(new SerializableItems(getString(R.string.change_logs), getString(R.string.change_logs_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_active), null));
        mData.add(new SerializableItems(getString(R.string.more_apps), getString(R.string.more_apps_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_playstore), "https://play.google.com/store/apps/dev?id=5836199813143882901"));
        mData.add(new SerializableItems(getString(R.string.privacy_policy), getString(R.string.privacy_policy_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_privacy), null));
        mData.add(new SerializableItems(getString(R.string.licence), getString(R.string.licence_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_licence), null));
        mData.add(new SerializableItems(getString(R.string.credits), getString(R.string.credits_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_contributors), null));
        mData.add(new SerializableItems(getString(R.string.donations), getString(R.string.donations_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_donate), null));
        if (UpdateCheck.isSignatureMatched(requireActivity())) {
            mData.add(new SerializableItems(getString(R.string.update_check), getString(R.string.update_check_summary), ContextCompat.getDrawable(requireActivity(), R.drawable.ic_update), null));
        }
        return mData;
    }

    private int getSpanCount(Activity activity) {
        return Utils.isTablet(activity) ? Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ?
                4 : 3 : Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
    }

}