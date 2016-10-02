package com.matevz.zapisnik;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

class CustomPhoneStateListener extends PhoneStateListener {

    private int previousState;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        switch (state) {
            case TelephonyManager.CALL_STATE_OFFHOOK:
                previousState = state;
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if(previousState == TelephonyManager.CALL_STATE_OFFHOOK)
                    System.out.println("Develop - Call ended");
        }
    }
}
