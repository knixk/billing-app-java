package com.pos.billingapp.enums;

public enum TransactionType {
    PURCHASE("Purchase", "4001"),
    UPI("UPI", "5120"),
    BRAND_EMI("Brand EMI", "5002"),
    VOID("Void", "4006"),
    REFUND("Refund", "4002"),
    BANK_EMI("Bank EMI", "5101"),
    PRE_AUTH("Preauth", "4007"),
    SALE_COMPLETE("Sale Complete", "4008"),
    BANK_EMI_SINGLE_TRANSACTION("Bank EMI Single Transaction", "5005"),
    ZEST_MONEY_BANK_EMI("Zest Money Bank EMI", "5367"),
    ZEST_MONEY_BRAND_EMI("Zest Money Brand EMI", "5370"),
    ZEST_MONEY_VOID("Zest Money Void", "5369"),
    HDFC_FLEXIPAY("HDFC Flexipay", "5030"),
    BHARAT_QR("Bharat QR", "5123"),
    TWID_VOID("TWID Void", "5121"),
    AIRTEL_BANK_TRANSACTION("Airtel Bank Transaction", "5127"),
    COD_SALE("COD Sale", "4507"),
    COD_VOID("COD Void", "4508"),
    AMAZON_PAY("Amazon Pay", "5129"),
    GET_STATUS_OF_AMAZON_PAY_SALE_CHARGECASE("Amazon Pay Sale ChargeCase", "5122"),
    MY_EMI("My EMI", "5032"),
    TWID_INTEGRATION("TWID Integration", "5131"),
    QWIKCILVER_REDEMPTION("Qwikcilver Redemption", "4205"),
    CARDLESS_BANK_EMI("Cardless Bank EMI", "5003"),
    CARDLESS_BRAND_EMI("Cardless Brand EMI", "5004"),
    CARDLESS_BANK_AND_BRAND_EMI("Cardless Bank and Brand EMI", "5031"),
    GIFTIICON("GiftIcon", "4113"),
    MAGIC_PIN("Magic PIN", "4109"),
    MAGIC_PIN_VOID("Magic PIN Void", "4110"),
    REWARD_TRANSACTION("Reward Transaction", "4101"),
    PAYBACK_TRANSACTION("Payback Transaction", "4402"),
    VOID_PAYBACK_TRANSACTION("Void Payback Transaction", "4403"),
    THIRD_PARTY_WALLET_INTEGRATION("Third Party Wallet Integration", "5102"),
    VOID_OF_SALE_CHARGE("Void of Sale Charge", "5104"),
    GET_STATUS_OF_SALE_CHARGE("Get Status of Sale Charge", "5112"),
    SODEXO_INTEGRATION("Sodexo Integration", "5106"),
    SODEXO_VOID_REQUEST("Sodexo Void Request", "5107"),
    PAPER_POS_INTEGRATION("Paper POS Integration", "5568"),
    SETTLEMENT("Settlement", "6001");

    private final String displayName;
    private final String value;

    TransactionType(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }

    // Helper method to convert a string to the corresponding TransactionType
    public static TransactionType fromString(String text) {
        for (TransactionType transactionType : TransactionType.values()) {
            if (transactionType.displayName.equalsIgnoreCase(text)) {
                return transactionType;
            }
        }
        return null; // If no matching enum is found
    }
}
