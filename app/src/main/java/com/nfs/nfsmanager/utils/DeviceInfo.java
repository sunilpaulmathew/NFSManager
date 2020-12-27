package com.nfs.nfsmanager.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 */

public class DeviceInfo {

    private static final String BATTERY = "/sys/class/power_supply/battery";
    private static final String CHARGING_STATUS = BATTERY + "/status";
    private static final String LEVEL = BATTERY + "/capacity";
    private static final String VOLTAGE = BATTERY + "/voltage_now";
    private static final String CHARGE_RATE = BATTERY + "/current_now";

    private static String getString(String prefix) {
        try {
            for (String line : Objects.requireNonNull(Utils.read("/proc/cpuinfo")).split("\\r?\\n")) {
                if (line.startsWith(prefix)) {
                    return line.split(":")[1].trim();
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getBatteryVoltage() {
        int voltage = parseInt(VOLTAGE);
        return String.valueOf(voltage / 1000);
    }

    public static String getChargingStatus() {
        int chargingrate = parseInt(CHARGE_RATE);
        if (chargingrate > 10000) {
            return String.valueOf(chargingrate / 1000);
        } else if (chargingrate < -10000) {
            return String.valueOf(chargingrate / -1000);
        } else if (chargingrate < 0 && chargingrate > -1000) {
            return String.valueOf(chargingrate * -1);
        } else {
            return String.valueOf(chargingrate);
        }
    }

    public static String getTotalUpTime() {
        return getDurationBreakdown(SystemClock.elapsedRealtime());
    }

    public static String getAwakeTime() {
        return DeviceInfo.getDurationBreakdown(SystemClock.uptimeMillis());
    }

    public static String getDeepSleepTime() {
        return DeviceInfo.getDurationBreakdown(SystemClock.elapsedRealtime() - SystemClock.uptimeMillis());
    }

    public static int getBatteryLevel() {
        return parseInt(LEVEL);
    }

    public static long getItemMb(String prefix) {
        try {
            return Long.parseLong(getItem(prefix).replaceAll("[^\\d]", "")) / 1024L;
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static String getItem(String prefix) {
        try {
            for (String line : Objects.requireNonNull(Utils.read("/proc/meminfo")).split("\\r?\\n")) {
                if (line.startsWith(prefix)) {
                    return line.split(":")[1].trim();
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    @SuppressLint("DefaultLocale")
    public static String getDurationBreakdown(long millis) {
        StringBuilder sb = new StringBuilder(64);
        if(millis <= 0)
        {
            sb.append("00 min 00 s");
            return sb.toString();
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 0) {
            sb.append(days);
            sb.append(" day ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(" hr ");
        }
        sb.append(String.format("%02d", minutes));
        sb.append(" min ");
        sb.append(String.format("%02d", seconds));
        sb.append(" s");
        return sb.toString();
    }

    private static int parseInt(String path) {
        return Utils.strToInt(Utils.read(path));
    }

    public static List<String> getData() {
        List<String> mData = new ArrayList<>();
        if (getDeviceInfo("** ID NFS : ") != null) {
            mData.add("NFS ID: " + getDeviceInfo("** ID NFS : "));
        }
        if (getDeviceInfo("** ID Phone : ") != null) {
            mData.add("Phone ID: " + getDeviceInfo("** ID Phone : "));
        }
        if (getDeviceInfo("** Rom : ") != null) {
            mData.add("ROM: " + getDeviceInfo("** Rom : "));
        }
        if (getDeviceInfo("** Kernel : ") != null) {
            mData.add("Kernel: " + getDeviceInfo("** Kernel : "));
        }
        if (getDeviceInfo("** Busybox Environment : ") != null) {
            mData.add("BusyBox: " + getDeviceInfo("** Busybox Environment : "));
        }
        if (getDeviceInfo("** Aarch : ") != null) {
            mData.add("ARCH: " + getDeviceInfo("** Aarch : "));
        }
        if (getDeviceInfo("** Android : ") != null) {
            mData.add("Android: " + getDeviceInfo("** Android : "));
        }
        if (getDeviceInfo("** Sdk : ") != null) {
            mData.add("SDK: " + getDeviceInfo("** Sdk : "));
        }
        return mData;
    }

    private static String getDeviceInfo(String string) {
        try {
            for (String line : Objects.requireNonNull(Utils.read("/data/NFS/nfs.log")).split("\\r?\\n")) {
                if (line.startsWith(string)) {
                    return line.replace(string, "").replace(" *","");
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

}