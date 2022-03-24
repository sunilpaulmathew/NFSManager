package com.nfs.nfsmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.SerializableItems;
import com.nfs.nfsmanager.utils.NFS;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 24, 2022
 */
public class NFSAdapter extends RecyclerView.Adapter<NFSAdapter.ViewHolder> {

    private final ArrayList<SerializableItems> data;

    public NFSAdapter(ArrayList<SerializableItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public NFSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_nfssettings, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull NFSAdapter.ViewHolder holder, int position) {
        try {
            holder.mTitle.setText(this.data.get(position).getTitle());
            holder.mValue.setText(this.data.get(position).getDescription());
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialTextView mTitle, mValue;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mTitle = view.findViewById(R.id.title);
            this.mValue = view.findViewById(R.id.value);
        }

        @Override
        public void onClick(View view) {
            CharSequence mItem = this.mTitle.getText();
            if (mItem.equals(view.getContext().getString(R.string.governor))) {
                NFS.setGOV(this.mValue, mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.scheduler))) {
                NFS.setScheduler(this.mValue, mValue);
            } else if (mItem.equals(view.getContext().getString(R.string.tcp))) {
                NFS.setTCP(this.mValue, mValue);
            } else if (mItem.equals(view.getContext().getString(R.string.doze))) {
                NFS.setDozeMode(this.mValue, mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.ram_level))) {
                NFS.setLMKMenu(this.mValue, mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.shield))) {
                NFS.setShield(this.mValue, mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.add_disabler))) {
                NFS.setAds(this.mValue, mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.dns))) {
                NFS.setDNSMode(this.mValue, mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.selinux))) {
                NFS.setSELinuxMode(this.mValue, mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.overwatch_engine)) || mItem.equals(view.getContext().getString(R.string.zygote))) {
                NFS.setEnableDisableMenu(this.mValue, mItem.toString(), mValue, view.getContext());
            } else if (mItem.equals(view.getContext().getString(R.string.thermal_throttle)) || mItem.equals(view.getContext().getString(R.string.sync))
                    || mItem.equals(view.getContext().getString(R.string.super_sampling))) {
                NFS.setOnOffMenu(this.mValue, mItem.toString(), mValue, view.getContext());
            }
        }
    }

}