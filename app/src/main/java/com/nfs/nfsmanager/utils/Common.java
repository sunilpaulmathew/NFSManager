package com.nfs.nfsmanager.utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 15, 2020
 */

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.nfs.nfsmanager.adapters.CPUTimesAdapter;

import java.util.ArrayList;
import java.util.List;

public class Common {

    private static boolean mApplying, mFlashing = false, mModuleInvalid = false;
    private static CPUTimesAdapter mRecycleViewAdapter;
    private static final List<String> mFlashingOutput = new ArrayList<>(), mOutput = new ArrayList<>();
    private static RecyclerView mRecyclerView;
    private static String mURL = null, mZipName;
    private static final StringBuilder mFlashingResult = new StringBuilder();

    public static boolean isApplying() {
        return mApplying;
    }

    public static boolean isFlashing() {
        return mFlashing;
    }

    public static boolean isModuleInvalid() {
        return mModuleInvalid;
    }

    public static CPUTimesAdapter getRecycleViewAdapter() {
        return mRecycleViewAdapter;
    }

    public static List<String> getFlashingOutput() {
        return mFlashingOutput;
    }

    public static List<String> getOutput() {
        return mOutput;
    }

    public static RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public static String getURL() {
        return mURL;
    }

    public static String getZipName() {
        return mZipName;
    }

    public static StringBuilder getFlashingResult() {
        return mFlashingResult;
    }

    public static void initializeRecycleViewAdapter(List<String> data) {
        mRecycleViewAdapter = new CPUTimesAdapter(data);
    }

    public static void initializeRecyclerView(View view, int id) {
        mRecyclerView = view.findViewById(id);
    }

    public static void isApplying(boolean b) {
        mApplying = b;
    }

    public static void isFlashing(boolean b) {
        mFlashing = b;
    }

    public static void isModuleInvalid(boolean b) {
        mModuleInvalid = b;
    }

    public static void setURL(String url) {
        mURL = url;
    }

    public static void setZipName(String zipName) {
        mZipName = zipName;
    }
}