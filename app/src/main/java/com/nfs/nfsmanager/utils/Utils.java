package com.nfs.nfsmanager.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.ShellUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 07, 2020
 */
public class Utils {

    static {
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
    }

    /*
     * The following code is partly taken from https://github.com/SmartPack/SmartPack-Kernel-Manager
     * Ref: https://github.com/SmartPack/SmartPack-Kernel-Manager/blob/beta/app/src/main/java/com/smartpack/kernelmanager/utils/root/RootUtils.java
     */
    public static boolean rootAccess() {
        return Shell.rootAccess();
    }

    public static void runCommand(String command) {
        Shell.cmd(command).exec();
    }

    public static void runAndGetLiveOutput(String command, List<String> output) {
        Shell.cmd(command).to(output).exec();
    }

    @NonNull
    public static String runAndGetOutput(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            List<String> outputs = Shell.cmd(command).exec().getOut();
            if (ShellUtils.isValidOutput(outputs)) {
                for (String output : outputs) {
                    sb.append(output).append("\n");
                }
            }
            return removeSuffix(sb.toString()).trim();
        } catch (Exception e) {
            return "";
        }
    }

    @NonNull
    public static String runAndGetError(String command) {
        StringBuilder sb = new StringBuilder();
        List<String> outputs = new ArrayList<>();
        List<String> stderr = new ArrayList<>();
        try {
            Shell.cmd(command).to(outputs, stderr).exec();
            outputs.addAll(stderr);
            if (ShellUtils.isValidOutput(outputs)) {
                for (String output : outputs) {
                    sb.append(output).append("\n");
                }
            }
            return removeSuffix(sb.toString()).trim();
        } catch (Exception e) {
            return "";
        }
    }

    private static String removeSuffix(@Nullable String s) {
        if (s != null && s.endsWith("\n")) {
            return s.substring(0, s.length() - "\n".length());
        }
        return s;
    }

    @NonNull
    public static String getOutput(List<String> output) {
        try {
            List<String> mData = new ArrayList<>();
            Collections.addAll(mData, output.toString().substring(1, output.toString().length() - 1).replace(", ", "\n").split("\\r?\\n"));
            return mData.toString().substring(1, mData.toString().length() - 1).replace(", ", "\n");
        } catch (ConcurrentModificationException ignored) {
        }
        return "";
    }

    /*
     * The following code is partly taken from https://github.com/Grarak/KernelAdiutor
     * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/utils/Prefs.java
     */
    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaults);
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, value).apply();
    }

    /*
     * The following code is partly taken from https://github.com/Grarak/KernelAdiutor
     * Ref: https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/utils/Utils.java
     */

    public static boolean isDarkTheme(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static File getInternalDataStorage(Context context) {
        return context.getExternalFilesDir("");
    }

    static boolean isAppInstalled(String appID, Context context) {
        try {
            context.getPackageManager().getApplicationInfo(appID, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }

    public static String read(String file) {
        return runAndGetOutput("cat '" + file + "'");
    }

    public static boolean exist(String file) {
        String output = runAndGetOutput("[ -e " + file + " ] && echo true");
        return !output.isEmpty() && output.equals("true");
    }

    public static void download(String path, String url) {
        /*
         * Based on the following stackoverflow discussion
         * Ref: https://stackoverflow.com/questions/15758856/android-how-to-download-file-from-webserver
         */
        try (InputStream input = new URL(url).openStream();
             OutputStream output = new FileOutputStream(path)) {
            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (Exception ignored) {
        }
    }

    public static String magiskBusyBox() {
        if (Utils.exist("/data/adb/magisk/busybox")) {
            return "/data/adb/magisk/busybox";
        } else {
            return null;
        }
    }

    public static void create(String text, String path) {
        runCommand("echo '" + text + "' > " + path);
    }

    public static void delete(String path) {
        if (exist(path)) {
            runCommand(magiskBusyBox() + " rm -r " + path);
        }
    }

    public static void mkdir(String path) {
        runCommand(magiskBusyBox() + " mkdir " + path);
    }

    static String getChecksum(String path) {
        return runAndGetOutput("sha1sum " + path);
    }

    public static int strToInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    static void applyValue(String value, String path) {
        Utils.runCommand("echo '" + value + "' > '" + path + "'");
    }

    public static void copy(String source, String dest) {
        Utils.runCommand("cp " + source + " " + dest);
    }

    public static void longSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.dismiss, v -> snackbar.dismiss());
        snackbar.show();
    }

    public static void indefiniteSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.dismiss, v -> snackbar.dismiss());
        snackbar.show();
    }

    public static void launchUrl(String url, Context context) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getOrientation(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode() ?
                Configuration.ORIENTATION_PORTRAIT : activity.getResources().getConfiguration().orientation;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean isNotificationAccessDenied(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED;
    }

    public static String readAssetFile(Context context, String file) {
        InputStream input = null;
        BufferedReader buf = null;
        try {
            StringBuilder s = new StringBuilder();
            input = context.getAssets().open(file);
            buf = new BufferedReader(new InputStreamReader(input));

            String str;
            while ((str = buf.readLine()) != null) {
                s.append(str).append("\n");
            }
            return s.toString().trim();
        } catch (IOException ignored) {
        } finally {
            try {
                if (input != null) input.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String rebootCommand() {
        return "am broadcast android.intent.action.ACTION_SHUTDOWN " + "&&" +
                " sync " + "&&" +
                " echo 3 > /proc/sys/vm/drop_caches " + "&&" +
                " sync " + "&&" +
                " sleep 3 " + "&&" +
                " reboot";
    }

    public static void reboot(String string, LinearLayout linearLayout, MaterialTextView textView, Context context) {
        new AsyncTasks() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onPreExecute() {
                linearLayout.setVisibility(View.VISIBLE);
                textView.setText(context.getString(R.string.rebooting) + "...");
            }

            @Override
            public void doInBackground() {
                Utils.runCommand(rebootCommand() + string);
            }

            @Override
            public void onPostExecute() {
                linearLayout.setVisibility(View.GONE);
            }
        }.execute();
    }

}