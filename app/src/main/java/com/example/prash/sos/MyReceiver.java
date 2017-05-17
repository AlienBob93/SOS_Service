package com.example.prash.sos;

import android.content.Context;
import android.database.ContentObserver;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;

public class MyReceiver extends ContentObserver {

    String TAG = "SOS";
    int previousVolume;
    public int gesture_ctr = 0;
    Context context;

    SmsManager smsManager = SmsManager.getDefault();
    String phoneNo = "2562267516", message = "Help!\n";

    public LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    Location mLastKnownLocation;
    double lat = 0, lon = 0;

    public LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public MyReceiver(Context c, Handler handler) {
        super(handler);
        context=c;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);

        int delta = previousVolume - currentVolume;

        if (delta > 0) {
            gesture_ctr++;
            previousVolume = currentVolume;

            Log.d("SOS", "delta: " + gesture_ctr);
        } else if (delta < 0) {
            gesture_ctr++;
            previousVolume = currentVolume;

            Log.d("SOS", "delta: " + gesture_ctr);
        }

        if (gesture_ctr == 5) {

            getLastKnownLocation();

            message += "I'm here: " + lat + ", " + lon;
            smsManager.sendTextMessage(phoneNo, null, message, null, null);

            gesture_ctr = 0;
        }
    }

    public void getLastKnownLocation() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                    , LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[1]);

            mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lat = mLastKnownLocation.getLatitude();
            lon = mLastKnownLocation.getLongitude();

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                    , LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListeners[0]);

            mLastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            lat = mLastKnownLocation.getLatitude();
            lon = mLastKnownLocation.getLongitude();

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    public class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            //Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
}