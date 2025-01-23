package com.pos.billingapp.navigation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.pos.billingapp.ui.activity.AppSettingActivity;
import com.pos.billingapp.ui.activity.MainActivity;

/**
 * Class used to navigate through the application.
 */
public abstract class Navigator {

    private static final String TAG = "Navigator";
    private static Navigator navigator;

    /**
     * Get an instance of the Navigator class.
     *
     * @return The Navigator instance.
     */
    public static synchronized Navigator getInstance() {
        if (navigator == null) {
            navigator = new Navigator() {
                @Override
                public void navigatorTo(Context context) {
                    Log.i(TAG, "Created Instance");
                }
            };
        }
        return navigator;
    }

    /**
     * Navigate to a specific location within the application.
     *
     * @param context The context from which navigation is initiated.
     */
    public abstract void navigatorTo(Context context);

    /**
     * Navigate to the app's settings activity.
     *
     * @param context The context from which navigation is initiated.
     */
    public void navigate2AppSetting(Context context) {
        Log.i(TAG,"navigate2AppSetting: executed.");
        if (context != null) {
            Intent intentToAppSetting = AppSettingActivity.getCallingIntent(context);
            context.startActivity(intentToAppSetting);
        }
    }

    /**
     * Navigate to the main activity of the app.
     *
     * @param context The context from which navigation is initiated.
     */
    public void navigate2Main(Context context) {
        Log.i(TAG,"navigate2Main: executed.");
        if (context != null) {
            Intent intentToMain = MainActivity.getCallingIntent(context);
            context.startActivity(intentToMain);
        }
    }
}
