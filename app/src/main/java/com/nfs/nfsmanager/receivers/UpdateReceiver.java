package com.nfs.nfsmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nfs.nfsmanager.utils.UpdateCheck;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 19, 2022
 */

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        UpdateCheck.initialize(1, true, context);
    }

}