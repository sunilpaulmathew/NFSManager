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
import com.nfs.nfsmanager.utils.Flasher;
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
        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> {
            Utils.create(Flasher.mFlashingResult.toString(), Utils.getInternalDataStorage() + "/flasher_log-" +
                    Flasher.mZipName.replace(".zip",""));
            Utils.longSnackbar(mSave, getString(R.string.flash_log, Utils.getInternalDataStorage() + "/flasher_log-" +
                    Flasher.mZipName.replace(".zip","")));
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
                            if (Flasher.mFlashing) {
                                mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                            } else {
                                mProgressLayout.setVisibility(View.GONE);
                                mSave.setVisibility(Flasher.mFlashing ? View.GONE: View.VISIBLE);
                            }
                            if (Flasher.mModuleInvalid) {
                                mTitle.setText(Flasher.mFlashing ? getString(R.string.flashing, "...") :
                                        getString(R.string.flashing, "aborted"));
                                mFlashingOutput.setText(Flasher.mFlashing ? "" : getString(R.string.invalid_module));
                            } else {
                                mTitle.setText(Flasher.mFlashing ? getString(R.string.flashing,"...") : Flasher.getOutput().endsWith("\nsuccess") ?
                                                getString(R.string.flashing, "finished") : getString(R.string.flashing, "failed"));
                                mFlashingOutput.setText(!Flasher.getOutput().isEmpty() ? Flasher.getOutput()
                                        .replace("\nsuccess","") : Flasher.mFlashing ? "" : Flasher.mFlashingResult);
                                mReboot.setVisibility(Flasher.getOutput().endsWith("\nsuccess") ? View.VISIBLE: View.GONE);
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
        if (Flasher.mFlashing) return;
        super.onBackPressed();
    }

}