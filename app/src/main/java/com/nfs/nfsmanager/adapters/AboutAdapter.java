package com.nfs.nfsmanager.adapters;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nfs.nfsmanager.BuildConfig;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.activities.ChangeLogActivity;
import com.nfs.nfsmanager.activities.CreditsActivity;
import com.nfs.nfsmanager.activities.WebViewActivity;
import com.nfs.nfsmanager.utils.Common;
import com.nfs.nfsmanager.utils.SerializableItems;
import com.nfs.nfsmanager.utils.UpdateCheck;
import com.nfs.nfsmanager.utils.Utils;

import java.util.ArrayList;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 24, 2022
 */
public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private final ArrayList<SerializableItems> data;

    public AboutAdapter(ArrayList<SerializableItems> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_about, parent, false);
        return new AboutAdapter.ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(this.data.get(position).getTitle());
        if (Utils.isDarkTheme(holder.mTitle.getContext())) {
            holder.mTitle.setTextColor(ContextCompat.getColor(holder.mTitle.getContext(), R.color.ColorBlue));
        }
        holder.mDescription.setText(this.data.get(position).getDescription());
        holder.mIcon.setImageDrawable(this.data.get(position).getIcon());
        holder.mRVLayout.setOnClickListener(v -> {
            if (position == 0) {
                Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                holder.mRVLayout.getContext().startActivity(settings);
            } else if (position == 5) {
                Intent changeLogs = new Intent(holder.mRVLayout.getContext(), ChangeLogActivity.class);
                holder.mRVLayout.getContext().startActivity(changeLogs);
            } else if (position == 7) {
                Common.setURL("file:///android_asset/privacy-policy.html");
                Intent privacyPolicy = new Intent(holder.mRVLayout.getContext(), WebViewActivity.class);
                holder.mRVLayout.getContext().startActivity(privacyPolicy);
            } else if (position == 8) {
                Common.setURL("https://www.gnu.org/licenses/gpl-3.0-standalone.html");
                Intent licence = new Intent(holder.mRVLayout.getContext(), WebViewActivity.class);
                holder.mRVLayout.getContext().startActivity(licence);
            } else if (position == 9) {
                Intent credits = new Intent(holder.mRVLayout.getContext(), CreditsActivity.class);
                holder.mRVLayout.getContext().startActivity(credits);
            } else if (position == 10) {
                new MaterialAlertDialogBuilder(v.getContext())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(v.getContext().getString(R.string.app_name))
                        .setMessage(v.getContext().getText(R.string.donations_message))
                        .setNegativeButton(R.string.cancel, (dialogInterface, ii) -> {
                        })
                        .setPositiveButton(R.string.donate, (dialogInterface, ii) ->
                                Utils.launchUrl("https://www.paypal.me/menacherry", holder.mRVLayout.getContext()))
                        .show();
            } else if (position == 11) {
                UpdateCheck.initialize(0, false, holder.mRVLayout.getContext());
            } else if (this.data.get(position).getURL() != null) {
                Utils.launchUrl(this.data.get(position).getURL(), holder.mRVLayout.getContext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton mIcon;
        private final AppCompatTextView mTitle;
        private final AppCompatTextView mDescription;
        private final LinearLayout mRVLayout;

        public ViewHolder(View view) {
            super(view);
            this.mIcon = view.findViewById(R.id.icon);
            this.mTitle = view.findViewById(R.id.title);
            this.mDescription = view.findViewById(R.id.description);
            this.mRVLayout = view.findViewById(R.id.rv_about);
        }
    }

}