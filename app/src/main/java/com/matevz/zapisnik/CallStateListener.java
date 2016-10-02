package com.matevz.zapisnik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallStateListener extends BroadcastReceiver {

    TelephonyManager telephonyManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Develop - Broadcast received");

        CustomPhoneStateListener phoneStateListener = new CustomPhoneStateListener();

        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
