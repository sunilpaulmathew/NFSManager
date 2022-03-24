package com.nfs.nfsmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 24, 2022
 */
public class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.ViewHolder> {

    private final List<String> data;

    public DeviceInfoAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public DeviceInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_nfsinfo, parent, false);
        return new DeviceInfoAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceInfoAdapter.ViewHolder holder, int position) {
        holder.mCoreStatus.setText(this.data.get(position));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView mCoreStatus;

        public ViewHolder(View view) {
            super(view);
            this.mCoreStatus = view.findViewById(R.id.core_status);
        }
    }

}