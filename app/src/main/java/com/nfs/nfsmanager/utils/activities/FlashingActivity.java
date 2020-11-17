package com.nfs.nfsmanager.utils.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.Flasher;
import com.nfs.nfsmanager.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 13, 2020
 */

public class FlashingActivity extends AppCompatActivity {

    private LinearLayout mProgressLayout;
    private MaterialTextView mFlashingOutput;
    private MaterialTextView mReboot;
    private MaterialTextView mTitle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashing);

        AppCompatImageButton mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mFlashingOutput = findViewById(R.id.output);
        mReboot = findViewById(R.id.reboot);
        mProgressLayout = findViewById(R.id.flashing_progress);
        MaterialTextView mProgressText = findViewById(R.id.progress_text);
        mProgressText.setText(getString(R.string.flashing, "..."));
        refreshStatus();
        mBack.setOnClickListener(v -> onBackPressed());
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
                            if (!Flasher.mFlashing) {
                                mProgressLayout.setVisibility(View.GONE);
                            }
                            if (Flasher.mModuleInvalid) {
                                mTitle.setText(Flasher.mFlashing ? getString(R.string.flashing, "...") :
                                        getString(R.string.flashing, "aborted"));
                                mFlashingOutput.setText(Flasher.mFlashing ? "" : getString(R.string.invalid_module));
                            } else {
                                mTitle.setText(Flasher.mFlashing ? getString(R.string.flashing, "...") :
                                        Flasher.mFlashingOutput != null  && !Flasher.mFlashingOutput.toString().isEmpty() ?
                                                getString(R.string.flashing, "finished") : getString(R.string.flashing, "failed"));
                                mFlashingOutput.setText(Flasher.mFlashing ? "" : Flasher.mFlashingOutput != null && !Flasher.mFlashingOutput.toString().isEmpty() ?
                                        Flasher.mFlashingOutput.toString() : getString(R.string.flashing_failed));
                                mReboot.setVisibility(Flasher.mFlashingOutput != null && !Flasher.mFlashingOutput
                                        .toString().isEmpty() ? View.VISIBLE: View.GONE);
                            }
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        }.start();
    }

}