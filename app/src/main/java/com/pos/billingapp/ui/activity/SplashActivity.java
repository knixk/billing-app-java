package com.pos.billingapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pos.billingapp.R;

/**
 * A splash screen activity that serves as the initial screen when the app launches.
 * It displays a splash screen for a short duration and then navigates to the main activity.
 */
@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    /**
     * Creates and initializes the splash screen activity.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setFooter();
        Log.d(TAG,"Entering onCreate()");

        // Create an intent to navigate to the main activity
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        // Delay the start of the main activity using a handler on the main looper
        new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(intent), 2000);
        Log.d(TAG,"Exiting onCreate()");

    }
}
