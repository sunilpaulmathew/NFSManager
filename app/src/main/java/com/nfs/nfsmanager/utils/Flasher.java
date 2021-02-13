package com.nfs.nfsmanager.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.nfs.nfsmanager.utils.activities.FlashingActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 05, 2020
 */

public class Flasher {

    public static String mZipName;

    public static StringBuilder mFlashingResult = null;

    public static List<String> mFlashingOutput = null;

    public static boolean mFlashing = false, mModuleInvalid = false;

    private static void prepareFolder(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    private static void flashModule(Context context) {
        /*
         * Flashing recovery zip without rebooting to custom recovery (Credits to osm0sis @ xda-developers.com)
         * Also include code from https://github.com/topjohnwu/Magisk/
         * Ref: https://github.com/topjohnwu/Magisk/blob/a848f10bba4f840248ecf314f7c9d55511d05a0f/app/src/main/java/com/topjohnwu/magisk/core/tasks/FlashZip.kt#L47
         */
        String FLASH_FOLDER = Utils.getInternalDataStorage(context) + "/flash";
        String CLEANING_COMMAND = "rm -r '" + FLASH_FOLDER + "'";
        String mScriptPath = Utils.getInternalDataStorage(context) + "/flash/META-INF/com/google/android/update-binary";
        String mZipPath = context.getCacheDir() + "/flash.zip";
        String flashingCommand = "BOOTMODE=true sh " + mScriptPath + " dummy 1 " + mZipPath + " && echo success";
        if (Utils.exist(FLASH_FOLDER)) {
            Utils.runCommand(CLEANING_COMMAND);
        } else {
            prepareFolder(FLASH_FOLDER);
        }
        mFlashingResult.append("** Extracting ").append(mZipName).append(" into working folder: ");
        Utils.runAndGetError(Utils.magiskBusyBox() + " unzip " + mZipPath + " -d '" + FLASH_FOLDER + "'");
        if (Utils.exist(mScriptPath)) {
            mFlashingResult.append(" Done *\n\n");
            mFlashingResult.append("** Checking Module: ");
            if (Objects.requireNonNull(Utils.read(FLASH_FOLDER + "/module.prop")).contains("name=NFS INJECTOR @nfsinjector")) {
                mModuleInvalid = false;
                mFlashingResult.append(" Done *\n\n");
                Utils.runCommand("cd '" + FLASH_FOLDER + "'");
                mFlashingResult.append("** Flashing ").append(mZipName).append(" ...\n\n");
                Utils.runAndGetLiveOutput(flashingCommand, mFlashingOutput);
                mFlashingResult.append(Utils.getOutput(mFlashingOutput).endsWith("\nsuccess") ? Utils.getOutput(mFlashingOutput)
                        .replace("\nsuccess","") : "** Flashing Failed *");
            } else {
                mModuleInvalid = true;
                mFlashingOutput = null;
                mFlashingResult.append(" Invalid *\n\n");
            }
        } else {
            mFlashingResult.append(" Failed *\n\n");
            mFlashingResult.append("** Flashing Failed *");
        }
        Utils.runCommand(CLEANING_COMMAND);
    }

    public static void flashModule(File file, Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mZipName = file.getName();
                mFlashingResult = new StringBuilder();
                mFlashingOutput = new ArrayList<>();
                mFlashing = true;
                Intent flashing = new Intent(activity, FlashingActivity.class);
                activity.startActivity(flashing);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                mFlashingResult.append("** Preparing to flash ").append(file.getName()).append("...\n\n");
                mFlashingResult.append("** Path: '").append(file.toString()).append("'\n\n");
                // Delete if an old zip file exists
                Utils.delete(activity.getCacheDir() + "/flash.zip");
                mFlashingResult.append("** Copying '").append(file.getName()).append("' into temporary folder: ");
                mFlashingResult.append(Utils.runAndGetError("cp '" + file.toString() + "' " + activity.getCacheDir() + "/flash.zip"));
                mFlashingResult.append(Utils.exist(activity.getCacheDir() + "/flash.zip") ? "Done *\n\n" : "\n\n");
                flashModule(activity);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Utils.delete(activity.getCacheDir() + "/flash.zip");
                mFlashing = false;
            }
        }.execute();
    }

}