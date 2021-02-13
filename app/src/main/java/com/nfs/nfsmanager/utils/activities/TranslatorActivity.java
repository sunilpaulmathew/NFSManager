package com.nfs.nfsmanager.utils.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.nfs.nfsmanager.R;
import com.nfs.nfsmanager.utils.Translator;
import com.nfs.nfsmanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 10, 2020
 * Adapted from The Translator (Ref: https://play.google.com/store/apps/details?id=com.sunilpaulmathew.translator)
 */

public class TranslatorActivity extends AppCompatActivity {

    private AppCompatEditText mSearchWord;
    private LinearLayout mProgressLayout;
    private List<String> mData = new ArrayList<>();
    private MaterialCardView mSave;
    private MaterialTextView mProgressMessage;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        AppCompatImageButton mBack = findViewById(R.id.back);
        mSearchWord = findViewById(R.id.search_Text);
        AppCompatImageButton mSettings = findViewById(R.id.settings_menu);
        AppCompatImageButton mSearch = findViewById(R.id.search_button);
        mSave = findViewById(R.id.save);
        mRecyclerView = findViewById(R.id.recycler_view);
        mProgressLayout = findViewById(R.id.progress_layout);
        mProgressMessage = findViewById(R.id.progress_text);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecycleViewAdapter mRecycleViewAdapter = new RecycleViewAdapter(getData());
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            dialogEditText(mData.get(position),
                    (dialogInterface1, i1) -> {
                    }, text -> {
                        if (text.isEmpty()) {
                            return;
                        }
                        Utils.create(Translator.getStrings(this).replace(">" + mData.get(position) + "</string>", ">"
                                        + text + "</string>"), Utils.getInternalDataStorage(this) + "/strings.xml");
                        mData.set(position, text);
                        mRecycleViewAdapter.notifyDataSetChanged();
                    }, this).setOnDismissListener(dialogInterface -> {
            }).show();
        });


        mBack.setOnClickListener(v -> {
            onBackPressed();
        });

        mSettings.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, mSettings);
            Menu menu = popupMenu.getMenu();
            if (Utils.exist(Utils.getInternalDataStorage(this) + "/strings.xml")) {
                menu.add(Menu.NONE, 1, Menu.NONE, R.string.string_remove);
            }
            SubMenu import_string = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.string_import));
            import_string.add(Menu.NONE, 2, Menu.NONE, "German");
            import_string.add(Menu.NONE, 3, Menu.NONE, "Spanish");
            import_string.add(Menu.NONE, 4, Menu.NONE, "French");
            import_string.add(Menu.NONE, 5, Menu.NONE, "Italian");
            import_string.add(Menu.NONE, 6, Menu.NONE, "Portuguese (rPt)");
            import_string.add(Menu.NONE, 7, Menu.NONE, "Portuguese (rBr)");
            import_string.add(Menu.NONE, 8, Menu.NONE, "Russian");
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() > 1 && Utils.exist(Utils.getInternalDataStorage(this) + "/strings.xml")) {
                    Utils.longSnackbar(findViewById(android.R.id.content), getString(R.string.string_remove_message));
                    return false;
                }
                switch (item.getItemId()) {
                    case 0:
                        break;
                    case 1:
                        new MaterialAlertDialogBuilder(this)
                                .setMessage(getString(R.string.sure_question))
                                .setNegativeButton(getString(R.string.cancel), (dialog1, id1) -> {
                                    super.onBackPressed();
                                })
                                .setPositiveButton(getString(R.string.delete), (dialog1, id1) -> {
                                    new File(Utils.getInternalDataStorage(this) + "/strings.xml").delete();
                                    mSave.setVisibility(View.GONE);
                                    reloadUI();
                                })
                                .show();
                        break;
                    case 2:
                        Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values-de/strings.xml",
                                mProgressLayout, mProgressMessage, this);
                        break;
                    case 3:
                        Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values-es/strings.xml",
                                mProgressLayout, mProgressMessage, this);
                        break;
                    case 4:
                        Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values-fr/strings.xml",
                                mProgressLayout, mProgressMessage, this);
                        break;
                    case 5:
                        Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values-it/strings.xml",
                                mProgressLayout, mProgressMessage, this);
                        break;
                    case 6:
                        Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values-pt-rPT/strings.xml",
                                mProgressLayout, mProgressMessage, this);
                        break;
                    case 7:
                        Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values-pt-rBR/strings.xml",
                                mProgressLayout, mProgressMessage, this);
                        break;
                    case 8:
                        Translator.importTransaltions("https://github.com/sunilpaulmathew/NFSManager/raw/master/app/src/main/res/values-ru/strings.xml",
                                mProgressLayout, mProgressMessage, this);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        mSave.setOnClickListener(v -> {
            Utils.create(Translator.getStrings(this), Environment.getExternalStorageDirectory().toString() + "/" + java.util.Locale.getDefault().getLanguage());
            Utils.indefiniteSnackbar(findViewById(android.R.id.content), getString(R.string.save_string_message,
                    Environment.getExternalStorageDirectory().toString() + "/strings" + java.util.Locale.getDefault().getLanguage() + ".xml"));
        });

        mSearch.setOnClickListener(v -> {
            mSearchWord.setVisibility(View.VISIBLE);
        });

        mSearchWord.setTextColor(Color.RED);
        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Translator.mSearchText = s.toString().toLowerCase();
                reloadUI();
            }
        });
    }

    public void reloadUI() {
        mRecyclerView.setAdapter(new RecycleViewAdapter(getData()));
    }

    private List<String> getData() {
        mData.clear();
        if (Utils.exist(Utils.getInternalDataStorage(this) + "/strings.xml")) {
            for (String line : Objects.requireNonNull(Utils.read(Utils.getInternalDataStorage(this) + "/strings.xml")).split("\\r?\\n")) {
                if (line.contains("<string name=") && line.endsWith("</string>") && !line.contains("translatable=\"false")) {
                    String[] finalLine = line.split("\">");
                    if (Translator.mSearchText == null) {
                        mData.add(finalLine[1].replace("</string>", ""));
                    } else if (finalLine[1].replace("</string>", "").toLowerCase().contains(Translator.mSearchText.toLowerCase())) {
                        mData.add(finalLine[1].replace("</string>", ""));
                    }
                }
            }
        }
        return mData;
    }

    public interface OnDialogEditTextListener {
        void onClick(String text);
    }

    private MaterialAlertDialogBuilder dialogEditText(String text, final DialogInterface.OnClickListener negativeListener,
                                                      final OnDialogEditTextListener onDialogEditTextListener,
                                                      Context context) {
        LinearLayout layout = new LinearLayout(context);
        int padding = 75;
        layout.setPadding(padding, padding, padding, padding);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setGravity(Gravity.FILL_HORIZONTAL);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (text != null) {
            editText.append(text);
        }
        Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.illegal_string_warning), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.dismiss, v -> snackBar.dismiss());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Translator.checkIllegalCharacters(Objects.requireNonNull(s.toString()))) {
                    editText.setTextColor(Color.RED);
                    snackBar.show();
                } else {
                    editText.setTextColor(Utils.isDarkTheme(context) ? Color.WHITE : Color.BLACK);
                    snackBar.dismiss();
                }
            }
        });

        layout.addView(editText);

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context).setView(layout);
        if (negativeListener != null) {
            dialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(R.string.update, (dialog1, which) -> {
                if (Translator.checkIllegalCharacters(Objects.requireNonNull(editText.getText()).toString())) {
                    return;
                }
                onDialogEditTextListener.onClick(editText.getText().toString());
            });
            dialog.setOnDismissListener(dialog1 -> {
                if (negativeListener != null) {
                    negativeListener.onClick(dialog1, 0);
                }
            });
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!Utils.exist(Utils.getInternalDataStorage(this) + "/strings.xml")) {
            Utils.indefiniteSnackbar(findViewById(android.R.id.content), getString(R.string.translator_failed_message));
            mSave.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (Translator.mSearchText != null) {
            mSearchWord.setText(null);
            Translator.mSearchText = null;
            return;
        }
        if (mSearchWord.getVisibility() == View.VISIBLE) {
            mSearchWord.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    private static class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

        private List<String> data;

        private static ClickListener clickListener;

        public RecycleViewAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_translator, parent, false);
            return new ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
            if (Translator.mSearchText != null && this.data.get(position).toLowerCase().contains(Translator.mSearchText)) {
                holder.mTextView.setText(Utils.fromHtml(this.data.get(position).toLowerCase().replace(Translator.mSearchText,
                        "<b><i><font color=\"" + Color.RED + "\">" + Translator.mSearchText + "</font></i></b>")));
            } else {
                holder.mTextView.setText(this.data.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private MaterialTextView mTextView;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.mTextView = view.findViewById(R.id.description);
            }

            @Override
            public void onClick(View view) {
                clickListener.onItemClick(getAdapterPosition(), view);
            }
        }

        public void setOnItemClickListener(ClickListener clickListener) {
            RecycleViewAdapter.clickListener = clickListener;
        }

        public interface ClickListener {
            void onItemClick(int position, View v);
        }
    }

}