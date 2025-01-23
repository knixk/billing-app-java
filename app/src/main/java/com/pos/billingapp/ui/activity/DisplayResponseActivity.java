package com.pos.billingapp.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pos.billingapp.R;
import com.pos.billingapp.utils.StringUtils;

public class DisplayResponseActivity extends BaseActivity {
    private static final String TAG = "DisplayResponseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_response);

        setFooter();

        byte[] successStatusCode = {0x00, 0x01};

        findViewById(R.id.toolbar).setVisibility(View.GONE);
        findViewById(R.id.settingToolbar).setVisibility(View.GONE);

        byte[] paymentResponse = getIntent().getByteArrayExtra("paymentResponse");

        // source id
        byte[] srcId = new byte[2];
        System.arraycopy(paymentResponse, 0, srcId, 0, 2);

        // function code
        byte[] fncCode = new byte[2];
        System.arraycopy(paymentResponse, 2, fncCode, 0, 2);

        //error code
        byte[] errCode = new byte[2];
        System.arraycopy(paymentResponse, 4, errCode, 0, 2);

        //data length
        byte[] dataLength = new byte[2];
        System.arraycopy(paymentResponse, 6, dataLength, 0, 2);


        int length = Integer.parseInt(StringUtils.bytesToHex(dataLength),16);

        if (length <= 0 || paymentResponse.length < 9 + length) {
            Log.e(TAG, "Invalid data length or insufficient data");
            return;
        }

        // CSV data
        byte[] csvHexData = new byte[length-1];
        System.arraycopy(paymentResponse, 9, csvHexData, 0, length-1);
        String csvData = StringUtils.hexToAscii(StringUtils.bytesToHex(csvHexData));

        TextView sourceId = findViewById(R.id.tvSourceIdValue);
        TextView functionCode = findViewById(R.id.tvFunctionCodeValue);
        TextView errorCode = findViewById(R.id.tvShowErrorCodeData);

        TextView msg= findViewById(R.id.tvCsvData);

        sourceId.setText(StringUtils.bytesToHex(srcId));
        functionCode.setText(StringUtils.bytesToHex(fncCode));
        errorCode.setText(StringUtils.bytesToHex(errCode));

        Log.i(TAG, "CSv Data is : " + csvData);

        if (successStatusCode[1] != errCode[1] ) {
            msg.setText(csvData);
            msg.setTextColor(Color.RED);
            findViewById(R.id.svData).setVisibility(View.GONE);
            findViewById(R.id.tvMsgLth).setVisibility(View.GONE);
            findViewById(R.id.tvShowMsgLthdata).setVisibility(View.GONE);
            return;
        }

        Log.i(TAG, "dataLength Data is : " + length);
        TextView msgLength = findViewById(R.id.tvShowMsgLthdata);
        msgLength.setText(String.valueOf(length));

        String[] csvArray = csvData.split(",");

        for (int i = 0; i < csvArray.length; i++) {
            TextView textView = getTextViewById(i);
            textView.setText(csvArray[i]);
        }
    }

    private TextView getTextViewById(int index) {
        switch (index) {
            case 0:
                return findViewById(R.id.billng_ref_value);
            case 1:
                return findViewById(R.id.approveCodeValue);
            case 2:
                return findViewById(R.id.hostResponseValue);
            case 3:
                return findViewById(R.id.cardNoValue);
            case 4:
                return findViewById(R.id.expdateValue);
            case 5:
                return findViewById(R.id.cardHolderNameValue);
            case 6:
                return findViewById(R.id.cardTypeValue);
            case 7:
                return findViewById(R.id.invNoValue);
            case 8:
                return findViewById(R.id.batchNoValue);
            case 9:
                return findViewById(R.id.terminalIdValue);
            case 10:
                return findViewById(R.id.loyalityPointsValue);
            case 11:
                return findViewById(R.id.remarkValue);
            case 12:
                return findViewById(R.id.acqNameVAlue);
            case 13:
                return findViewById(R.id.merchantIdValue);
            case 14:
                return findViewById(R.id.rrnValue);
            case 15:
                return findViewById(R.id.CardEntryMode);
            case 16:
                return findViewById(R.id.cardHolderName);
            case 17:
                return findViewById(R.id.mercahntNameValue);
            case 18:
                return findViewById(R.id.merchantAddressValue);
            case 19:
                return findViewById(R.id.merchantCity);
            case 20:
                return findViewById(R.id.plutusVersiom);
            case 21:
                return findViewById(R.id.AcqBAnkCode);
            case 22:
                return findViewById(R.id.rwrdRdmdAmnt);
            case 23:
                return findViewById(R.id.rwrdRdmdPoint);
            case 24:
                return findViewById(R.id.rwrdBalanceAmnt);
            case 25:
                return findViewById(R.id.rwrdBalancePoint);
            case 26:
                return findViewById(R.id.chargeSlip);
            case 27:
                return findViewById(R.id.rfu1);
            case 28:
                return findViewById(R.id.voidAmnt);
            case 29:
                return findViewById(R.id.rfu3);
            case 30:
                return findViewById(R.id.settleSummary);
            case 31:
                return findViewById(R.id.TrxnDate);
            case 32:
                return findViewById(R.id.TrxnTime);
            case 33:
                return findViewById(R.id.PinelabClientId);
            case 34:
                return findViewById(R.id.PinelabsBatchId);
            case 35:
                return findViewById(R.id.pinelabRoc);
            case 36:
                return findViewById(R.id.TrxnCurr);
            case 37:
                return findViewById(R.id.exchrate);
            case 38:
                return findViewById(R.id.dccAmount);
            case 39:
                return findViewById(R.id.traceNo);
            case 40:
                return findViewById(R.id.aidValue);
            case 41:
                return findViewById(R.id.tcrValue);
            case 42:
                return findViewById(R.id.arqcValue);

            default:
                throw new IllegalArgumentException("Invalid index: " + index);
        }

    }
}
