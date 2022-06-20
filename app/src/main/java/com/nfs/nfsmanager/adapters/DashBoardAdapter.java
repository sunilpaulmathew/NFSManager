package com.nfs.nfsmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.NFS;
import com.nfs.nfsmanager.utils.SerializableItems;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 24, 2022
 */
public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.ViewHolder> {

    private final ArrayList<SerializableItems> data;

    private static ClickListener clickListener;

    public DashBoardAdapter(ArrayList<SerializableItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public DashBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_nfsmode, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DashBoardAdapter.ViewHolder holder, int position) {
        try {
            holder.mTitle.setText(this.data.get(position).getTitle());
            if (NFS.getNFSMode() == 0) {
                if (position == 0) {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorBlue));
                } else {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorTeal));
                }
            } else if (NFS.getNFSMode() == 1) {
                if (position == 1) {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorBlue));
                } else {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorTeal));
                }
            } else if (NFS.getNFSMode() == 2) {
                if (position == 2) {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorBlue));
                } else {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorTeal));
                }
            } else {
                if (position == 3) {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorBlue));
                } else {
                    holder.mLayoutCard.setCardBackgroundColor(ContextCompat.getColor(holder.mLayoutCard.getContext(), R.color.ColorTeal));
                }
            }
        } catch (NullPointerException ignored) {}
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialCardView mLayoutCard;
        private final MaterialTextView mTitle;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mLayoutCard = view.findViewById(R.id.main_card);
            this.mTitle = view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DashBoardAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}