package com.nfs.nfsmanager.utils.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.Translator;
import com.nfs.nfsmanager.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 09, 2020
 */

public class CreditsActivity extends AppCompatActivity {

    private ArrayList<RecycleViewItem> mData = new ArrayList<>();
    private LinearLayout mProgressLayout;
    private MaterialTextView mProgressMessage;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        mData.add(new RecycleViewItem("Kernel Adiutor", "Willi Ye", getResources().getDrawable(R.drawable.ic_grarak), "https://github.com/Grarak"));
        mData.add(new RecycleViewItem("libsu", "John Wu", getResources().getDrawable(R.drawable.ic_topjohnwu),"https://github.com/topjohnwu"));
        mData.add(new RecycleViewItem("German Translation", "simonk206", getResources().getDrawable(R.drawable.ic_germany), null));
        mData.add(new RecycleViewItem("Italian Translation", "andrea", getResources().getDrawable(R.drawable.ic_italy),null));
        mData.add(new RecycleViewItem("French Translation", "K1ks", getResources().getDrawable(R.drawable.ic_france), null));
        mData.add(new RecycleViewItem("Spanish Translation", "Peter A. Cuevas", getResources().getDrawable(R.drawable.ic_spain),null));
        mData.add(new RecycleViewItem("Portuguese (rPt) Translation", "BrauliX", getResources().getDrawable(R.drawable.ic_portugal), "https://github.com/BrauliX"));
        mData.add(new RecycleViewItem("Portuguese (rBr) Translation", "*Unknown*", getResources().getDrawable(R.drawable.ic_brazil),null));
        mData.add(new RecycleViewItem("Russian Translation", "Ramazan Magomedov", getResources().getDrawable(R.drawable.ic_russia),"https://github.com/RamazanMagomedov"));

        AppCompatImageButton mBack = findViewById(R.id.back);
        MaterialCardView mTranslator = findViewById(R.id.translator);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mProgressLayout = findViewById(R.id.progress_layout);
        mProgressMessage = findViewById(R.id.progress_text);
        mBack.setOnClickListener(v -> {
            onBackPressed();
        });

        mTranslator.setOnClickListener(v -> {
            if (Utils.checkWriteStoragePermission(this)) {
                Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values/strings.xml",
                        mProgressLayout, mProgressMessage, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }

        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getSpanCount(this)));
        RecycleViewAdapter mRecycleViewAdapter = new RecycleViewAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private int getSpanCount(Activity activity) {
        return Utils.isTablet(activity) ? Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ?
                4 : 3 : Utils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
    }

    private static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

        private ArrayList<RecycleViewItem> data;

        public RecycleViewAdapter(ArrayList<RecycleViewItem> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_credits, parent, false);
            return new RecycleViewAdapter.ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
            holder.mTitle.setText(this.data.get(position).getTitle());
            if (Utils.isDarkTheme(holder.mTitle.getContext())) {
                holder.mTitle.setTextColor(Utils.getThemeAccentColor(holder.mTitle.getContext()));
            }
            holder.mDescription.setText(this.data.get(position).getDescription());
            holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
            holder.mRVLayout.setOnClickListener(v -> {
                if (this.data.get(position).getURL() != null) {
                    Utils.launchUrl(holder.mRVLayout, this.data.get(position).getURL(), holder.mRVLayout.getContext());
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView mIcon;
            private AppCompatTextView mTitle;
            private AppCompatTextView mDescription;
            private LinearLayout mRVLayout;

            public ViewHolder(View view) {
                super(view);
                this.mIcon = view.findViewById(R.id.icon);
                this.mTitle = view.findViewById(R.id.title);
                this.mDescription = view.findViewById(R.id.description);
                this.mRVLayout = view.findViewById(R.id.rv_credits);
            }
        }
    }

    private static class RecycleViewItem implements Serializable {
        private String mTitle;
        private String mDescription;
        private Drawable mIcon;
        private String mURL;

        public RecycleViewItem(String title, String description, Drawable icon, String url) {
            this.mTitle = title;
            this.mDescription = description;
            this.mIcon = icon;
            this.mURL = url;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getDescription() {
            return mDescription;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public String getURL() {
            return mURL;
        }
    }

}