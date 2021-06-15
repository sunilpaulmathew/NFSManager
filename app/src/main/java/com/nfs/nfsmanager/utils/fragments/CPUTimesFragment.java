package com.nfs.nfsmanager.utils.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.CPUTimes;
import com.nfs.nfsmanager.utils.Common;

import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 */

public class CPUTimesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_cputime, container, false);

        Common.initializeRecyclerView(mRootView, R.id.recycler_view);
        Common.getRecyclerView().setLayoutManager(new LinearLayoutManager(requireActivity()));
        Common.getRecyclerView().setAdapter(Common.getRecycleViewAdapter());

        return mRootView;
    }

    public static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

        private final List<String> data;

        public RecycleViewAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_cputimes, parent, false);
            return new ViewHolder(rowItem);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
            String[] finalLine = this.data.get(position).split(" ");
            try {
                if (this.data.get(position).equals("Deep sleep")) {
                    holder.mFreq.setText("Deep  sleep");
                    holder.mPercent.setText(Math.round((float) ((SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) * 100) / SystemClock.elapsedRealtime()) + "%");
                    holder.mTime.setText(CPUTimes.sToString((SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) / 1000));
                    holder.mProgress.setProgress(Math.round((float) ((SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) * 100) / SystemClock.elapsedRealtime()));
                } else {
                    holder.mFreq.setText(Integer.parseInt(finalLine[0]) / 1000 + " MHz");
                    holder.mPercent.setText(Math.round((float) Integer.parseInt(finalLine[1]) * 1000 / SystemClock.elapsedRealtime()) + "%");
                    holder.mTime.setText(CPUTimes.sToString(Integer.parseInt(finalLine[1]) / 100));
                    holder.mProgress.setProgress(Math.round((float) Integer.parseInt(finalLine[1]) * 1000 / SystemClock.elapsedRealtime()));
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            }
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final MaterialTextView mFreq, mPercent, mTime;
            private final ProgressBar mProgress;

            public ViewHolder(View view) {
                super(view);
                this.mFreq = view.findViewById(R.id.freq);
                this.mPercent = view.findViewById(R.id.percentage);
                this.mTime = view.findViewById(R.id.time);
                this.mProgress = view.findViewById(R.id.progress);
            }
        }
    }

}