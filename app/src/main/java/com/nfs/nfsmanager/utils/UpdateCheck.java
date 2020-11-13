package com.nfs.nfsmanager.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 07, 2020
 */

public class UpdateCheck {

    private static final String LATEST_VERSION_URL = "https://raw.githubusercontent.com/sunilpaulmathew/NFSManager/master/app/src/main/assets/release.json";
    private static final String LATEST_APK = Utils.getInternalDataStorage() + "/com.nfs.nfsmanager.apk";

    private static void prepareInternalStorage() {
        File file = new File(Utils.getInternalDataStorage());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    private static void getVersionInfo(Context context) {
        prepareInternalStorage();
        Utils.download(releaseInfo(context), LATEST_VERSION_URL);
    }

    private static void getLatestApp(Context context) {
        prepareInternalStorage();
        Utils.download(LATEST_APK, getLatestApk(context));
    }

    private static String versionName(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("versionName"));
        } catch (JSONException e) {
            return BuildConfig.VERSION_NAME;
        }
    }

    private static int versionCode(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getInt("versionCode"));
        } catch (JSONException e) {
            return BuildConfig.VERSION_CODE;
        }
    }

    private static String getLatestApk(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("downloadUrl"));
        } catch (JSONException e) {
            return "";
        }
    }

    private static String getChangelogs(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("releaseNotes"));
        } catch (JSONException e) {
            return "";
        }
    }

    private static String getChecksum(Context context) {
        try {
            JSONObject obj = new JSONObject(getReleaseInfo(context));
            return (obj.getString("sha1"));
        } catch (JSONException e) {
            return null;
        }
    }

    private static boolean hasVersionInfo(Context context) {
        return Utils.exist(releaseInfo(context));
    }

    private static long lastModified(Context context) {
        return new File(releaseInfo(context)).lastModified();
    }

    private static void updateAvailableDialog(Context context) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.update_available, UpdateCheck.versionName(context)))
                .setMessage(UpdateCheck.getChangelogs(context))
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> {
                })
                .setPositiveButton(context.getString(R.string.get_it), (dialog, id) -> {
                    updaterTask(context);
                })
                .show();
    }

    private static void updaterTask(Context context) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;
            @SuppressLint("SetTextI18n")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.downloading) + "...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                getLatestApp(context);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    mProgressDialog.dismiss();
                } catch (IllegalArgumentException ignored) {
                }
                if (Utils.exist(LATEST_APK) && Utils.getChecksum(LATEST_APK).contains(Objects.requireNonNull(getChecksum(context)))) {
                    installUpdate(LATEST_APK, context);
                } else {
                    new MaterialAlertDialogBuilder(context)
                            .setMessage(R.string.download_failed)
                            .setPositiveButton(R.string.cancel, (dialog, which) -> {
                            }).show();
                }
            }
        }.execute();
    }

    public static void autoUpdateCheck(Context context) {
        if (!Utils.checkWriteStoragePermission(context)) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            return;
        }
        if (Utils.isNetworkUnavailable(context)) {
            return;
        }
        if (!Utils.exist("/system/bin/curl") && !Utils.exist("/system/bin/wget")) {
            return;
        }
        if (!UpdateCheck.hasVersionInfo(context) || (UpdateCheck.lastModified(context) + 3720000L < System.currentTimeMillis())) {
            UpdateCheck.getVersionInfo(context);
        }
        if (UpdateCheck.hasVersionInfo(context) && BuildConfig.VERSION_CODE < UpdateCheck.versionCode(context)) {
            UpdateCheck.updateAvailableDialog(context);
        }
    }

    private static String releaseInfo(Context context) {
        return context.getFilesDir().getPath() + "/release";
    }

    private static String getReleaseInfo(Context context) {
        return Utils.read(releaseInfo(context));
    }

    public static void manualUpdateCheck(Context context) {
        UpdateCheck.getVersionInfo(context);
        if (UpdateCheck.hasVersionInfo(context) && BuildConfig.VERSION_CODE < UpdateCheck.versionCode(context)) {
            UpdateCheck.updateAvailableDialog(context);
        } else {
            new MaterialAlertDialogBuilder(context)
                    .setMessage(R.string.update_unavailable)
                    .setPositiveButton(context.getString(R.string.cancel), (dialog, id) -> {
                    })
                    .show();
        }
    }

    public static void installUpdate(String path, Context context) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uriFile;
        uriFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                new File(path));
        intent.setDataAndType(uriFile, "application/vnd.android.package-archive");
        context.startActivity(Intent.createChooser(intent, ""));
    }

}