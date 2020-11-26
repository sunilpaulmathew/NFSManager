package com.nfs.nfsmanager.utils;

import android.content.Context;

import java.io.File;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 05, 2020
 */

public class Flasher {

    public static String mZipName, mFlashingOutput = null;

    public static StringBuilder mFlashingResult = null;

    public static boolean mFlashing = false, mModuleInvalid = false;

    private static void prepareFolder(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static void flashModule(Context context) {
        /*
         * Flashing recovery zip without rebooting to custom recovery (Credits to osm0sis @ xda-developers.com)
         * Also include code from https://github.com/topjohnwu/Magisk/
         * Ref: https://github.com/topjohnwu/Magisk/blob/a848f10bba4f840248ecf314f7c9d55511d05a0f/app/src/main/java/com/topjohnwu/magisk/core/tasks/FlashZip.kt#L47
         */
        String FLASH_FOLDER = Utils.getInternalDataStorage() + "/flash";
        String CLEANING_COMMAND = "rm -r '" + FLASH_FOLDER + "'";
        String mScriptPath = Utils.getInternalDataStorage() + "/flash/META-INF/com/google/android/update-binary";
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
            if (Utils.read(FLASH_FOLDER + "/module.prop").contains("name=NFS INJECTOR @nfsinjector")) {
                mModuleInvalid = false;
                mFlashingResult.append(" Done *\n\n");
                Utils.runCommand("cd '" + FLASH_FOLDER + "'");
                mFlashingResult.append("** Flashing ").append(mZipName).append(" ...\n\n");
                mFlashingOutput = Utils.runAndGetOutput(flashingCommand);
                mFlashingResult.append(mFlashingOutput.replace("\nsuccess",""));
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

}