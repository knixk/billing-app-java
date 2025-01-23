package com.pos.billingapp.ui.activity;


import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pos.billingapp.BuildConfig;
import com.pos.billingapp.R;
import com.pos.billingapp.navigation.Navigator;

/**
 * Base {@link Activity} class for every Activity in this application.
 */
@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    /**
     * Called when the activity is being created. This method should be overridden
     * by subclasses to perform initialization and setup logic specific to each activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectActivityLeaks().penaltyLog().build());
        }
        Log.d(TAG,"Exiting onCreate()");
    }

    /**
     * Called when the activity is resumed. This method sets a flag to keep the screen on,
     * preventing the device's screen from turning off automatically while this activity is active.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"Entering onResume()");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG,"Exiting onResume()");
    }

    /**
     * Sets up the toolbar for the activity.
     */
    public void setToolbar() {
        Log.d(TAG,"Entering setToolbar");
        Log.i(TAG,"Setting up toolbar...");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ((ImageView) findViewById(R.id.settings)).setOnClickListener(view -> Navigator.getInstance().navigate2AppSetting(BaseActivity.this));
        ((ImageView) findViewById(R.id.home_back_button)).setOnClickListener(view -> Navigator.getInstance().navigate2Main(BaseActivity.this));
        Log.d(TAG,"Exiting setToolbar()");
    }

    /**
     * Sets up the footer text in the activity's layout to display the app version.
     */
    public void setFooter() {
        Log.d(TAG,"Entering setFooter()");
        ((TextView) findViewById(R.id.version)).setText(getString(R.string.txt_version) + BuildConfig.VERSION_NAME);
        Log.d(TAG,"Exiting setFooter()");
    }

    protected void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
