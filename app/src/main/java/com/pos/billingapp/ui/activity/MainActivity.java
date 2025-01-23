package com.pos.billingapp.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// this is your code.. do whatever u wanna do wit this... cool..

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pos.billingapp.R;
import com.pos.billingapp.enums.TransactionType;
import com.pos.billingapp.navigation.Navigator;
import com.pos.poslib.callback.ComEventListener;
import com.pos.poslib.callback.TransactionListener;
import com.pos.billingapp.constants.Constants;
import com.pos.poslib.model.ConfigData;
import com.pos.poslib.poslibmanager.PosLibManager;
import com.google.android.material.textfield.TextInputEditText;
import com.pos.billingapp.utils.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main activity class that handles user interactions for making transactions,
 * managing settings, and displaying relevant information.
 */
@java.lang.SuppressWarnings("squid:MaximumInheritanceDepth")
public class MainActivity extends BaseActivity {
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    AutoCompleteTextView transTypeAtv;
    TextView cashierIdTv;
    TextView cashierNameTv;
    Button transBtn;
    private String requestBody = "TXNTYP,TX12345678,AMT,,,,,,,";
    private AlertDialog progressAlertDialog = null;
    TextInputEditText totalAmountEt;
    String cashierId;
    String cashierName;
    String amount;
    private static final String TXNTYP = "TXNTYP";
    private SharedPreferences cashierSharedPref;
    private static boolean isTCPActive = false;
    private static boolean isBtActive = false;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, MainActivity.class);
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
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Entering onCreate");
        checkPermissions();
        checkBluetoothPermission();
        cashierIdTv = findViewById(R.id.cashierId);
        cashierNameTv = findViewById(R.id.cashierName);

        ((ImageView) findViewById(R.id.settings)).setVisibility(View.VISIBLE);

        ((Toolbar) findViewById(R.id.settingToolbar)).setVisibility(View.INVISIBLE);
        ((Toolbar) findViewById(R.id.posToolbar)).setVisibility(View.INVISIBLE);

        ((Toolbar) findViewById(R.id.toolbar)).setVisibility(View.VISIBLE);

        // Set up transaction type spinner
        transTypeAtv = findViewById(R.id.trans_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.trans_type_items, android.R.layout.simple_dropdown_item_1line);

        transTypeAtv.setAdapter(adapter);

        totalAmountEt = (TextInputEditText) findViewById(R.id.amountEt);
        PosLibManager.getInstance().posLibInitialize(getApplicationContext());

        transBtn = (Button) findViewById(R.id.trans);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        setToolbar();
        selectTransaction();
        setEditButton();
        setPriorityTcpBtDetails();
        setFooter();
        onBackPress();

        // Set up transaction button click listener
        transBtn.setOnClickListener(view -> {

            Log.i(TAG, "Setting up transaction button click listener");
            String request = "";
            int trxnType = 6;
            String txnType = transTypeAtv.getText().toString();

            if (totalAmountEt.getText() != null && totalAmountEt.getText().length() > 0) {
                amount = totalAmountEt.getText().toString().replace(".", "");
            } else if (!txnType.contains(Constants.LAST_TRANSACTION) && !txnType.contains(Constants.SETTLEMENT)) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Please enter Amount", Toast.LENGTH_LONG).show());
                return;
            }

            TransactionType transactionType = TransactionType.fromString(txnType);

            if (transactionType == TransactionType.SETTLEMENT) {
                request = "6001,,,,,,,,,,,";
            } else if (transactionType != null) {
                request = requestBody.replace(Constants.AMT, amount);
                request = request.replace(TXNTYP, transactionType.getValue());
            } else {
                Log.i(TAG, "No Transaction type found ");
            }

            Log.i(TAG, "request" + request);
            runOnUiThread(this::showProgress);
            transBtn.setEnabled(false);
            String finalRequest = getPaymentPacket(request);
            int finalTxnType = trxnType;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> PosLibManager.getInstance().doTransaction(MainActivity.this, finalRequest, finalTxnType, new TransactionListener() {
                    @Override
                    public void onSuccess(String paymentResponse) {
                        Log.i(TAG, "transactionListener" + "onSuccess");
                        dismissProgress();
                        Intent intent = new Intent(MainActivity.this, DisplayResponseActivity.class);
                        intent.putExtra("paymentResponse", StringUtils.hexStringToBCD(paymentResponse)); // Pass paymentResponse as an extra
                        startActivity(intent);
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Response received", Toast.LENGTH_LONG).show());
                        Log.i(TAG, "paymentResponse" + paymentResponse);
                    }

                    @Override
                    public void onFailure(String errorMsg, int errorCode) {
                        Log.i(TAG,"transactionListener" + "onFailure Listener error " + errorMsg + ":" + errorCode);
                        dismissProgress();
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), errorCode + " : "+ errorMsg, Toast.LENGTH_LONG).show());
                    }
                }));
                transBtn.setEnabled(true);
                executor.shutdown();
            }, 100);
        });
        if (isBtActive) {
            Log.i(TAG, "BT is Active");
            runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn_bt)).setVisibility(View.GONE));
            runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn_bt)).setVisibility(View.VISIBLE));
        } else {
            runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn_bt)).setVisibility(View.GONE));
            runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn_bt)).setVisibility(View.VISIBLE));
        }
        if (isTCPActive) {
            Log.i(TAG, "TCP/IP is Active");
            runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn)).setVisibility(View.GONE));
            runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn)).setVisibility(View.VISIBLE));
        } else {
            runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn)).setVisibility(View.GONE));
            runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn)).setVisibility(View.VISIBLE));
        }

        // Check communication status using PosLibManager
        Log.i(TAG, "Checking communication status");
        // time interval should be minimum 30 sec
        runOnUiThread(() -> PosLibManager.getInstance().checkBtComStatus(btComEventListener, getApplicationContext(), 30));
        runOnUiThread(() -> PosLibManager.getInstance().checkTcpComStatus(tcpComEventListener, getApplicationContext(), 30));

    }

    /**
     * Checks for necessary permissions related to storage.
     */
    private void checkPermissions() {
        Log.d(TAG,"Entering checkPermissions()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"Requesting storage permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
        Log.d(TAG,"Exiting checkPermissions()");
    }

    /**
     * Handles permission request results.
     *
     * @param requestCode The request code.
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,"Entering onRequestPermissionsResult()");
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // statements yet to be implemented.
        }
        Log.d(TAG,"Exiting onRequestPermissionsResult()");
    }

    /**
     * Constructs the payment packet for a given CSV data.
     *
     * @param csvData The CSV data to be included in the packet.
     * @return The constructed payment packet as a hexadecimal string.
     */
    private String getPaymentPacket(String csvData) {
        Log.d(TAG,"Entering getPaymentPacket()");
        if (csvData != null && !csvData.isEmpty()) {
            int iOffset = 0;
            byte[] msgBytes = csvData.getBytes();
            int iCSVLen = msgBytes.length;
            int finalMsgLen = iCSVLen + 7;
            // 7 = 2 byte source , 2 byte function code, 2 byte length, 1 byte termination
            byte[] msgBytesExtra = new byte[finalMsgLen];
            //source id - 2 bytes
            msgBytesExtra[iOffset] = 0x10;
            iOffset++;
            msgBytesExtra[iOffset] = 0x00;
            iOffset++;
            //function code or MTI - 2 bytes
            msgBytesExtra[iOffset] = 0x09;
            iOffset++;
            msgBytesExtra[iOffset] = (byte) 0x97;
            iOffset++;
            //data length to follow
            msgBytesExtra[iOffset] = (byte) ((byte) (iCSVLen >> 8) & 0xFF);
            iOffset++;
            msgBytesExtra[iOffset] = (byte) (iCSVLen & 0xFF);
            iOffset++;
            System.arraycopy(msgBytes, 0, msgBytesExtra, iOffset, msgBytes.length);
            iOffset += msgBytes.length;
            msgBytesExtra[iOffset] = (byte) 0xFF;
            Log.i(TAG,StringUtils.bytesToHex(msgBytesExtra));
            Log.d(TAG,"Exiting getPaymentPacket()");
            return StringUtils.bytesToHex(msgBytesExtra);
        } else {
            Log.d(TAG,"Exiting getPaymentPacket()");
            return "";
        }
    }

    /**
     * Event listener for communication status events.
     */
    ComEventListener btComEventListener = eventId -> {
        Log.d(TAG,"Entering ComEventListener()");
        Log.i(TAG,"Entering comEventListener on onEvent : " + eventId);

        switch (eventId) {
            case 3000:
                isTCPActive = false;
                Log.i(TAG,"in comEventListener Payment app down ");
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn)).setVisibility(View.VISIBLE));
                isBtActive = false;
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn_bt)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn_bt)).setVisibility(View.VISIBLE));
                break;
            case 2000:
                Log.i(TAG,"Bluetooth connected: Event ID 2000");
                isBtActive = true;
                Log.i(TAG,"in comEventListener BT connected ");
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn_bt)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn_bt)).setVisibility(View.VISIBLE));
                break;
            case 2001:
            case 2002:
                isBtActive = false;
                Log.i(TAG,"in comEventListener BT dis-connected ");
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn_bt)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn_bt)).setVisibility(View.VISIBLE));
                break;
            default:
                Log.i(TAG,"Unknown Event ID: " + eventId);
                break;
        }
    };

    /**
     * Event listener for communication status events.
     */
    ComEventListener tcpComEventListener = eventId -> {
        Log.d(TAG,"Entering tcpComEventListener()");
        Log.i(TAG,"Entering tcpComEventListener on onEvent : " + eventId);

        switch (eventId) {
            case 3000:
                isTCPActive = false;
                Log.i(TAG,"in comEventListener Payment app down ");
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn)).setVisibility(View.VISIBLE));
                isBtActive = false;
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn_bt)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn_bt)).setVisibility(View.VISIBLE));
                break;
            case 1000:
                Log.i(TAG,"TCP/IP connected: Event ID 1000");
                isTCPActive = true;
                Log.i(TAG,"in comEventListener TCPIP connected ");
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn)).setVisibility(View.VISIBLE));
                break;
            case 1001:
            case 1002:
            case 1003:
                isTCPActive = false;
                Log.i(TAG,"in comEventListener TCPIP dis-connected ");
                runOnUiThread(() -> ((Button) findViewById(R.id.activate_Btn)).setVisibility(View.GONE));
                runOnUiThread(() -> ((Button) findViewById(R.id.inActivate_Btn)).setVisibility(View.VISIBLE));
                break;
            default:
                Log.i(TAG,"Unknown Event ID: " + eventId);
                break;
        }
    };

    /**
     * Sets up the toolbar UI component.
     */
    @Override
    public void setToolbar() {
        findViewById(R.id.settings).setOnClickListener(view -> Navigator.getInstance().navigate2AppSetting(MainActivity.this));
    }

    /**
     * Handles the selection of a transaction type.
     */
    public void selectTransaction() {
        ((TextView) findViewById(R.id.trans)).setOnClickListener(view -> {
        });
    }

    /**
     * Customizes the behavior of the back button press.
     */
    private void onBackPress() {
        Log.d(TAG,"Entering onBackPress()");
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
        Log.d(TAG,"Exiting onBackPress()");
    }

    /**
     * Resumes the activity and updates UI components.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"Entering onResume()");
        cashierSharedPref = getSharedPreferences(Constants.POS_LIB_PREFS, Context.MODE_PRIVATE);
        String savedCashierId = cashierSharedPref.getString(Constants.CASHIER_ID, "");
        cashierIdTv.setText(savedCashierId);
        Log.i(TAG,"onResume: Retrieved and set Cashier ID: " + savedCashierId);
        String savedCashierName = cashierSharedPref.getString(Constants.CASHIER_NAME, "");
        cashierNameTv.setText(savedCashierName);
        Log.i(TAG,"onResume: Retrieved and set Cashier Name: " + savedCashierName);
        setPriorityTcpBtDetails();
        Log.d(TAG,"Exiting onResume()");
    }

    /**
     * Displays a progress dialog for ongoing operations.
     */
    private void showProgress() {
        Log.d(TAG,"Entering showProgress()");
        Log.i(TAG,"Showing progress dialog...");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(R.layout.progress_bar);
        builder.setCancelable(false);
        progressAlertDialog = builder.create();
        Log.d(TAG,"Exiting showProgress()");
        progressAlertDialog.show();
    }

    /**
     * Dismisses the progress dialog.
     */
    private void dismissProgress() {
        Log.d(TAG,"Entering dismissProgress()");
        if ((progressAlertDialog != null) && (progressAlertDialog.isShowing())) {
            progressAlertDialog.dismiss();
        }
        Log.d(TAG,"Exiting dismissProgress()");
    }

    /**
     * Sets up the edit button functionality.
     */
    public void setEditButton() {
        Log.d(TAG,"Entering setEditButton()");
        ((Button) findViewById(R.id.editEt)).setOnClickListener(view -> {
            Log.i(TAG, "Edit button clicked. Showing edit dialog...");
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.edit_cashier_details_layout);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cornered_dialog));
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            EditText cashierIdDialogEt = dialog.findViewById(R.id.cashier_Id_Et);
            EditText cashierNameDialogEt = dialog.findViewById(R.id.cashier_name_Et);

            ConfigData configData = new ConfigData();
            PosLibManager.getInstance().getConfiguration(configData);

            cashierIdDialogEt.setText(configData.getCashierID());
            cashierNameDialogEt.setText(configData.getCashierName());
            cashierSharedPref = getSharedPreferences(Constants.POS_LIB_PREFS, Context.MODE_PRIVATE);

            ((Button) dialog.findViewById(R.id.saveDialog)).setOnClickListener(v -> {
                Log.i(TAG,"Save button clicked in edit dialog...");
                if (cashierIdDialogEt.getText() != null && cashierIdDialogEt.getText().length() > 0) {
                    cashierId = cashierIdDialogEt.getText().toString();
                    cashierIdTv.setText(cashierId);
                    SharedPreferences.Editor editor = cashierSharedPref.edit();
                    editor.putString("cashierId", cashierId);
                    editor.apply();
                    cashierIdTv.setText(cashierId);
                    dialog.dismiss();
                    Log.i(TAG,"Cashier ID updated: " + cashierId);
                } else {
                    Log.e(TAG,Constants.CASHIER_ID_EMPTY_MSG);
                    showToast("Please enter Cashier Id");
                }
                if (cashierNameDialogEt.getText() != null && cashierNameDialogEt.getText().length() > 0) {
                    cashierName = cashierNameDialogEt.getText().toString();
                    cashierNameTv.setText(cashierName);
                    SharedPreferences.Editor editor = cashierSharedPref.edit();
                    editor.putString("cashierName", cashierName);
                    editor.apply();
                    cashierNameTv.setText(cashierName);
                    dialog.dismiss();
                    Log.i(TAG, "Cashier Name updated: " + cashierName);
                } else {
                    Log.e(TAG,Constants.CASHIER_NAME_EMPTY_MSG);
                    showToast("Please enter Cashier Name");
                }
            });

            ((Button) dialog.findViewById(R.id.cancelDialog)).setOnClickListener(view1 -> dialog.dismiss());

            cashierId = cashierSharedPref.getString(Constants.CASHIER_ID, "");
            cashierIdDialogEt.setText(cashierId);

            cashierName = cashierSharedPref.getString(Constants.CASHIER_NAME, "");
            cashierNameDialogEt.setText(cashierName);
            dialog.show();
        });
        Log.d(TAG,"Exiting setEditButton()");
    }

    /**
     * Displays a short toast message.
     *
     * @param msg The message to be displayed.
     */
    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates and displays priority settings for communication channels.
     */
    private void setPriorityTcpBtDetails() {
        Log.d(TAG,"Entering setPriorityTcpBtDetails()");
        Log.i(TAG,"Setting Priority TCP/BT Details...");
        PosLibManager.getInstance().posLibInitialize(getApplicationContext());
        ConfigData configData = new ConfigData();
        PosLibManager.getInstance().getConfiguration(configData);

        //TCP details
        ((TextView) findViewById(R.id.deviceIdTv)).setText(configData.getDeviceId());
        ((TextView) findViewById(R.id.slnoAddTv)).setText(configData.getDeviceSlNo());
        ((TextView) findViewById(R.id.ipAddTv)).setText(configData.getTcpIP());
        ((TextView) findViewById(R.id.portAddTv)).setText(configData.getTcpPort());
        ((TextView) findViewById(R.id.portAddTv)).setText(configData.getTcpPort());
        if (configData.getCommP1() == 1) {
            ((TextView) findViewById(R.id.priorityAddTv)).setText("1");
        } else {
            ((TextView) findViewById(R.id.priorityAddTv)).setText("2");
        }

        //BT Details
        ((TextView) findViewById(R.id.deviceIdTvBt)).setText(configData.getBtName());
        ((TextView) findViewById(R.id.ssidAddTvBt)).setText(configData.getBtSSID());

        if (configData.getCommP1() == 2) {
            ((TextView) findViewById(R.id.priorityAddTvBt)).setText("1");
        } else {
            ((TextView) findViewById(R.id.priorityAddTvBt)).setText("2");
        }
        Log.i(TAG,"Priority TCP/BT Details Set Successfully");
        Log.d(TAG,"Exiting setPriorityTcpBtDetails()");

    }

    /**
     * Checks and requests Bluetooth permissions if needed.
     */
    public void checkBluetoothPermission() {
        Log.d(TAG,"Entering checkBluetoothPermission()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{android.Manifest.permission.BLUETOOTH}, 1
                );
            }

            int btConnectPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
            if (btConnectPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT},
                        1
                );
            }
            Log.d(TAG,"Exiting checkBluetoothPermission()");
        }
    }
}