package com.nfs.nfsmanager.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

    public static String getStrings() {
        List<String> mData = new ArrayList<>();
        if (Utils.exist(Utils.getInternalDataStorage() + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.read(Utils.getInternalDataStorage() + "/strings.xml")).split("\\r?\\n")) {
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                linearLayout.setVisibility(View.VISIBLE);
                materialTextView.setText(activity.getString(R.string.downloading, "..."));
            }
            @Override
            protected Void doInBackground(Void... voids) {
                if (!Utils.exist(Utils.getInternalDataStorage() + "/strings.xml") && !Utils.isNetworkUnavailable(activity)) {
                    Utils.download(Utils.getInternalDataStorage() + "/strings.xml", url);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent translator = new Intent(activity, TranslatorActivity.class);
                activity.startActivity(translator);
                activity.finish();
                linearLayout.setVisibility(View.GONE);
            }
        }.execute();
    }

}