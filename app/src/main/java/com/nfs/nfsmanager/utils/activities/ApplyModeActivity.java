package com.nfs.nfsmanager.utils.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.Common;
import com.nfs.nfsmanager.utils.Utils;

import java.util.ConcurrentModificationException;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 01, 2020
 */

public class ApplyModeActivity extends AppCompatActivity {

    private MaterialCardView mCancel;
    private MaterialTextView mOutput;
    private NestedScrollView mScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applymode);

        AppCompatImageButton mBackButton = findViewById(R.id.back_button);
        mCancel = findViewById(R.id.cancel_button);
        mOutput = findViewById(R.id.result_text);
        mScrollView = findViewById(R.id.scroll_view);

        mBackButton.setOnClickListener(v -> onBackPressed());
        mCancel.setOnClickListener(v -> onBackPressed());

        refreshStatus();
    }

    public void refreshStatus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(() -> {
                            try {
                                mOutput.setText(Utils.getOutput(Common.getOutput()));
                            } catch (ConcurrentModificationException ignored) {}
                            if (Common.isApplying()) {
                                mScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                            } else {
                                mCancel.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (InterruptedException ignored) {}
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (Common.isApplying()) return;
        super.onBackPressed();
    }

}