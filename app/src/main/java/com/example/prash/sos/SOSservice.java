package com.example.prash.sos;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class SOSservice extends Service {

    String TAG = "SOS";
    MyReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        mReceiver = new MyReceiver(this, new Handler());

        // initialize location managers
        if (mReceiver.mLocationManager == null) {
            mReceiver.mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        getApplicationContext().getContentResolver().
                registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mReceiver);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        getApplicationContext().getContentResolver().unregisterContentObserver(mReceiver);

        if (mReceiver.mLocationManager != null) {
            for (int i = 0; i < mReceiver.mLocationListeners.length; i++) {
                try {
                    mReceiver.mLocationManager.removeUpdates(mReceiver.mLocationListeners[i]);
                    Log.d(TAG, "removed location listner " + i + " of " + mReceiver.mLocationListeners.length + " ignore");
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}