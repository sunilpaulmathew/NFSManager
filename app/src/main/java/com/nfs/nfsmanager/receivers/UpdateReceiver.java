package com.nfs.nfsmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nfs.nfsmanager.utils.UpdateCheck;
import com.nfs.nfsmanager.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 19, 2022
 */

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Utils.getBoolean("update_check_auto", true, context) && UpdateCheck
                .isSignatureMatched(context)) {
            UpdateCheck.initialize(1, true, context);
        }
    }

}