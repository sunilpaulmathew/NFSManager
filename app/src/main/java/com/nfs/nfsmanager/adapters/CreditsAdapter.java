package com.nfs.nfsmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.SerializableItems;
import com.nfs.nfsmanager.utils.Utils;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 24, 2022
 */
public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

    private final ArrayList<SerializableItems> data;

    public CreditsAdapter(ArrayList<SerializableItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public CreditsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_credits, parent, false);
        return new CreditsAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditsAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(this.data.get(position).getTitle());
        if (Utils.isDarkTheme(holder.mTitle.getContext())) {
            holder.mTitle.setTextColor(ContextCompat.getColor(holder.mTitle.getContext(), R.color.ColorBlue));
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
        private final AppCompatImageView mIcon;
        private final AppCompatTextView mTitle;
        private final AppCompatTextView mDescription;
        private final LinearLayout mRVLayout;

        public ViewHolder(View view) {
            super(view);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mRVLayout = view.findViewById(R.id.rv_credits);
        }
    }

}