package com.nfs.nfsmanager.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.fragments.DeviceInfoFragment;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 */

public class DeviceInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        AppCompatImageButton mBack = findViewById(R.id.back);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new DeviceInfoFragment()).commit();

        mBack.setOnClickListener(v -> super.onBackPressed());
    }

}