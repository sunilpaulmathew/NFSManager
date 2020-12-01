/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import com.nfs.nfsmanager.utils.NFS;
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
                                mOutput.setText(Utils.getOutput(NFS.mOutput));
                            } catch (ConcurrentModificationException ignored) {}
                            if (NFS.mApplying) {
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
        if (NFS.mApplying) return;
        super.onBackPressed();
    }

}