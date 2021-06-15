package com.nfs.nfsmanager.utils.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.Common;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on January 01, 2021
 */

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);

        AppCompatImageButton mBack = findViewById(R.id.back);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(v -> finish());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new WebViewFragment()).commit();

        mBack.setOnClickListener(v -> finish());
    }

    public static class WebViewFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            WebView mWebView = new WebView(requireActivity());
            mWebView.loadUrl(Common.getURL());
            mWebView.setWebViewClient(new WebViewClient());

            requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        requireActivity().finish();
                    }
                }
            });

            return mWebView;
        }

    }

}