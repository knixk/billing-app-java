package com.pos.billingapp.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.billingapp.R;
import com.pos.billingapp.adapter.ScanDevicesAdapter;
import com.pos.billingapp.callback.OnSelectDeviceListener;
import com.pos.billingapp.navigation.Navigator;
import com.pos.poslib.callback.ScanDeviceListener;
import com.pos.billingapp.constants.Constants;
import com.pos.poslib.model.ConfigData;
import com.pos.poslib.model.DeviceDetails;
import com.pos.poslib.poslibmanager.PosLibManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RequiresApi(api = Build.VERSION_CODES.S)
@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class AppSettingActivity extends BaseActivity {
    private static final int REQUEST_MANAGE_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static final String TAG = "AppSettingActivity";
    EditText logPathEt;
    EditText logRetainEt;
    Button btConnectBtn;
    EditText logLevelEt;
    String logLevel = null;
    String logRetain = null;
    String logFilePath;
    int logTypeLevel;
    String regexPattern = "^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?";
    CheckBox logOptionCheckBox;
    TextView scanDevicetcpIp;
    TextView scanbtname;
    TextView scanbtssid;
    TextView scanDevicePort;
    Spinner prioritySpinnerTv;
    EditText connectionTimeOut;
    EditText connectionRetry;
    EditText logPath;
    Dialog dialog;
    String deviceSlno = null;
    String deviceID = null;
    String appToAPP = "App To App";
    List<DeviceDetails> deviceLists;
    private AlertDialog progressAlertDialog = null;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, AppSettingActivity.class);
    }

    /**
     * Called when the activity is being created. Initializes and configures UI elements and behaviors.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view layout for this activity.
        setContentView(R.layout.activity_settings);
        Log.d(TAG,"Entering onCreate()");

        // Hide and show various toolbar elements based on UI requirements.
        Log.i(TAG,"onCreate: Hiding/showing toolbar elements...");
        ((Toolbar) findViewById(R.id.settingToolbar)).setVisibility(View.VISIBLE);
        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.INVISIBLE);
        ((Toolbar) findViewById(R.id.posToolbar)).setVisibility(View.INVISIBLE);

        // Hide certain image views in the UI.
        ((ImageView) findViewById(R.id.exclamatory_Icon_Image)).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.exclamatory_Icon)).setVisibility(View.INVISIBLE);

        // Initialize and find references to various UI elements.
        logRetainEt = findViewById(R.id.log_Retain_Ed);
        logLevelEt = findViewById(R.id.log_Level_Ed);
        logOptionCheckBox = findViewById(R.id.log_Option_CheckBox);

        ((ImageView) findViewById(R.id.home_back_button)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.settings)).setVisibility(View.GONE);

        //TCP IP
        scanDevicetcpIp = findViewById(R.id.device_Ip_Ed);
        scanDevicePort = findViewById(R.id.tcpIp_Port_Value);

        btConnectBtn = findViewById(R.id.bt_connect_Btn);

        //Bt
        scanbtname = findViewById(R.id.bt_Device_Value);
        scanbtssid = findViewById(R.id.bt_Ssis_Ed);
        connectionTimeOut = findViewById(R.id.connection_TimeOut_Ed);
        connectionRetry = findViewById(R.id.connection_Retries_Ed);
        prioritySpinnerTv = findViewById(R.id.communication_Spinner);

        // Show or hide UI elements based on selected priority.
        prioritySpinnerTv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Disable the CardView and its children when "App To App" is selected
                if (appToAPP.equals(selectedItem)) {
                    ((CardView) findViewById(R.id.cardViewOne)).setEnabled(false);
                    ((CardView) findViewById(R.id.cardViewTwo)).setEnabled(false);
                    ((EditText) findViewById(R.id.device_Ip_Ed)).setEnabled(false);
                    ((EditText) findViewById(R.id.tcpIp_Port_Value)).setEnabled(false);
                    ((Button) findViewById(R.id.tcpIp_Scan_Btn)).setEnabled(false);
                    ((Button) findViewById(R.id.tcpIp_Port_Connect_Btn)).setEnabled(false);
                    ((Button) findViewById(R.id.scan_Device_Bt)).setEnabled(false);
                    ((Button) findViewById(R.id.bt_connect_Btn)).setEnabled(false);
                    ((EditText) findViewById(R.id.bt_Device_Value)).setEnabled(false);
                    ((EditText) findViewById(R.id.bt_Ssis_Ed)).setEnabled(false);
                    ((CardView) findViewById(R.id.cardViewOne)).setAlpha(0.5f);
                    ((CardView) findViewById(R.id.cardViewTwo)).setAlpha(0.5f);// Optional: To visually indicate it's disabled
                } else {
                    ((CardView) findViewById(R.id.cardViewOne)).setEnabled(true);
                    ((CardView) findViewById(R.id.cardViewTwo)).setEnabled(true);
                    ((EditText) findViewById(R.id.device_Ip_Ed)).setEnabled(false);
                    ((EditText) findViewById(R.id.tcpIp_Port_Value)).setEnabled(false);
                    ((Button) findViewById(R.id.tcpIp_Scan_Btn)).setEnabled(true);
                    ((Button) findViewById(R.id.tcpIp_Port_Connect_Btn)).setEnabled(true);
                    ((Button) findViewById(R.id.scan_Device_Bt)).setEnabled(true);
                    ((Button) findViewById(R.id.bt_connect_Btn)).setEnabled(true);
                    ((EditText) findViewById(R.id.bt_Device_Value)).setEnabled(false);
                    ((EditText) findViewById(R.id.bt_Ssis_Ed)).setEnabled(false);
                    ((CardView) findViewById(R.id.cardViewOne)).setAlpha(1.0f); // Optional: Reset alpha to indicate it's enabled
                    ((CardView) findViewById(R.id.cardViewTwo)).setAlpha(1.0f); // Optional: Reset alpha to indicate it's enabled
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (if needed)
            }
        });
        logPathEt = findViewById(R.id.log_Path_Ed);

        setSettingValues();

        logOptionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> enableDisableLogOptionComView(isChecked));
        // Handle the click event to save settings and navigate back to the main activity.
        findViewById(R.id.save_settings).setOnClickListener(view -> {
            Log.i(TAG, "Save settings button clicked...");
            saveValues();
            Toast.makeText(getApplicationContext(), "Config Saved!!", Toast.LENGTH_LONG).show();
            handler.postDelayed(() -> Navigator.getInstance().navigate2Main(AppSettingActivity.this), 1000);

        });

        // Set up the toolbar, scan devices, save settings, set footer, and perform permission checks.
        setToolbar();
        scanDevices();
        onSaveSettings();
        setFooter();
        connect();
        checkPermissions();
        checkManageExternalStoragePermission();
        Log.d(TAG,"Exiting onCreate()");
    }

    /**
     * Checks if the app has the necessary permission to manage external storage.
     * If not, prompts the user to grant the permission.
     */
    private void checkManageExternalStoragePermission() {
        Log.d(TAG,"Entering checkManageExternalStoragePermission()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
        Log.d(TAG,"Exiting checkManageExternalStoragePermission()");
    }

    /**
     * Checks and requests necessary permissions(Storage & Bluetooth) for the app.
     */
    private void checkPermissions() {
        Log.d(TAG,"Entering checkPermissions()");
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"WRITE_EXTERNAL_STORAGE permission not granted. Requesting permission...");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"BLUETOOTH_SCAN permission not granted. Requesting permission...");
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
        Log.d(TAG,"Exiting checkPermissions()");
    }

    /**
     * Enables or disables the logging option and its related views based on the checkbox state.
     *
     * @param isTcpLOgOptionEnabled Boolean value indicating whether the logging option is enabled.
     */
    private void enableDisableLogOptionComView(boolean isTcpLOgOptionEnabled) {
        Log.d(TAG,"Entering enableDisableLogOptionComView()");
        if (isTcpLOgOptionEnabled) {
            Log.i(TAG,"Log option is enabled. Enabling UI elements...");
            logRetainEt.setBackgroundResource(R.drawable.circular_border_white);
            logRetainEt.setEnabled(true);

            logLevelEt.setBackgroundResource(R.drawable.circular_border_white);
            logLevelEt.setEnabled(true);

            logPathEt.setBackgroundResource(R.drawable.circular_border_white);
            logPathEt.setEnabled(false);
        } else {
            // Enable the fields
            Log.i(TAG,"Log option is disabled. Disabling UI elements...");
            logRetainEt.setBackgroundResource(R.drawable.circular_border_grey);
            logRetainEt.setEnabled(false);

            logLevelEt.setBackgroundResource(R.drawable.circular_border_grey);
            logLevelEt.setEnabled(false);

            logPathEt.setBackgroundResource(R.drawable.circular_border_grey);
            logPathEt.setEnabled(false);
        }
        Log.d(TAG,"Exiting enableDisableLogOptionComView()");
    }

    /**
     * Handles the result of the activity for picking a log folder.
     *
     * @param requestCode The request code of the activity.
     * @param resultCode  The result code of the activity.
     * @param data        The intent containing the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"Entering onActivityResult()");
        if (requestCode == REQUEST_MANAGE_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    handleActivityResult(data);
                } else {
                    Log.w(TAG,"Access Required");
                }
            } else {
                handleActivityResult(data);
            }
        }
        Log.d(TAG,"Exiting onActivityResult()");
    }

    /**
     * Handles the result of the activity for picking a log folder.
     * This method is called when the user selects a folder using the file explorer.
     * It extracts the selected folder's URI from the result intent and updates the log path
     * EditText with the selected folder's path.
     *
     * @param data The intent containing the result data from the file explorer activity.
     */
    private void handleActivityResult(Intent data) {
        Log.d(TAG,"Entering handleActivityResult()");
        Uri fileUri = data.getData();
        if (fileUri != null) {
            String folderPath = fileUri.getPath().replace("/tree/primary:", "");
            logPathEt.setText(folderPath);
            Log.i(TAG,"File URI selected and folder path set: " + folderPath);
        } else {
            Log.e(TAG,Constants.INVALID_FILE_URI_MSG);
        }
        Log.d(TAG,"Exiting handleActivityResult()");
    }


    /**
     * Sets the initial values of UI elements based on configuration data.
     */
    private void setSettingValues() {
        Log.d(TAG,"Entering setSettingValues()");
        ConfigData configData = new ConfigData();
        PosLibManager.getInstance().getConfiguration(configData);

        scanDevicetcpIp.setText(configData.getTcpIP());
        scanDevicePort.setText(configData.getTcpPort());
        scanbtname.setText(configData.getBtName());
        scanbtssid.setText(configData.getBtSSID());
        connectionTimeOut.setText(String.valueOf(configData.getConnectionTimeOut()));
        logOptionCheckBox.setChecked(configData.isLogsEnabled());
        logPathEt.setText(configData.getLogPath());
        logRetainEt.setText(String.valueOf(configData.getDayToRetainLogs()));
        logLevelEt.setText(String.valueOf(configData.getLogLevel()));
        connectionRetry.setText(String.valueOf(configData.getRetryCount()));
        ((CheckBox) findViewById(R.id.connection_FallBack_CheckBox)).setChecked(configData.isConnectivityFallBackAllowed());

        if (configData.getCommP1() == Constants.TCP_IP) {
            prioritySpinnerTv.setSelection(0);
        } else if (configData.getCommP1() == Constants.BT) {
            prioritySpinnerTv.setSelection(1);
        } else {
            prioritySpinnerTv.setSelection(2);
        }

        Log.i(TAG, "TCP IP: " + configData.getTcpIP());
        Log.i(TAG, "TCP Port: " + configData.getTcpPort());
        Log.i(TAG, "BT Name: " + configData.getBtName());
        Log.i(TAG, "BT SSID: " + configData.getBtSSID());
        Log.i(TAG, "Connection Timeout: " + configData.getConnectionTimeOut());
        Log.i(TAG, "Logs Enabled: " + configData.isLogsEnabled());
        Log.i(TAG, "Log Path: " + configData.getLogPath());
        Log.i(TAG, "Day to Retain Logs: " + configData.getDayToRetainLogs());
        Log.i(TAG, "Log Level: " + configData.getLogLevel());
        Log.i(TAG, "Connection Retry Count: " + configData.getRetryCount());
        Log.i(TAG, "Connectivity FallBack Allowed: " + configData.isConnectivityFallBackAllowed());
        Log.i(TAG, "Communication Priority: " + configData.getCommP1());

        enableDisableLogOptionComView(configData.isLogsEnabled());
        Log.d(TAG,"Exiting setSettingValues()");
    }

    /**
     * Checks if a given string is not null and not empty.
     *
     * This method takes a string input and verifies if it is not null and contains at least one character.
     *
     * @param input The string to be checked for null or empty.
     * @return {@code true} if the input string is not null and not empty, {@code false} otherwise.
     */
    boolean notNullNotEmpty(String input) {
        Log.d(TAG,"Entering notNullNotEmpty()");
        return (input != null && (!input.isEmpty()));
    }

    /**
     * Saves the entered values into the configuration data.
     */
    private void saveValues() {
        Log.d(TAG,"Entering saveValues()");
        Log.i(TAG,"Saving values to ConfigData...");
        ConfigData configData = new ConfigData();
        PosLibManager.getInstance().getConfiguration(configData);

        if (scanDevicetcpIp != null) {
            configData.setTcpIP(scanDevicetcpIp.getText().toString());
            Log.i(TAG,"TCP IP set: " + scanDevicetcpIp.getText().toString());
        }
        if (scanDevicePort != null) {
            configData.setTcpPort(scanDevicePort.getText().toString());
            Log.i(TAG,"TCP Port set: " + scanDevicePort.getText().toString());
        }
        if (scanbtname != null) {
            configData.setBtName(scanbtname.getText().toString());
            Log.i(TAG,"BT Name set: " + scanbtname.getText().toString());
        }
        if (deviceID != null) {
            configData.setDeviceId(deviceID);
            Log.i(TAG,"Device Id set: " + deviceID);
        }
        if (deviceSlno != null) {
            configData.setDeviceSlNo(deviceSlno);
            Log.i(TAG,"Device Sl No set: " + deviceSlno);
        }
        if (scanbtssid != null) {
            configData.setBtSSID(scanbtssid.getText().toString().toUpperCase());
            Log.i(TAG,"BT SSID set: " + scanbtssid.getText().toString());
        }
        if (logPathEt.getText().length() > 0) {
            configData.setLogPath(logPathEt.getText().toString());
            Log.i(TAG,"Log Path set: " + logPathEt.getText().toString());
        }

        String logRetainValue = logRetainEt.getText().toString();

        if (notNullNotEmpty(logRetainValue)) {
            configData.setDayToRetainLogs(Integer.parseInt((logRetainValue)));
            Log.i(TAG,"Day to Retain Logs set: " + logRetainValue);
        }

        String logLevelValue = logLevelEt.getText().toString();

        if (notNullNotEmpty(logLevelValue)) {
            configData.setLogLevel(Integer.parseInt(logLevelValue));
            Log.i(TAG,"Log Level set: " + logLevelValue);
        }

        if (connectionTimeOut.getText().length() > 0) {
            configData.setConnectionTimeOut(Integer.parseInt(connectionTimeOut.getText().toString()));
            Log.i(TAG,"Connection timeout set: " + connectionTimeOut.getText().toString());
        }
        if (connectionRetry.getText().length() > 0) {
            configData.setRetryCount(Integer.parseInt(connectionRetry.getText().toString()));
            Log.i(TAG,"Connection retry set: " + connectionRetry.getText().toString());
        }

        configData.setConnectivityFallBackAllowed(((CheckBox) findViewById(R.id.connection_FallBack_CheckBox)).isChecked());

        if (prioritySpinnerTv.getSelectedItem().toString().contains("TCPIP")) {
            configData.setCommP1(Constants.TCP_IP);
            configData.setCommP2(Constants.BT);
        } else if (prioritySpinnerTv.getSelectedItem().toString().contains("BT")) {
            configData.setCommP1(Constants.BT);
            configData.setCommP2(Constants.TCP_IP);
        } else {
            configData.setCommP1(Constants.APPTOAPP);
        }
        configData.setLogsEnabled(logOptionCheckBox.isChecked());
        Log.i(TAG,"Logs Enabled: " + logOptionCheckBox.isChecked());
        PosLibManager.getInstance().setConfiguration(configData,this);
        Log.i(TAG,"Values saved successfully.");
        Log.d(TAG,"Exiting saveValues()");
    }

    /**
     * Initializes and configures the UI components related to device scanning and connection.
     * Sets up listeners for buttons related to device scanning and connection.
     * Manages the progress dialog for device operations.
     */
    public void scanDevices() {
        Log.d(TAG,"Entering scanDevices()");
        ((Button) findViewById(R.id.tcpIp_Scan_Btn)).setOnClickListener(view -> {
            Log.i(TAG,"TCP/IP Scan button clicked. Initiating device scan...");
            showProgress("Searching");
            handler.postDelayed(() -> PosLibManager.getInstance().scanOnlinePOSDevice(scanDeviceListener, getApplicationContext()), 1000);

        });
        ((Button) findViewById(R.id.scan_Device_Bt)).setOnClickListener(view -> {
            Log.i(TAG,"Bluetooth Scan button clicked. Initiating device scan...");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG,"Bluetooth permission not granted. Prompting user...");
                Toast.makeText(this, "Please allow Bluetooth permissions", Toast.LENGTH_SHORT).show();
            } else {
                showProgress("Searching");
                handler.postDelayed(() -> PosLibManager.getInstance().scanBTDevice(scanDeviceListener, getApplicationContext()), 1000);
            }
        });
        Log.d(TAG,"Exiting scanDevices()");
    }

    /**
     * Handles the device connection logic.
     * Sets up listeners for buttons related to connecting devices.
     * Manages the progress dialog for the connection process.
     */
    private void connect() {

        Log.d(TAG,"Entering connect()");
        ((Button) findViewById(R.id.tcpIp_Port_Connect_Btn)).setOnClickListener(view -> {
            Log.i(TAG,"TCP/IP Connect button clicked. Initiating device connection...");
            String ipAddress = scanDevicetcpIp.getText().toString().trim();
            String portNum = scanDevicePort.getText().toString().trim();

            if (ipAddress.isEmpty() || portNum.isEmpty()) {
                Log.w(TAG,"Please select a device before connecting.");
                showToastMessage("Please Select Device");
                return;
            }
            showProgress("Connecting");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> handleDeviceConnection(PosLibManager.getInstance().testTCP(scanDevicetcpIp.getText().toString(), scanDevicePort.getText().toString())));
            executorService.shutdown();

        });

        // Define an ExecutorService with a single thread
        ExecutorService executor = Executors.newSingleThreadExecutor();

            btConnectBtn.setOnClickListener(view -> {
            btConnectBtn.setEnabled(false);
            String scanBtName = scanbtname.getText().toString().trim();
            String scanBtSsid = scanbtssid.getText().toString().trim();
            Log.i(TAG,"BT Connect button clicked. Initiating device connection...");
            if (scanBtName.isEmpty() || scanBtSsid.isEmpty()) {
                Log.w(TAG,"Please select a device before connecting.");
                showToastMessage("Please Select Device");
                btConnectBtn.setEnabled(true);
                return;
            }
            showProgress("Connecting");

            executor.execute(() -> {
                boolean isConnected = PosLibManager.getInstance().testBT(scanBtSsid);

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    dismissProgress();
                    showToastMessage(isConnected ? "Device Connected!!!" : "Failed to Connect");
                    btConnectBtn.setEnabled(true); // Re-enable the button
                });
            });
        });
    }
    private void handleDeviceConnection(boolean isConnected) {
        runOnUiThread(() -> {
            dismissProgress();
            if (isConnected) {
                showToastMessage("Device Connected!!!");
            } else {
                showToastMessage("Failed to Connect");
            }
        });
        Log.d(TAG,"Exiting connect()");
    }

    /**
     * Displays a progress dialog with the specified message.
     *
     * @param msg The message to display in the progress dialog.
     */
    private void showProgress(String msg) {
        if (!isFinishing() && !isDestroyed()) {
            Log.d(TAG, "Entering showProgress()");
            Log.i(TAG, "Showing progress dialog with message: " + msg);

            AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(AppSettingActivity.this);
            settingsBuilder.setTitle(msg);
            settingsBuilder.setView(R.layout.progress_bar);
            settingsBuilder.setCancelable(false);

            if (progressAlertDialog != null && progressAlertDialog.isShowing()) {
                // Dismiss the existing dialog to avoid the "WindowLeaked" error
                progressAlertDialog.dismiss();
            }

            progressAlertDialog = settingsBuilder.create();
            Log.d(TAG, "Exiting showProgress()");
            progressAlertDialog.show();
        }
    }

    /**
     * Dismisses the progress dialog if it's currently showing.
     */
    private void dismissProgress() {
        Log.d(TAG,"Entering dismissProgress()");

        if ((progressAlertDialog != null) && (progressAlertDialog.isShowing())) {
            progressAlertDialog.dismiss();
        }
        Log.d(TAG,"Exiting dismissProgress()");
    }

    /**
     * Sets up the listener for the "Save Settings" button.
     * Handles the logic for saving configuration values and storing logs if enabled.
     */
    public void onSaveSettings() {
        Log.d(TAG,"Entering onSaveSettings()");
        ((Button) findViewById(R.id.save_settings)).setOnClickListener(view -> {
            saveValues();
            Log.i(TAG,"Config Saved");
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Config Saved!!", Toast.LENGTH_LONG).show());
            handler.postDelayed(() -> Navigator.getInstance().navigate2Main(AppSettingActivity.this), 1000);

        });
        Log.d(TAG,"Exiting onSaveSettings()");
    }

    /**
     * Displays a dialog that allows the user to select a device from a list of available devices.
     *
     * @param activity The activity context in which the dialog should be displayed.
     * @param list     The list of available devices to display in the dialog.
     */
    public void showDialog(Activity activity, List<DeviceDetails> list) {
        Log.d(TAG,"Entering showDialog()");
        deviceLists = list;
        dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.device_recycler_layout);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        Button cancelBtn = (Button) dialog.findViewById(R.id.cancelButton);
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        RecyclerView recyclerView = dialog.findViewById(R.id.device_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        ScanDevicesAdapter scanDevicesAdapter = new ScanDevicesAdapter(AppSettingActivity.this, deviceLists, selectDeviceListener);
        recyclerView.setAdapter(scanDevicesAdapter);
        dialog.show();
        Log.d(TAG,"Exiting showDialog()");
    }

    /**
     * Listener for device selection events in the device selection dialog.
     * Handles updating UI elements based on the selected device.
     */
    OnSelectDeviceListener selectDeviceListener = new OnSelectDeviceListener() {
        @Override
        public void onSuccess(int itemSelected, List<DeviceDetails> devices) {

            Log.i(TAG,"Selected index" + Integer.toString(itemSelected));
            if (devices.get(itemSelected).isBtDevice()) {
                Log.i(TAG,"Selected device ssid" + deviceLists.get(itemSelected).getBtDeviceSsid());
                Log.i(TAG,"Selected device Bt name" + deviceLists.get(itemSelected).getBtDeviceName());
                scanbtname.setText(deviceLists.get(itemSelected).getBtDeviceName());
                scanbtssid.setText(deviceLists.get(itemSelected).getBtDeviceSsid());
            } else {
                Log.i(TAG,"Selected device IP" + deviceLists.get(itemSelected).getDeviceIp());
                Log.i(TAG,"Selected device port" + deviceLists.get(itemSelected).getDevicePort());
                scanDevicetcpIp.setText(deviceLists.get(itemSelected).getDeviceIp());
                scanDevicePort.setText(deviceLists.get(itemSelected).getDevicePort());
                deviceID = deviceLists.get(itemSelected).getDeviceId();
                deviceSlno = deviceLists.get(itemSelected).getDeviceSlNo();
            }

            dialog.dismiss();
        }

        @Override
        public void onFailure(String errorMsg, int errorCode) {
            // onFailure Method
        }
    };

    /**
     * Listener for device scanning events.
     * Handles updating UI elements based on the scan result.
     */
    ScanDeviceListener scanDeviceListener = new ScanDeviceListener() {

        @Override
        public void onSuccess(List<DeviceDetails> list) {

            dismissProgress();
            Log.i(TAG,"scanDeviceListener" + "onSuccess");
            runOnUiThread(() -> showDialog(AppSettingActivity.this, list));
        }

        @Override
        public void onFailure(String errorMsg, int errorCode) {
            dismissProgress();
            Log.i(TAG,"onFailure" + "on failure");
            Log.i(TAG,"onFailure" + errorMsg);
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), errorCode + " : "+ errorMsg, Toast.LENGTH_LONG).show());
        }
    };
}