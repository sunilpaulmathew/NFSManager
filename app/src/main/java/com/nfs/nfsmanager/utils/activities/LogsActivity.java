package com.nfs.nfsmanager.utils.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.PagerAdapter;
import com.nfs.nfsmanager.utils.fragments.MagiskLogFragment;
import com.nfs.nfsmanager.utils.fragments.NFSLogFragment;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 11, 2020
 */

public class LogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);

        TabLayout mTabLayout = findViewById(R.id.tab_Layout);
        ViewPager mViewPager = findViewById(R.id.view_pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new NFSLogFragment(), getString(R.string.nfs_log));
        adapter.AddFragment(new MagiskLogFragment(), getString(R.string.magisk_log));

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

}