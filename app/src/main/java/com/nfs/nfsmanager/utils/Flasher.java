package com.nfs.nfsmanager.utils;

import java.io.File;
import java.io.FileDescriptor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 05, 2020
 */

public class Flasher {

    private static final String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";
    private static final String CLEANING_COMMAND = Utils.magiskBusyBox() + " rm -r '" + FLASH_FOLDER + "'";
    private static final String ZIPFILE_EXTRACTED = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";

    public static String mZipName, mFlashingOutput = null;

    public static StringBuilder mFlashingResult = null;

    public static boolean mFlashing = false, mModuleInvalid = false;

    private static String mountRootFS(String command) {
        return Utils.magiskBusyBox() + " mount -o remount," + command + " /";
    }

    private static void prepareFolder(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static void flashModule() {
        /*
         * Flashing recovery zip without rebooting to custom recovery
         * Credits to osm0sis @ xda-developers.com
         */
        FileDescriptor fd = new FileDescriptor();
        int RECOVERY_API = 3;
        String path = "/data/local/tmp/flash.zip";
        String flashingCommand = Utils.magiskBusyBox() + " sh '" + ZIPFILE_EXTRACTED + "' '" + RECOVERY_API + "' '" +
                fd + "' '" + path + "'";
        if (Utils.exist(FLASH_FOLDER)) {
            Utils.runCommand(CLEANING_COMMAND);
        } else {
            prepareFolder(FLASH_FOLDER);
        }
        mFlashingResult.append("** Extracting ").append(mZipName).append(" into working folder: ");
        Utils.runAndGetError(Utils.magiskBusyBox() + " unzip " + path + " -d '" + FLASH_FOLDER + "'");
        if (Utils.exist(ZIPFILE_EXTRACTED)) {
            mFlashingResult.append(" Done *\n\n");
            mFlashingResult.append("** Checking Module: ");
            if (Utils.read(FLASH_FOLDER + "/module.prop").contains("name=NFS INJECTOR @nfsinjector")) {
                mModuleInvalid = false;
                mFlashingResult.append(" Done *\n\n");
                mFlashingResult.append("** Preparing a recovery-like environment for flashing...\n");
                Utils.runCommand("cd '" + FLASH_FOLDER + "'");
                mFlashingResult.append(Utils.runAndGetError(mountRootFS("rw"))).append(" \n");
                mFlashingResult.append(Utils.runAndGetError(Utils.magiskBusyBox() + " mkdir /tmp")).append(" \n");
                mFlashingResult.append(Utils.runAndGetError(Utils.magiskBusyBox() + " mke2fs -F tmp.ext4 500000")).append(" \n");
                mFlashingResult.append(Utils.runAndGetError(Utils.magiskBusyBox() + " mount -o loop tmp.ext4 /tmp/")).append(" \n\n");
                mFlashingResult.append("** Flashing ").append(mZipName).append(" ...\n\n");
                mFlashingOutput = Utils.runAndGetOutput(flashingCommand);
                mFlashingResult.append(mFlashingOutput);
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
    }

}