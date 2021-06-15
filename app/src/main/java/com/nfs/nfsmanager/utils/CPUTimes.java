package com.nfs.nfsmanager.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 */

public class CPUTimes {

    private static final List<String> mData = new ArrayList<>();

    public static List<String> getData(String path) {
        mData.clear();
        Collections.addAll(mData, Objects.requireNonNull(Utils.read(path)).split("\\r?\\n"));
        mData.add("Deep sleep");
        return mData;
    }

    public static String sToString(long tSec) {
        int h = (int) tSec / 60 / 60;
        int m = (int) tSec / 60 % 60;
        int s = (int) tSec % 60;
        String sDur;
        sDur = h + ":";
        if (m < 10) sDur += "0";
        sDur += m + ":";
        if (s < 10) sDur += "0";
        sDur += s;

        return sDur;
    }

    public static boolean supported(String path) {
        return getData(path).size() > 2;
    }

}