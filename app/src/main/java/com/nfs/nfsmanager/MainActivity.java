package com.nfs.nfsmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.utils.CPUTimes;
import com.nfs.nfsmanager.utils.Flasher;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.UpdateCheck;
import com.nfs.nfsmanager.utils.Utils;
import com.nfs.nfsmanager.activities.CPUTimesActivity;
import com.nfs.nfsmanager.activities.DeviceInfoActivity;
import com.nfs.nfsmanager.activities.LogsActivity;
import com.nfs.nfsmanager.fragments.AboutFragment;
import com.nfs.nfsmanager.fragments.DashBoardFragment;
import com.nfs.nfsmanager.fragments.NFSFragment;
import com.wortise.ads.AdError;
import com.wortise.ads.AdSize;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.banner.BannerAd;
import com.wortise.ads.consent.ConsentManager;

import java.io.File;

import in.sunilpaulmathew.rootfilepicker.activities.FilePickerActivity;
import in.sunilpaulmathew.rootfilepicker.utils.FilePicker;
import kotlin.Unit;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 07, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatImageButton mSettings;
    private BannerAd mBannerAd;
    private MaterialTextView mProgressMessage;
    private boolean mExit, mSleeping = false, mWarning = true;
    private FrameLayout mBottomMenu;
    private Intent mIntent;
    private LinearLayout mProgressLayout;
    private final Handler mHandler = new Handler();
    private int doze, shield, dns, ads, ow, selinux, sync, tt, sf, zygot, lmk;
    private String gov, sched, tcp;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatImageButton mModuleImage = findViewById(R.id.module_image);
        mSettings =  findViewById(R.id.settings_menu);
        AppCompatImageView mUnsupportedImage = findViewById(R.id.no_root_Image);
        MaterialTextView mModuleStatus = findViewById(R.id.status_message);
        MaterialTextView mModuleTitle = findViewById(R.id.module_version);
        mProgressMessage = findViewById(R.id.progress_text);
        MaterialTextView mUnsupportedText = findViewById(R.id.unsupported_Text);
        FrameLayout mStatusLayout = findViewById(R.id.support_statue);
        LinearLayout mOffLineAd = findViewById(R.id.offline_ad);
        mBottomMenu = findViewById(R.id.bottom_menu);
        mProgressLayout = findViewById(R.id.progress_layout);
        LinearLayout mUnsupportedLayout = findViewById(R.id.unsupported_layout);

        // Initialize Banner Ad
        WortiseSdk.initialize(this, "b44ffeb9-63bf-41b8-a5d8-dc4bd1897eb4");

        mBannerAd = new BannerAd(this);
        mBannerAd.setAdSize(AdSize.HEIGHT_90);
        mBannerAd.setAdUnitId("388389a0-508b-4782-b11f-94989bf2394c");

        if (!Utils.rootAccess() || !NFS.magiskSupported() || !NFS.illegalAppsList(this).isEmpty() || NFS.isNFSSleeping()) {
            mBottomMenu.setVisibility(View.GONE);
            mUnsupportedLayout.setVisibility(View.VISIBLE);
            mUnsupportedImage.setImageDrawable(Utils.getColoredIcon(R.drawable.ic_help, this));
            mUnsupportedText.setText(!Utils.rootAccess() ? getString(R.string.no_root) : !NFS.magiskSupported() ?
                    getString(R.string.no_magisk) : !NFS.illegalAppsList(this).isEmpty() ?
                    getString(R.string.illegal_apps) : getString(R.string.sleeping));
            mUnsupportedImage.setOnClickListener(v -> {
                if (!Utils.rootAccess() || !NFS.magiskSupported()) {
                    Utils.launchUrl(mBottomMenu, "https://www.google.com/search?site=&source=hp&q=android+rooting+magisk", this);
                } else {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(mSleeping ? getString(R.string.sleeping_message) : getString (
                                    R.string.illegal_apps_summary, NFS.illegalAppsList(this)))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> super.onBackPressed())
                            .show();
                }
            });
            return;
        }

        mSettings.setOnClickListener(v -> settingsMenu());

        mOffLineAd.setOnClickListener(v -> Utils.launchUrl(mBottomMenu, "https://t.me/nfsreleases/424", this));

        mModuleImage.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
        mModuleTitle.setText(getString(R.string.module_version, NFS.getModVersion() +
                (NFS.getReleaseStatus().equals("") ? "" : " (" + NFS.getReleaseStatus() + ")")));

        if (NFS.isModuleParent()) {
            if (NFS.isModuleRemoved() || NFS.isModuleDisabled()) {
                mModuleStatus.setText(getString(R.string.module_status_message, NFS.isModuleRemoved() ?
                        getString(R.string.removed) : getString(R.string.disabled)));
                mModuleStatus.setVisibility(View.VISIBLE);
                mStatusLayout.setVisibility(View.VISIBLE);
                return;
            } else if (!NFS.isNFSRunning()) {
                mModuleStatus.setText(getString(R.string.module_status_execution_failed));
                mModuleStatus.setVisibility(View.VISIBLE);
                mStatusLayout.setVisibility(View.VISIBLE);
                return;
            } else if (!NFS.isNFSParent()) {
                mModuleStatus.setText(getString(R.string.data_removed));
                mModuleStatus.setVisibility(View.VISIBLE);
                mStatusLayout.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            mModuleStatus.setText(getString(R.string.no_support));
            mModuleStatus.setVisibility(View.VISIBLE);
            mStatusLayout.setVisibility(View.VISIBLE);
            return;
        }

        BottomNavigationView mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(navListener);
        mBottomNav.setVisibility(View.VISIBLE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DashBoardFragment()).commit();
        }

        if (!NFS.isProUser() && Utils.getOrientation(this) == Configuration.ORIENTATION_PORTRAIT) {
            // Request user consent
            WortiseSdk.wait(() -> {
                ConsentManager.requestOnce(this);
                return Unit.INSTANCE;
            });

            FrameLayout mAdFrame = findViewById(R.id.ad_frame);
            mAdFrame.addView(mBannerAd);

            mBannerAd.loadAd();

            mBannerAd.setListener(new BannerAd.Listener() {
                @Override
                public void onBannerClicked(@NonNull BannerAd ad) {
                }

                @Override
                public void onBannerFailed(@NonNull BannerAd ad, @NonNull AdError error) {
                    mAdFrame.setVisibility(View.GONE);
                    mOffLineAd.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBannerLoaded(@NonNull BannerAd ad) {
                    mAdFrame.setVisibility(View.VISIBLE);
                    mOffLineAd.setVisibility(View.GONE);
                }
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener
            = menuItem -> {
        Fragment selectedFragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                selectedFragment = new DashBoardFragment();
                break;
            case R.id.nav_nfs:
                selectedFragment = new NFSFragment();
                break;
            case R.id.nav_about:
                selectedFragment = new AboutFragment();
                break;
        }

        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();

        return true;
    };

    @SuppressLint("SetTextI18n")
    private void settingsMenu() {
        PopupMenu popupMenu = new PopupMenu(this, mSettings);
        Menu menu = popupMenu.getMenu();
        if (NFS.isNFSRunning()) {
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.view_log));
        }
        menu.add(Menu.NONE, 10, Menu.NONE, R.string.device_info);
        if (CPUTimes.supported("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state")) {
            menu.add(Menu.NONE, 11, Menu.NONE, R.string.cpu_stats);
        }
        if (NFS.isModuleParent()) {
            SubMenu module_options = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.module_settings));
            module_options.add(Menu.NONE, 2, Menu.NONE, getString(R.string.nfs_disable)).setCheckable(true)
                    .setChecked(NFS.isModuleDisabled());
            module_options.add(Menu.NONE, 3, Menu.NONE, getString(R.string.nfs_remove)).setCheckable(true)
                    .setChecked(NFS.isModuleRemoved());
            if (NFS.supported()) {
                module_options.add(Menu.NONE, 4, Menu.NONE, getString(R.string.nfs_delete));
            }
        }
        if (UpdateCheck.isSignatureMatched(this)) {
            SubMenu appSettings = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.app_settings));
            appSettings.add(Menu.NONE, 9, Menu.NONE, getString(R.string.update_check_auto)).setCheckable(true)
                    .setChecked(Utils.getBoolean("update_check_auto", true, this));
        }
        menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.flash_nfs));
        SubMenu reboot = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.reboot));
        reboot.add(Menu.NONE, 6, Menu.NONE, getString(R.string.normal));
        reboot.add(Menu.NONE, 7, Menu.NONE, getString(R.string.recovery));
        reboot.add(Menu.NONE, 8, Menu.NONE, getString(R.string.bootloader));
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    break;
                case 1:
                    mIntent = new Intent(this, LogsActivity.class);
                    startActivity(mIntent);
                    break;
                case 2:
                    if (NFS.isModuleDisabled()) {
                        Utils.delete("/data/adb/modules/injector/disable");
                    } else {
                        Utils.create("", "/data/adb/modules/injector/disable");
                    }
                    restartApp();
                    break;
                case 3:
                    if (NFS.isModuleRemoved()) {
                        Utils.delete("/data/adb/modules/injector/remove");
                    } else {
                        Utils.create("", "/data/adb/modules/injector/remove");
                    }
                    restartApp();
                    break;
                case 4:
                    new MaterialAlertDialogBuilder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(getString(R.string.delete_title))
                            .setMessage(getText(R.string.delete_message))
                            .setNeutralButton(getString(R.string.cancel), (dialogInterface, ii) -> {
                            })
                            .setPositiveButton(getString(R.string.delete), (dialogInterface, ii) -> {
                                Utils.delete("/data/NFS/");
                                restartApp();
                            })
                            .show();
                    break;
                case 5:
                    FilePicker.setExtension("zip");
                    FilePicker.setPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                    mIntent = new Intent(this, FilePickerActivity.class);
                    startActivityForResult(mIntent, 0);
                    break;
                case 6:
                    Utils.reboot("", mProgressLayout, mProgressMessage, this);
                    break;
                case 7:
                    Utils.reboot(" recovery", mProgressLayout, mProgressMessage, this);
                    break;
                case 8:
                    Utils.reboot(" bootloader", mProgressLayout, mProgressMessage, this);
                    break;
                case 9:
                    Utils.saveBoolean("update_check_auto", !Utils.getBoolean("update_check_auto", true, this), this);
                    break;
                case 10:
                    mIntent = new Intent(this, DeviceInfoActivity.class);
                    startActivity(mIntent);
                    break;
                case 11:
                    mIntent = new Intent(this, CPUTimesActivity.class);
                    startActivity(mIntent);
            }
            return false;
        });
        popupMenu.show();
    }

    private void showWarning() {
        View checkBoxView = View.inflate(this, R.layout.checkbox_layout, null);
        MaterialCheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
        checkBox.setChecked(true);
        checkBox.setText(getString(R.string.always_show));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                -> mWarning = isChecked);
        MaterialAlertDialogBuilder warning = new MaterialAlertDialogBuilder(this);
        warning.setIcon(R.mipmap.ic_launcher);
        warning.setTitle(getString(R.string.nfs_conflicts));
        warning.setMessage(getString(R.string.nfs_conflicts_summary) +
                NFS.conflictsList(this));
        warning.setCancelable(false);
        warning.setView(checkBoxView);
        warning.setPositiveButton(getString(R.string.got_it), (dialog, id) ->
                Utils.saveBoolean("warningMessage", mWarning, this)
        );
        warning.show();
    }

    private void restartApp() {
        mIntent = new Intent(this, MainActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null) {
            File mSelectedFile = FilePicker.getSelectedFile();
            if (!mSelectedFile.getName().endsWith("zip")) {
                Utils.longSnackbar(mBottomMenu, getString(R.string.invalid_zip));
                return;
            }
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setMessage(getString(R.string.sure_message, mSelectedFile.getName()))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.flash), (dialogInterface, i) -> Flasher.flashModule(mSelectedFile, this)).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!Utils.rootAccess()) {
            return;
        }
        if (!NFS.supported()) {
            return;
        }
        if (NFS.isModuleParent() && Utils.getBoolean("warningMessage", true, this)
                && !NFS.conflictsList(this).isEmpty()) {
            showWarning();
        }
        if (NFS.isNFSSleeping()) {
            mSleeping = true;
        }
        if (NFS.isModuleRemoved() || NFS.isModuleDisabled()) {
            Utils.indefiniteSnackbar(findViewById(android.R.id.content), getString(R.string.module_status_message, NFS.isModuleRemoved() ?
                    getString(R.string.removed) : getString(R.string.disabled)));
        }

        doze = NFS.getDozeMode();
        shield = NFS.getShield();
        dns = NFS.getDNSMode();
        ads = NFS.getAds();
        ow = NFS.getOW();
        selinux = NFS.getSELinuxMode();
        sync = NFS.getSync();
        tt = NFS.getTT();
        sf = NFS.getSF();
        zygot = NFS.getZygote();
        lmk = NFS.getLMK();
        gov = NFS.getGOV();
        sched = NFS.getSched();
        tcp = NFS.getTCP();

        if (NFS.getGOV().equals("performance")) {
            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.warning)
                    .setMessage(getString(R.string.performance_warning))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.got_it), (dialog, id) -> {
                    })
                    .show();
        }

        if (Utils.getBoolean("update_check_auto", true, this) && UpdateCheck.isSignatureMatched(this)) {
            UpdateCheck.initialize(1, this);
        }
    }

    @Override
    public void onBackPressed() {
        if (Utils.rootAccess() && NFS.magiskSupported() && NFS.supported() && !mSleeping) {
            if (doze != NFS.getDozeMode() || shield != NFS.getShield()
                    || dns != NFS.getDNSMode() || ads != NFS.getAds() || ow != NFS.getOW()
                    || selinux != NFS.getSELinuxMode() || sync != NFS.getSync() || tt != NFS.getTT()
                    || sf != NFS.getSF() || zygot != NFS.getZygote() || lmk != NFS.getLMK()
                    || !gov.equals(NFS.getGOV()) || !sched.equals(NFS.getSched())
                    || !tcp.equals(NFS.getTCP())) {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.reboot_dialog))
                        .setCancelable(false)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.reboot_required))
                        .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> super.onBackPressed())
                        .setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> Utils.reboot("", mProgressLayout, mProgressMessage, this))
                        .show();
            } else {
                if (mExit) {
                    mExit = false;
                    super.onBackPressed();
                } else {
                    Utils.longSnackbar(mBottomMenu, getString(R.string.press_back));
                    mExit = true;
                    mHandler.postDelayed(() -> mExit = false, 2000);
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBannerAd.destroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBannerAd.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBannerAd.resume();
    }

}