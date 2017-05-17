package com.example.prash.sos;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    String TAG = "Android : ";
    SharedPreferences prefs = null;
    private static final int REQUEST_LOCATION = 0;

    public Notification n;
    public static NotificationManager notificationManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "The onCreate() event");

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // get sharedPreferences (for checking the application's first run)
        prefs = getSharedPreferences("com.example.prash.SOSservice", MODE_PRIVATE);

        // prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(this, MainActivity.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //<editor-fold desc="create notification">
        // build notification the addAction re-use the same intent to keep the example short
        n  = new Notification.Builder(this)
                .setContentTitle("Running").setContentText("SOS")
                .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true).setContentIntent(pIntent).build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //</editor-fold>

        // create the notification
        notificationManager.notify(0, n);
        startService(new Intent(getBaseContext(), SOSservice.class));

        final Fragment fragment_info = new tabFragment_INFO();
        final Fragment fragment_contacts = new tabFragment_CONTACTS();

        final FragmentManager fm = getSupportFragmentManager();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_info) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.contentContainer, fragment_info);
                    transaction.commit();

                } else if (tabId == R.id.tab_contacts) {
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.contentContainer, fragment_contacts);
                    transaction.commit();
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_info) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.contentContainer, fragment_info);
                    transaction.commit();
                } else if (tabId == R.id.tab_contacts) {
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.contentContainer, fragment_contacts);
                    transaction.commit();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            //ask for location and sms permissions

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this
                    , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Location permission has not been granted.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                                                                                                                                                                                                                                                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }

            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void stopServiceNotification() {
        notificationManager.cancelAll();
    }
}
