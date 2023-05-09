package com.nfs.nfsmanager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.Utils;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 06, 2020
 */

public class StartActivity extends AppCompatActivity {

    private boolean mSleeping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        AppCompatImageView mSplashScreen = findViewById(R.id.splash_screen);
        AppCompatImageView mUnsupportedImage = findViewById(R.id.no_root_Image);
        MaterialTextView mUnsupportedText = findViewById(R.id.unsupported_Text);
        LinearLayout mUnsupportedLayout = findViewById(R.id.unsupported_layout);

        if (!Utils.rootAccess() || !NFS.magiskSupported() || !NFS.illegalAppsList(this).isEmpty() || NFS.isNFSSleeping()) {
            mSplashScreen.setVisibility(View.GONE);
            mUnsupportedLayout.setVisibility(View.VISIBLE);
            Drawable helpDrawable = ContextCompat.getDrawable(this, R.drawable.ic_help);
            Objects.requireNonNull(helpDrawable).setTint(ContextCompat.getColor(this, R.color.ColorBlue));
            mUnsupportedImage.setImageDrawable(helpDrawable);
            mUnsupportedText.setText(!Utils.rootAccess() ? getString(R.string.no_root) : !NFS.magiskSupported() ?
                    getString(R.string.no_magisk) : !NFS.illegalAppsList(this).isEmpty() ?
                    getString(R.string.illegal_apps) : getString(R.string.sleeping));
            mUnsupportedImage.setOnClickListener(v -> {
                if (!Utils.rootAccess() || !NFS.magiskSupported()) {
                    Utils.launchUrl("https://www.google.com/search?site=&source=hp&q=android+rooting+magisk", this);
                } else {
                    new MaterialAlertDialogBuilder(this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(R.string.app_name)
                            .setMessage(mSleeping ? getString(R.string.sleeping_message) : getString (
                                    R.string.illegal_apps_summary, NFS.illegalAppsList(this)))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.cancel), (dialog1, id1) -> super.onBackPressed())
                            .show();
                }
            });
            return;
        }

        // Launch MainActivity
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
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
        if (NFS.isNFSSleeping()) {
            mSleeping = true;
        }
    }

}