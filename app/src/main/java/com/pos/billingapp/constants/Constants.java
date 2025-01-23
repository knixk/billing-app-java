package com.pos.billingapp.constants;

/**
 * This class holds constant values used in the POS (Point of Sale) library for various purposes,
 * including communication protocols, transaction types, error codes, and error messages.
 */
public final class Constants {

    /**
     * Private constructor to prevent instantiation of the Constants class.
     */
    private Constants() {
        //constuctor
    }

    //Configuration Types
    public static final int TCP_IP = 1;
    public static final int BT = 2;
    public static final int APPTOAPP = 3;
    public static final String DEVICE_ID = "Device Id :  ";
    public static final String BT_SSID = "SSID     :   ";
    public static final String DEVICE_SLNO = "Serial No :  ";
    public static final String DEVICE_NAME = "Device Name :  ";
    public static final String POS_LIB_PREFS = "posLibPref";
    public static final String CASHIER_ID = "cashierId";
    public static final String CASHIER_NAME = "cashierName";

    //Transaction Types
    public static final String PURCHASE = "Purchase";
    public static final String UPI = "UPI";
    public static final String BRAND_EMI = "Brand EMI";
    public static final String VOID = "Void";
    public static final String REFUND = "Refund";
    public static final String BANK_EMI = "Bank EMI";
    public static final String PRE_AUTH = "Preauth";
    public static final String SALE_COMPLETE = "Sale Complete";
    public static final String BANK_EMI_SINGLE_TRANSACTION = "Bank Emi Single Transaction";
    public static final String ZEST_MONEY_BANK_EMI = "Zest Money Bank Emi";
    public static final String ZEST_MONEY_BRAND_EMI = "Zest Money Brand Emi";
    public static final String ZEST_MONEY_VOID = "Zest Money Void";
    public static final String HDFC_FLEXIPAY = "HDFC Flexipay";
    public static final String BHARAT_QR = "Bharat Qr";
    public static final String TWID_VOID = "Twid Void";
    public static final String AIRTEL_BANK_TRANSACTION = "Airtel Bank Transaction";
    public static final String COD_SALE = "Cod Sale";
    public static final String COD_VOID = "Cod Void";
    public static final String AMAZON_PAY = "Amazon Pay";
    public static final String GET_STATUS_OF_AMAZON_PAY_SALE_CHARGECASE = "Amazon Pay Sale ChargeCase";
    public static final String MY_EMI = "My Emi";
    public static final String TWID_INTEGRATION = "Twid Integration";
    public static final String QWIKCILVER_REDEMPTION = "Qwikcilver Redemption";
    public static final String CARDLESS_BANK_EMI = "CardLess Bank Emi";
    public static final String CARDLESS_BRAND_EMI = "CardLess Brand Emi";
    public static final String CARDLESS_BANK_AND_BRAND_EMI = "CardLess Bank and Brand Emi";
    public static final String GIFTIICON = "GiftIcon";
    public static final String MAGIC_PIN = "Magic Pin";
    public static final String MAGIC_PIN_VOID = "Magic Pin Void";
    public static final String REWARD_TRANSACTION = "Reward Transaction";
    public static final String PAYBACK_TRANSACTION = "Payback Transaction";
    public static final String VOID_PAYBACK_TRANSACTION = "Void Payback Transaction";
    public static final String THIRD_PARTY_WALLET_INTEGRATION = "Third Party Wallet Integration";
    public static final String VOID_OF_SALE_CHARGE = "Void Of Sale Charge";
    public static final String GET_STATUS_OF_SALE_CHARGE = "Get Status of Sale Charge";
    public static final String SODEXO_INTEGRATION = "Sodexo Integration";
    public static final String SODEXO_VOID_REQUEST = "Sodexo Void Request";
    public static final String PAPER_POS_INTEGRATION = "Paper Pos Integration";
    public static final String LAST_TRANSACTION = "Last Transaction";
    public static final String SETTLEMENT = "Settlement";
    public static final String AMT = "AMT";

    //Connection Error Message
    public static final String INVALID_FILE_URI_MSG = "Invalid file URI";
    public static final String CASHIER_ID_EMPTY_MSG = "Cashier ID IS Empty";
    public static final String CASHIER_NAME_EMPTY_MSG = "Cashier Name IS Empty";
}