package com.nfs.nfsmanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.nfs.nfsmanager.utils.activities.FlashingActivity;

import java.io.File;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 05, 2020
 */

public class Flasher {

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
        Common.getFlashingResult().append("** Extracting ").append(Common.getZipName()).append(" into working folder: ");
        Utils.runAndGetError(Utils.magiskBusyBox() + " unzip " + mZipPath + " -d '" + FLASH_FOLDER + "'");
        if (Utils.exist(mScriptPath)) {
            Common.getFlashingResult().append(" Done *\n\n");
            Common.getFlashingResult().append("** Checking Module: ");
            if (Objects.requireNonNull(Utils.read(FLASH_FOLDER + "/module.prop")).contains("name=NFS INJECTOR @nfsinjector")) {
                Common.isModuleInvalid(false);
                Common.getFlashingResult().append(" Done *\n\n");
                Utils.runCommand("cd '" + FLASH_FOLDER + "'");
                Common.getFlashingResult().append("** Flashing ").append(Common.getZipName()).append(" ...\n\n");
                Utils.runAndGetLiveOutput(flashingCommand, Common.getFlashingOutput());
                Common.getFlashingResult().append(Utils.getOutput(Common.getFlashingOutput()).endsWith("\nsuccess") ? Utils.getOutput(Common.getFlashingOutput())
                        .replace("\nsuccess","") : "** Flashing Failed *");
            } else {
                Common.isModuleInvalid(true);
                Common.getFlashingOutput().clear();
                Common.getFlashingResult().append(" Invalid *\n\n");
            }
        } else {
            Common.getFlashingResult().append(" Failed *\n\n");
            Common.getFlashingResult().append("** Flashing Failed *");
        }
        Utils.runCommand(CLEANING_COMMAND);
    }

    public static void flashModule(File file, Activity activity) {
        new AsyncTasks() {

            @Override
            public void onPreExecute() {
                Common.setZipName(file.getName());
                Common.getFlashingResult().setLength(0);
                Common.getFlashingOutput().clear();
                Common.isFlashing(true);
                Intent flashing = new Intent(activity, FlashingActivity.class);
                activity.startActivity(flashing);
            }

            @Override
            public void doInBackground() {
                Common.getFlashingResult().append("** Preparing to flash ").append(file.getName()).append("...\n\n");
                Common.getFlashingResult().append("** Path: '").append(file).append("'\n\n");
                // Delete if an old zip file exists
                Utils.delete(activity.getCacheDir() + "/flash.zip");
                Common.getFlashingResult().append("** Copying '").append(file.getName()).append("' into temporary folder: ");
                Common.getFlashingResult().append(Utils.runAndGetError("cp '" + file + "' " + activity.getCacheDir() + "/flash.zip"));
                Common.getFlashingResult().append(Utils.exist(activity.getCacheDir() + "/flash.zip") ? "Done *\n\n" : "\n\n");
                flashModule(activity);
            }

            @Override
            public void onPostExecute() {
                Utils.delete(activity.getCacheDir() + "/flash.zip");
                Common.isFlashing(false);
            }
        }.execute();
    }

}