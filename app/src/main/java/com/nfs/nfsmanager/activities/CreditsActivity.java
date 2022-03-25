package com.nfs.nfsmanager.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.adapters.CreditsAdapter;
import com.nfs.nfsmanager.utils.SerializableItems;
import com.nfs.nfsmanager.utils.Utils;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 09, 2020
 */
public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        AppCompatImageButton mBack = findViewById(R.id.back);
        MaterialTextView mVersion = findViewById(R.id.version);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        mVersion.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        mBack.setOnClickListener(v -> onBackPressed());

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getSpanCount(this)));
        CreditsAdapter mRecycleViewAdapter = new CreditsAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private int getSpanCount(Activity activity) {
        return Utils.isTablet(activity) ? Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ?
                4 : 3 : Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
    }

    private ArrayList<SerializableItems> getData() {
        ArrayList<SerializableItems> mData = new ArrayList<>();
        mData.add(new SerializableItems("Kernel Adiutor", "Willi Ye", ContextCompat.getDrawable(this, R.drawable.ic_grarak), "https://github.com/Grarak"));
        mData.add(new SerializableItems("libsu", "John Wu", ContextCompat.getDrawable(this, R.drawable.ic_topjohnwu),"https://github.com/topjohnwu"));
        mData.add(new SerializableItems("German Translation", "simonk206", ContextCompat.getDrawable(this, R.drawable.ic_germany), null));
        mData.add(new SerializableItems("Italian Translation", "andrea", ContextCompat.getDrawable(this, R.drawable.ic_italy),null));
        mData.add(new SerializableItems("French Translation", "K1ks", ContextCompat.getDrawable(this, R.drawable.ic_france), null));
        mData.add(new SerializableItems("Spanish Translation", "Peter A. Cuevas", ContextCompat.getDrawable(this, R.drawable.ic_spain),null));
        mData.add(new SerializableItems("Portuguese (rPt) Translation", "BrauliX", ContextCompat.getDrawable(this, R.drawable.ic_portugal), "https://github.com/BrauliX"));
        mData.add(new SerializableItems("Portuguese (rBr) Translation", "BrauliX", ContextCompat.getDrawable(this, R.drawable.ic_brazil),"https://github.com/BrauliX"));
        mData.add(new SerializableItems("Russian Translation", "Ramazan Magomedov", ContextCompat.getDrawable(this, R.drawable.ic_russia),"https://github.com/RamazanMagomedov"));
        mData.add(new SerializableItems("Indonesian Translation", "Hafitz Setya", ContextCompat.getDrawable(this, R.drawable.ic_indonesia),"https://github.com/breakdowns"));
        return mData;
    }

}