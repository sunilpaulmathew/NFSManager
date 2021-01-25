package com.nfs.nfsmanager.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 07, 2020
 */

public class NFS {

    private static final String MODULE_PARANT = "/data/adb/modules/injector";
    private static final String SCRIPT_PATH = MODULE_PARANT + "/service.sh";
    private static final String NFS_PARANT = "/data/NFS";
    private static final String MODE = NFS_PARANT + "/mode.txt";
    private static final String DOZE = NFS_PARANT + "/doze.txt";
    private static final String SHIELD = NFS_PARANT + "/shield.txt";
    private static final String DNS = NFS_PARANT + "/dns.txt";
    private static final String GOV = NFS_PARANT + "/governor.txt";
    private static final String LINUX = NFS_PARANT + "/linux.txt";
    private static final String SCHED = NFS_PARANT + "/scheduler.txt";
    private static final String ADS = NFS_PARANT + "/ads.txt";
    private static final String OW = NFS_PARANT + "/ow.txt";
    private static final String SYNC = NFS_PARANT + "/sync.txt";
    private static final String TCP = NFS_PARANT + "/tcp.txt";
    private static final String TT = NFS_PARANT + "/tt.txt";
    private static final String SF = NFS_PARANT + "/sf.txt";
    private static final String ZYGOTE = NFS_PARANT + "/zygote.txt";
    private static final String NFS_LOG = NFS_PARANT + "/nfs.log";

    private static final String MAGISK_LOG = "/cache/magisk.log";

    private static final String AVAILABLE_TCP = "/proc/sys/net/ipv4/tcp_available_congestion_control";

    private static final String AVAILABLE_GOV = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    private static final String AVAILABLE_GOV1 = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_available_governors";

    private static final String AVAILABLE_SCHED = "/sys/block/mmcblk0/queue/scheduler";
    private static final String AVAILABLE_SCHED1 = "/sys/block/sda/queue/scheduler";
    private static final String AVAILABLE_SCHED2 = "/sys/block/loop0/queue/scheduler";
    private static final String AVAILABLE_SCHED3 = "/sys/block/dm-0/queue/scheduler";

    public static List<String> mOutput = null;

    public static String mURL = null;

    private static int i;

    public static boolean mApplying;

    public static void makeAppFolder() {
        File file = new File(Utils.getInternalDataStorage());
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        file.mkdirs();
    }

    public static String getReleaseStatus() {
        try {
            for (String line : Utils.read(MODULE_PARANT + "/module.prop").split("\\r?\\n")) {
                if (line.startsWith("status=")) {
                    return line.replace("status=", "");
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private static List<String> DozeMode(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.doze_disable));
        list.add(context.getString(R.string.doze_default));
        list.add(context.getString(R.string.doze_custom));
        list.add(context.getString(R.string.doze_force));
        return list;
    }

    public static String getDozeMode(Context context) {
        if (getDozeMode() == 0) {
            return context.getString(R.string.doze_disable);
        } else if (getDozeMode() == 1) {
            return context.getString(R.string.doze_default);
        } else if (getDozeMode() == 2) {
            return context.getString(R.string.doze_custom);
        } else if (getDozeMode() == 3) {
            return context.getString(R.string.doze_force);
        } else {
            return context.getString(R.string.unknown);
        }
    }

    private static List<String> Shield(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.device_default));
        list.add(context.getString(R.string.custom_nfs));
        return list;
    }

    public static String getShield(Context context) {
        if (getShield() == 0) {
            return context.getString(R.string.device_default);
        } else if (getShield() == 1) {
            return context.getString(R.string.custom_nfs);
        } else {
            return context.getString(R.string.unknown);
        }
    }

