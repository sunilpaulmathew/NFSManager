package com.nfs.nfsmanager.utils.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.Common;
import com.nfs.nfsmanager.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 13, 2020
 */

public class FlashingActivity extends AppCompatActivity {

    private AppCompatImageButton mSave;
    private LinearLayout mProgressLayout;
    private MaterialCardView mReboot;
    private MaterialTextView mFlashingOutput;
    private MaterialTextView mTitle;
    private NestedScrollView mScrollView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashing);

        AppCompatImageButton mBack = findViewById(R.id.back);
        mSave = findViewById(R.id.save);
        mTitle = findViewById(R.id.title);
        mFlashingOutput = findViewById(R.id.output);
        mReboot = findViewById(R.id.reboot);
        mProgressLayout = findViewById(R.id.flashing_progress);
        mScrollView = findViewById(R.id.scroll);
        MaterialTextView mProgressText = findViewById(R.id.progress_text);
        mProgressText.setText(getString(R.string.flashing, "..."));
        refreshStatus();
        mBack.setOnClickListener(v -> finish());
        mSave.setOnClickListener(v -> {
            Utils.create(Common.getFlashingResult().toString(), Utils.getInternalDataStorage(this) + "/flasher_log-" +
                    Common.getZipName().replace(".zip",""));
            Utils.longSnackbar(mSave, getString(R.string.flash_log, Utils.getInternalDataStorage(this) + "/flasher_log-" +
                    Common.getZipName().replace(".zip","")));
        });
        mReboot.setOnClickListener(v -> {
            Utils.reboot("", mProgressLayout, mProgressText, this);
            finish();
        });
    }

    public void refreshStatus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(() -> {
                            if (Common.isFlashing()) {
                                mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                            } else {
                                mProgressLayout.setVisibility(View.GONE);
                                mSave.setVisibility(Common.isFlashing() ? View.GONE: View.VISIBLE);
                            }
                            if (Common.isModuleInvalid()) {
                                mTitle.setText(Common.isFlashing() ? getString(R.string.flashing, "...") :
                                        getString(R.string.flashing, "aborted"));
                                mFlashingOutput.setText(Common.isFlashing() ? "" : getString(R.string.invalid_module));
                            } else {
                                mTitle.setText(Common.isFlashing() ? getString(R.string.flashing,"...") : Utils.getOutput(Common.getFlashingOutput()).endsWith("\nsuccess") ?
                                                getString(R.string.flashing, "finished") : getString(R.string.flashing, "failed"));
                                mFlashingOutput.setText(!Utils.getOutput(Common.getFlashingOutput()).isEmpty() ? Utils.getOutput(Common.getFlashingOutput())
                                        .replace("\nsuccess","") : Common.isFlashing() ? "" : Common.getFlashingResult());
                                mReboot.setVisibility(Utils.getOutput(Common.getFlashingOutput()).endsWith("\nsuccess") ? View.VISIBLE: View.GONE);
                            }
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!Common.isFlashing()) {
            finish();
        }
    }

}