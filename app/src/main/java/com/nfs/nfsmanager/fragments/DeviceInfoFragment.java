package com.nfs.nfsmanager.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.adapters.DeviceInfoAdapter;
import com.nfs.nfsmanager.utils.DeviceInfo;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 */
public class DeviceInfoFragment extends Fragment {

    private MaterialTextView mProgress, mCharging, mVoltage, mTotal, mAwake, mDeepSleep;
    private CircularProgressIndicator mProgressBarBattery;
    private final long mMemTotal = DeviceInfo.getItemMb("MemTotal"), mSwapTotal = DeviceInfo.getItemMb("SwapTotal"),
            mSwapFree = DeviceInfo.getItemMb("SwapFree"), mMemUsed = (DeviceInfo.getItemMb("Cached") +
            DeviceInfo.getItemMb("MemFree"));

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_device, container, false);

        LinearLayout mLinearLayout = mRootView.findViewById(R.id.layout_main);
        mLinearLayout.setOrientation(Utils.getOrientation(requireActivity()) == Configuration.ORIENTATION_LANDSCAPE ?
                LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);

        MaterialCardView mNFS_Info = mRootView.findViewById(R.id.nfs_info);
        mProgress = mRootView.findViewById(R.id.percent);
        mCharging = mRootView.findViewById(R.id.charge_rate);
        mVoltage = mRootView.findViewById(R.id.voltage);
        MaterialTextView mHealth = mRootView.findViewById(R.id.health);
        MaterialTextView mTotalRAM = mRootView.findViewById(R.id.total_ram);
        MaterialTextView mUsedRAM = mRootView.findViewById(R.id.used_ram);
        MaterialTextView mTotalSwap = mRootView.findViewById(R.id.total_swap);
        MaterialTextView mUsedSwap = mRootView.findViewById(R.id.used_swap);
        MaterialTextView mCPUTitle = mRootView.findViewById(R.id.cpu_title);
        MaterialTextView mDeviceTitle = mRootView.findViewById(R.id.device_title);
        mTotal = mRootView.findViewById(R.id.total);
        mAwake = mRootView.findViewById(R.id.awake);
        mDeepSleep = mRootView.findViewById(R.id.deep_sleep);
        mProgressBarBattery = mRootView.findViewById(R.id.progress_circle_battery);
        CircularProgressIndicator mProgressBarRAM = mRootView.findViewById(R.id.progress_circle_ram);
        CircularProgressIndicator mProgressBarSwap = mRootView.findViewById(R.id.progress_circle_swap);
        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        mProgress.setText(DeviceInfo.getBatteryLevel() + "%");
        mCharging.setText(getString(R.string.charge_rate, DeviceInfo.getChargingStatus()));
        mVoltage.setText(getString(R.string.voltage, DeviceInfo.getBatteryVoltage()));
        mTotal.setText(getString(R.string.total_time, DeviceInfo.getTotalUpTime()));
        mAwake.setText(getString(R.string.awake_time, DeviceInfo.getAwakeTime()));
        mDeepSleep.setText(getString(R.string.deep_sleep_time, DeviceInfo.getDeepSleepTime()));
        mProgressBarBattery.setProgress(DeviceInfo.getBatteryLevel());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DeviceInfoAdapter mRecycleViewAdapter = new DeviceInfoAdapter(DeviceInfo.getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mProgressBarRAM.setMax((int) mMemTotal);
        mProgressBarSwap.setMax((int) mMemTotal);

        mTotalRAM.setText(mMemTotal + " MB");
        mUsedRAM.setText((mMemTotal - mMemUsed) + " MB");
        mTotalSwap.setText(mSwapTotal + " MB");
        mUsedSwap.setText(((mSwapTotal - mSwapFree)) + " MB");
        mProgressBarRAM.setProgress((int) (mMemTotal - mMemUsed));
        mProgressBarSwap.setProgress((int) (mSwapTotal - mSwapFree));

        mCPUTitle.setText(getString(R.string.nfs_injector) + " " + NFS.getReleaseStatus());
        mDeviceTitle.setText(DeviceInfo.getModel());
        mHealth.setText(getString(R.string.health, Utils.read("/sys/class/power_supply/battery/health")));
        if (!NFS.isNFSParent()) {
            mNFS_Info.setVisibility(View.GONE);
        }

        refreshStatus(requireActivity());

        return mRootView;
    }

    private void refreshStatus(Activity activity) {
        new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(2000);
                        activity.runOnUiThread(() -> {
                            try {
                                mProgress.setText(DeviceInfo.getBatteryLevel() + "%");
                                mCharging.setText(getString(R.string.charge_rate, DeviceInfo.getChargingStatus()));
                                mVoltage.setText(getString(R.string.voltage, DeviceInfo.getBatteryVoltage()));
                                mTotal.setText(getString(R.string.total_time, DeviceInfo.getTotalUpTime()));
                                mAwake.setText(getString(R.string.awake_time, DeviceInfo.getAwakeTime()));
                                mDeepSleep.setText(getString(R.string.deep_sleep_time, DeviceInfo.getDeepSleepTime()));
                                mProgressBarBattery.setProgress(DeviceInfo.getBatteryLevel());
                            } catch (IllegalStateException ignored) {}
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        }.start();
    }

}