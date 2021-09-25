package com.nfs.nfsmanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.activities.TranslatorActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 * Adapted from The Translator (Ref: https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator)
 */

public class Translator {

    public static String mSearchText;

    public static String getStrings(Context context) {
        List<String> mData = new ArrayList<>();
        if (Utils.exist(Utils.getInternalDataStorage(context) + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.read(Utils.getInternalDataStorage(context) + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    mData.add(line);
                }
            }
        }
        return "<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n<!--Created by NFS Manager-->\n\n" +
                mData.toString().replace("[","").replace("]","").replace(",","\n") + "\n</resources>";
    }

    public static boolean checkIllegalCharacters(String string) {
        String[] array = string.trim().split("\\s+");
        for (String s : array) {
            if (!s.matches("&gt;|&lt;|&amp;") && s.startsWith("&")
                    || s.startsWith("<") && s.length() == 1 || s.startsWith(">") && s.length() == 1
                    || s.startsWith("<b") && s.length() <= 3 || s.startsWith("</") && s.length() <= 4
                    || s.startsWith("<i") && s.length() <= 3 || s.startsWith("\"") || s.startsWith("'"))
                return true;
        }
        return false;
    }

    public static void importTransaltions(String url, LinearLayout linearLayout, MaterialTextView materialTextView, Activity activity) {
        new AsyncTasks() {

            @Override
            public void onPreExecute() {
                linearLayout.setVisibility(View.VISIBLE);
                materialTextView.setText(activity.getString(R.string.downloading, "..."));
            }

            @Override
            public void doInBackground() {
                if (!Utils.exist(Utils.getInternalDataStorage(activity) + "/strings.xml") && !Utils.isNetworkUnavailable(activity)) {
                    Utils.download(Utils.getInternalDataStorage(activity) + "/strings.xml", url);
                }
            }

            @Override
            public void onPostExecute() {
                Intent translator = new Intent(activity, TranslatorActivity.class);
                activity.startActivity(translator);
                activity.finish();
                linearLayout.setVisibility(View.GONE);
            }
        }.execute();
    }

}