    private static List<String> DNSMode(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.off));
        list.add(context.getString(R.string.dns_cloudfare));
        list.add(context.getString(R.string.dns_google));
        list.add(context.getString(R.string.dns_watch));
        list.add(context.getString(R.string.dns_neustar));
        return list;
    }

    public static String getDNSMode(Context context) {
        if (getDNSMode() == 0) {
            return context.getString(R.string.off);
        } else if (getDNSMode() == 1) {
            return context.getString(R.string.dns_cloudfare);
        } else if (getDNSMode() == 2) {
            return context.getString(R.string.dns_google);
        } else if (getDNSMode() == 3) {
            return context.getString(R.string.dns_watch);
        } else if (getDNSMode() == 4) {
            return context.getString(R.string.dns_neustar);
        } else {
            return context.getString(R.string.unknown);
        }
    }

    private static List<String> Ads(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.execute_no));
        list.add(context.getString(R.string.execute_disable));
        list.add(context.getString(R.string.execute_default));
        return list;
    }

    public static String getAds(Context context) {
        if (getAds() == 0) {
            return context.getString(R.string.execute_no);
        } else if (getAds() == 1) {
            return context.getString(R.string.execute_disable);
        } else if (getAds() == 2) {
            return context.getString(R.string.execute_default);
        } else {
            return context.getString(R.string.unknown);
        }
    }

    private static List<String> enableDisable(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.disable));
        list.add(context.getString(R.string.enable));
        return list;
    }

    public static String getEnableDisable(String string, Context context) {
        if (string.equals(context.getString(R.string.overwatch_engine))) {
            i = getOW();
        } else {
            i = getZygote();
        }
        if (i == 0) {
            return context.getString(R.string.disable);
        } else if (i == 1) {
            return context.getString(R.string.enable);
        } else {
            return context.getString(R.string.unknown);
        }
    }

    private static List<String> SELinux(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.permissive));
        list.add(context.getString(R.string.enforcing));
        return list;
    }

    public static String getSELinuxMode(Context context) {
        if (getSELinuxMode() == 0) {
            return context.getString(R.string.permissive);
        } else if (getSELinuxMode() == 1) {
            return context.getString(R.string.enforcing);
        } else {
            return context.getString(R.string.unknown);
        }
    }

    private static List<String> onOff(Context context) {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.off));
        list.add(context.getString(R.string.on));
        return list;
    }

    public static String getOnOff(String string, Context context) {
        if (string.equals(context.getString(R.string.thermal_throttle))) {
            i = getTT();
        } else if (string.equals(context.getString(R.string.sync))) {
            i = getSync();
        } else {
            i = getSF();
        }
        if (i == 0) {
            return context.getString(R.string.off);
        } else if (i == 1) {
            return context.getString(R.string.on);
        } else {
            return context.getString(R.string.unknown);
        }
    }

    public static String getGOVs() {
        if (Utils.exist(AVAILABLE_GOV)) {
            return Utils.read(AVAILABLE_GOV);
        } else if (Utils.exist(AVAILABLE_GOV1)) {
            return Utils.read(AVAILABLE_GOV1);
        }
        return null;
    }

    public static String getSchedulers() {
        if (Utils.exist(AVAILABLE_SCHED)) {
            return Objects.requireNonNull(Utils.read(AVAILABLE_SCHED)).replace("[", "").replace("]", "");
        } else if (Utils.exist(AVAILABLE_SCHED1)) {
            return Objects.requireNonNull(Utils.read(AVAILABLE_SCHED1)).replace("[", "").replace("]", "");
        } else if (Utils.exist(AVAILABLE_SCHED2)) {
            return Objects.requireNonNull(Utils.read(AVAILABLE_SCHED2)).replace("[", "").replace("]", "");
        } else if (Utils.exist(AVAILABLE_SCHED3)) {
            return Objects.requireNonNull(Utils.read(AVAILABLE_SCHED3)).replace("[", "").replace("]", "");
        }
        return null;
    }

    public static void setScheduler(AppCompatTextView textView, View view) {
        String[] mItem = Objects.requireNonNull(getSchedulers()).split(" ");
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    Utils.applyValue(mItem[i], SCHED);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static String availableTCPs() {
        try {
            return Utils.read(AVAILABLE_TCP);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void setTCP(AppCompatTextView textView, View view) {
        String[] mItem = Objects.requireNonNull(availableTCPs()).split(" ");
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    Utils.applyValue(mItem[i], TCP);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static String getGOV() {
        return Utils.read(GOV).toLowerCase();
    }

    public static String getSched() {
        return Utils.read(SCHED);
    }

    public static String getTCP() {
        return Utils.read(TCP);
    }

    public static void setNFSMode(int value) {
        Utils.applyValue(String.valueOf(value), MODE);
    }

    public static void setDozeMode(AppCompatTextView textView, View view, Context context) {
        String[] mItem = DozeMode(context).toArray(new String[0]);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    Utils.applyValue(String.valueOf(i), DOZE);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static void setShield(AppCompatTextView textView, View view, Context context) {
        String[] mItem = Shield(context).toArray(new String[0]);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    Utils.applyValue(String.valueOf(i), SHIELD);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static void setDNSMode(AppCompatTextView textView, View view, Context context) {
        String[] mItem = DNSMode(context).toArray(new String[0]);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    Utils.applyValue(String.valueOf(i), DNS);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static void setAds(AppCompatTextView textView, View view, Context context) {
        String[] mItem = Ads(context).toArray(new String[0]);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    Utils.applyValue(String.valueOf(i), ADS);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static void setSELinuxMode(AppCompatTextView textView, View view, Context context) {
        String[] mItem = SELinux(context).toArray(new String[0]);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    Utils.applyValue(String.valueOf(i), LINUX);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static void setOnOffMenu(AppCompatTextView textView, String title, View view, Context context) {
        String[] mItem = onOff(context).toArray(new String[0]);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    if (title.equals(view.getContext().getString(R.string.thermal_throttle))) {
                        Utils.applyValue(String.valueOf(i), TT);
                    } else if (title.equals(view.getContext().getString(R.string.sync))) {
                        Utils.applyValue(String.valueOf(i), SYNC);
                    } else if (title.equals(view.getContext().getString(R.string.super_sampling))) {
                        Utils.applyValue(String.valueOf(i), SF);
                    }
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    @SuppressLint("StringFormatInvalid")
    public static void setGOV(AppCompatTextView textView, View view, Context context) {
        String[] mItem = Objects.requireNonNull(getGOVs()).split(" ");
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    if (mItem[i].equals("performance")) {
                        new MaterialAlertDialogBuilder(context)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle(R.string.sure_question)
                                .setMessage(context.getString(R.string.performance_warning, getGOV()))
                                .setCancelable(false)
                                .setPositiveButton(context.getString(R.string.ok), (dialog, id) -> {
                                })
                                .show();
                    }
                    Utils.applyValue(mItem[i], GOV);
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static void setEnableDisableMenu(AppCompatTextView textView, String title, View view, Context context) {
        String[] mItem = enableDisable(context).toArray(new String[0]);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        Menu menu = popupMenu.getMenu();
        for (i = 0; i < mItem.length; i++) {
            menu.add(Menu.NONE, i, Menu.NONE, mItem[i]);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            for (i = 0; i < mItem.length; i++) {
                if (item.getItemId() == i) {
                    if (title.equals(view.getContext().getString(R.string.overwatch_engine))) {
                        Utils.applyValue(String.valueOf(i), OW);
                    } else if (title.equals(view.getContext().getString(R.string.zygote))) {
                        Utils.applyValue(String.valueOf(i), ZYGOTE);
                    }
                    textView.setText(mItem[i]);
                }
            }
            return false;
        });
        popupMenu.show();
    }

    public static int getAds() {
        return Utils.strToInt(Utils.read(ADS));
    }

    public static int getZygote() {
        return Utils.strToInt(Utils.read(ZYGOTE));
    }

    public static int getNFSMode() {
        return Utils.strToInt(Utils.read(MODE));
    }

    public static int getDozeMode() {
        return Utils.strToInt(Utils.read(DOZE));
    }

    public static int getShield() {
        return Utils.strToInt(Utils.read(SHIELD));
    }

    public static int getDNSMode() {
        return Utils.strToInt(Utils.read(DNS));
    }

    public static int getOW() {
        return Utils.strToInt(Utils.read(OW));
    }

    public static int getSELinuxMode() {
        return Utils.strToInt(Utils.read(LINUX));
    }

    public static int getSync() {
        return Utils.strToInt(Utils.read(SYNC));
    }

    public static int getTT() {
        return Utils.strToInt(Utils.read(TT));
    }

    public static int getSF() {
        return Utils.strToInt(Utils.read(SF));
    }

    public static boolean hasZygote() {
        return Utils.exist(ZYGOTE);
    }

    public static boolean hasSF() {
        return Utils.exist(SF);
    }

    public static boolean hasDozeMode() {
        return Utils.exist(DOZE);
    }

    public static boolean hasShield() {
        return Utils.exist(SHIELD);
    }

    public static boolean hasDNSMode() {
        return Utils.exist(DNS);
    }

    public static boolean hasAds() {
        return Utils.exist(ADS);
    }

    public static boolean hasOW() {
        return Utils.exist(OW);
    }

    public static boolean hasSELinux() {
        return Utils.exist(LINUX);
    }

    public static boolean hasSync() {
        return Utils.exist(SYNC);
    }

    public static boolean hasTT() {
        return Utils.exist(TT);
    }

    public static boolean hasNFSLog() {
        return Utils.exist(NFS_LOG);
    }

    public static boolean isModuleDisabled() {
        return Utils.exist(MODULE_PARANT + "/disable");
    }

    public static boolean isModuleRemoved() {
        return Utils.exist(MODULE_PARANT + "/remove");
    }

    public static String readNFSLog() {
        return Utils.read(NFS_LOG);
    }

    public static String readMagiskLog() {
        return Utils.read(MAGISK_LOG);
    }

    public static String getModVersion() {
        try {
            for (String line : Utils.read(MODULE_PARANT + "/module.prop").split("\\r?\\n")) {
                if (line.startsWith("version")) {
                    return line.replace("version=", "");
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    public static boolean isProUser() {
        if (BuildConfig.DEBUG) return true;
        return hasAds() && hasTT() && hasOW() && hasShield() && hasSF();
    }

    public static String conflictsList(Context context) {
        StringBuilder sb = new StringBuilder();
        if (Utils.exist("/data/adb/modules/GPUTurboBoost")) {
            sb.append("* GPU Turbo Boost Module\n");
        }
        if (Utils.exist("/data/adb/modules/legendary_kernel_tweaks")) {
            sb.append("* LKT Module\n");
        }
        if (Utils.exist("/data/adb/modules/lspeed")) {
            sb.append("* LSpeed Module\n");
        }
        if (Utils.exist("/data/adb/modules/FDE")) {
            sb.append("* FDE.AI Module\n");
        }
        if (Utils.exist("/data/adb/modules/MAGNETAR")
                || Utils.exist("/data/adb/modules/M4GN3T4R")) {
            sb.append("* Magnetar Module\n");
        }
        if (Utils.isAppInstalled("com.feravolt.fdeai", context)
                || Utils.isAppInstalled("com.feravolt.lite", context)) {
            sb.append("* FDE.AI App\n");
        }
        if (Utils.isAppInstalled("vul.max", context)
                || Utils.isAppInstalled("vul.pub", context)
                || Utils.isAppInstalled("vulmax.game", context)) {
            sb.append("* Vulmax App\n");
        }
        if (Utils.isAppInstalled("com.paget96.lspeedoptimizer", context)
                || Utils.isAppInstalled("com.paget96.lspeed", context)
                || Utils.isAppInstalled("com.paget96.l_speed", context)) {
            sb.append("* LSpeed App\n");
        }
        return sb.toString();
    }

    public static String illegalAppsList(Context context) {
        StringBuilder sb = new StringBuilder();
        if (Utils.isAppInstalled("nfg.multi_crack.android", context)) {
            sb.append("* NFG Multi Crack\n");
        }
        return sb.toString();
    }

    public static boolean isNFSSleeping() {
        return hasNFSLog() && readNFSLog().contains("NFS Will Comeback After 60 Seconds Sleeping")
                || supported() && !hasNFSLog();
    }

    public static boolean isNFSRunning() {
        return Utils.exist("/system/bin/injector") || !Utils.runAndGetError("injector --help")
                .contains("injector: inaccessible or not found");
    }

    public static boolean magiskSupported() {
        return Utils.exist("/sbin/.magisk") || Utils.exist("/data/adb/magisk");
    }

    public static boolean isModuleParent() {
        return Utils.exist(MODULE_PARANT);
    }

    public static boolean isNFSParent() {
        return Utils.exist(NFS_PARANT);
    }

    public static boolean supported() {
        return isModuleParent() && isNFSParent() && isNFSRunning();
    }

}