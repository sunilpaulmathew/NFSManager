package com.nfs.nfsmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.utils.Flasher;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.UpdateCheck;
import com.nfs.nfsmanager.utils.Utils;
import com.nfs.nfsmanager.utils.activities.FlashingActivity;
import com.nfs.nfsmanager.utils.activities.LogsActivity;
import com.nfs.nfsmanager.utils.fragments.AboutFragment;
import com.nfs.nfsmanager.utils.fragments.DashBoardFragment;
import com.nfs.nfsmanager.utils.fragments.NFSFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 07, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatImageButton mSettings;
    private MaterialTextView mProgressMessage;
    private boolean mExit, mSleeping = false, mWarning = true;
    private FrameLayout mBottomMenu;
    private LinearLayout mProgressLayout;
    private Handler mHandler = new Handler();
    private int doze, shield, dns, ads, ow, selinux, sync, tt, sf, zygot;
    private String gov, mPath, sched, tcp;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
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
                            .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> {
                                super.onBackPressed();
                            })
                            .show();
                }
            });
            return;
        }

        mSettings.setOnClickListener(v -> settingsMenu());

        mOffLineAd.setOnClickListener(v -> {
            Utils.launchUrl(mBottomMenu, "https://t.me/nfsreleases/424", this);
        });

        mModuleImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
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
            mOffLineAd.setVisibility(View.VISIBLE);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
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
                    Intent nfsLog = new Intent(this, LogsActivity.class);
                    startActivity(nfsLog);
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
                    if (Utils.checkWriteStoragePermission(this)) {
                        Intent manualflash = new Intent(Intent.ACTION_GET_CONTENT);
                        manualflash.setType("application/*");
                        startActivityForResult(manualflash, 0);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        Utils.longSnackbar(mBottomMenu, getString(R.string.storage_access_denied));
                    }
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
            }
            return false;
        });
        popupMenu.show();
    }

    private void showWarning() {
        View checkBoxView = View.inflate(this, R.layout.rv_checkbox, null);
        MaterialCheckBox checkBox = checkBoxView.findViewById(R.id.checkbox);
        checkBox.setChecked(true);
        checkBox.setText(getString(R.string.always_show));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked)
                -> mWarning = isChecked);
        MaterialAlertDialogBuilder warning = new MaterialAlertDialogBuilder(Objects.requireNonNull(this));
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private void flashModule(File file, Activity activity) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Flasher.mZipName = file.getName();
                Flasher.mFlashingResult = new StringBuilder();
                Flasher.mFlashingOutput = new ArrayList<>();
                Flasher.mFlashing = true;
                Intent flashing = new Intent(activity, FlashingActivity.class);
                startActivity(flashing);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                Flasher.mFlashingResult.append("** Preparing to flash ").append(file.getName()).append("...\n\n");
                Flasher.mFlashingResult.append("** Path: '").append(file.toString()).append("'\n\n");
                // Delete if an old zip file exists
                Utils.delete(getCacheDir() + "/flash.zip");
                Flasher.mFlashingResult.append("** Copying '").append(file.getName()).append("' into temporary folder: ");
                Flasher.mFlashingResult.append(Utils.runAndGetError("cp '" + file.toString() + "' " + getCacheDir() + "/flash.zip"));
                Flasher.mFlashingResult.append(Utils.exist(getCacheDir() + "/flash.zip") ? "Done *\n\n" : "\n\n");
                Flasher.flashModule(activity);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Utils.delete(getCacheDir() + "/flash.zip");
                Flasher.mFlashing = false;
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getPath(file);
            }
            if (!mPath.endsWith("zip")) {
                Utils.longSnackbar(mBottomMenu, getString(R.string.invalid_zip));
                return;
            }
            MaterialAlertDialogBuilder manualFlash = new MaterialAlertDialogBuilder(this);
            manualFlash.setMessage(getString(R.string.sure_message, new File(mPath).getName()));
            manualFlash.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            manualFlash.setPositiveButton(getString(R.string.flash), (dialogInterface, i) -> {
                flashModule(new File(mPath), this);
            });
            manualFlash.show();
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

        UpdateCheck.autoUpdateCheck(this);
    }

    @Override
    public void onBackPressed() {
        if (Utils.rootAccess() && NFS.magiskSupported() && NFS.supported() && !mSleeping) {
            if (doze != NFS.getDozeMode() || shield != NFS.getShield()
                    || dns != NFS.getDNSMode() || ads != NFS.getAds() || ow != NFS.getOW()
                    || selinux != NFS.getSELinuxMode() || sync != NFS.getSync() || tt != NFS.getTT()
                    || sf != NFS.getSF() || zygot != NFS.getZygote() || !gov.equals(NFS.getGOV())
                    || !sched.equals(NFS.getSched()) || !tcp.equals(NFS.getTCP())) {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.reboot_dialog))
                        .setCancelable(false)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.reboot_required))
                        .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                            super.onBackPressed();
                        })
                        .setPositiveButton(getString(R.string.reboot), (dialog1, id1) -> {
                            Utils.reboot("", mProgressLayout, mProgressMessage, this);
                        })
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
}