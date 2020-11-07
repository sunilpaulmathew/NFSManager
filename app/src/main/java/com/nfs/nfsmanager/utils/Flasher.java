package com.nfs.nfsmanager.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileDescriptor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 05, 2020
 */

public class Flasher {

    private static final String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";
    private static final String CLEANING_COMMAND = "rm -r '" + FLASH_FOLDER + "'";
    private static final String ZIPFILE_EXTRACTED = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";

    public static String mZipName;

    public static StringBuilder mFlashingResult = null, mFlashingOutput = null;

    public static boolean mModuleInvalid = false, mManagerUpdateAvailable = false;

    private static int getCurrentVersion(Context context) {
        try {
            PackageInfo pm = context.getPackageManager().getPackageInfo("com.nfs.nfsmanager", 0);
            return pm.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return 0;
    }

    private static int getLatestVersion(String path, Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, 0);
        if (info == null) return 0;
        return info.versionCode;
    }

    private static String mountRootFS(String command) {
        return "mount -o remount," + command + " /";
    }

    private static void mount(String source, String dest) {
        Utils.runCommand("mount -o --bind" + " " + source + " " + dest);
    }

    private static boolean isUnzipAvailable() {
        return !Utils.runAndGetError("unzip --help").contains(
                "/system/bin/sh: unzip: inaccessible or not found");
    }

    private static String BusyBoxPath() {
        if (Utils.exist("/sbin/.magisk/busybox")) {
            return "/sbin/.magisk/busybox";
        } else if (Utils.exist("/sbin/.core/busybox")) {
            return "/sbin/.core/busybox";
        } else {
            return null;
        }
    }

    private static void prepareFolder(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static void flashModule(Context context) {
        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        FileDescriptor fd = new FileDescriptor();
        int RECOVERY_API = 3;
        String path = "/data/local/tmp/flash.zip";
        String flashingCommand = "sh '" + ZIPFILE_EXTRACTED + "' '" + RECOVERY_API + "' '" +
                fd + "' '" + path + "'";
        if (Utils.exist(FLASH_FOLDER)) {
            Utils.runCommand(CLEANING_COMMAND);
        } else {
            prepareFolder(FLASH_FOLDER);
        }
        mFlashingResult.append("** Checking for unzip binary: ");
        if (isUnzipAvailable()) {
            mFlashingResult.append("Available *\n\n");
        } else if (BusyBoxPath() != null) {
            mFlashingResult.append("Native Binary Unavailable *\nloop mounting BusyBox binaries to '/system/xbin' *\n\n");
            mount(BusyBoxPath(), Utils.exist("/system/xbin") ? "/system/xbin" : "/system/bin");
        } else {
            mFlashingResult.append("Unavailable *\n\n");
        }
        mFlashingResult.append("** Extracting ").append(mZipName).append(" into working folder: ");
        Utils.runAndGetError("unzip " + path + " -d '" + FLASH_FOLDER + "'");
        if (Utils.exist(ZIPFILE_EXTRACTED)) {
            mFlashingResult.append(" Done *\n\n");
            mFlashingResult.append(" Checking Module: ");
            if (Utils.read(FLASH_FOLDER + "/module.prop").contains("name=NFS INJECTOR @nfsinjector")) {
                mFlashingResult.append(" Done *\n\n");
                mFlashingResult.append("** Preparing a recovery-like environment for flashing...\n");
                Utils.runCommand("cd '" + FLASH_FOLDER + "'");
                mFlashingResult.append(Utils.runAndGetError(mountRootFS("rw"))).append(" \n");
                mFlashingResult.append(Utils.runAndGetError("mkdir /tmp")).append(" \n");
                mFlashingResult.append(Utils.runAndGetError("mke2fs -F tmp.ext4 500000")).append(" \n");
                mFlashingResult.append(Utils.runAndGetError("mount -o loop tmp.ext4 /tmp/")).append(" \n\n");
                mFlashingResult.append("** Flashing ").append(mZipName).append(" ...\n\n");
                if (mFlashingOutput == null) {
                    mFlashingOutput = new StringBuilder();
                } else {
                    mFlashingOutput.setLength(0);
                }
                mFlashingOutput.append(Utils.runAndGetOutput(flashingCommand));
                if (getCurrentVersion(context) < getLatestVersion(FLASH_FOLDER + "/com.nfs.nfsmanager.apk", context)) {
                    Utils.runCommand("mv " + FLASH_FOLDER + "/com.nfs.nfsmanager.apk " + Utils.getInternalDataStorage() + "/com.nfs.nfsmanager.apk");
                    mManagerUpdateAvailable = true;
                }
                mFlashingResult.append(mFlashingOutput.toString());
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
        Utils.delete("/data/local/tmp/flash.zip");
        Utils.runCommand(mountRootFS("ro"));
        Utils.create(mFlashingResult.toString(), Utils.getInternalDataStorage() + "/flasher_log-" +
                mZipName.replace(".zip",""));
    }

}