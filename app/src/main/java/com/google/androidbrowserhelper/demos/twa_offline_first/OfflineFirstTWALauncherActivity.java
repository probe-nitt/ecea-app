package com.google.androidbrowserhelper.demos.twa_offline_first;

import com.google.androidbrowserhelper.trusted.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;

public class OfflineFirstTWALauncherActivity extends LauncherActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFinishing()) {
            return;
        }
        tryLaunchTwa();
    }

    @Override
    protected boolean shouldLaunchImmediately() {
        return false;
    }

    private void tryLaunchTwa() {
        if (hasTwaLaunchedSuccessfully()) {
            launchTwa();
            return;
        }

        if (isOnline()) {
            firstTimeLaunchTwa();
            return;
        }

        renderOfflineFallback();
    }

    private boolean hasTwaLaunchedSuccessfully(){
        StrictMode.ThreadPolicy oldPolicy = StrictMode.getThreadPolicy();
        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.twa_offline_first_preferences_file_key),
                    Context.MODE_PRIVATE);
            return sharedPref.getBoolean(getString(R.string.twa_launched_successfully), false);
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    private void renderOfflineFallback() {
        setContentView(R.layout.activity_offline_first_twalauncher3);

        Button retryBtn = this.findViewById(R.id.retry_btn);
        retryBtn.setOnClickListener(v -> {
            if (isOnline()) {
                firstTimeLaunchTwa();
            }
        });
    }

    private void firstTimeLaunchTwa() {

        launchTwa();

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.twa_offline_first_preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.twa_launched_successfully), true);
        editor.apply();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

