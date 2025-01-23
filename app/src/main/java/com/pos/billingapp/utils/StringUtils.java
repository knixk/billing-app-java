package com.pos.billingapp.utils;

import android.util.Log;
import java.nio.charset.StandardCharsets;

/**
 * A utility class containing methods for handling various string and byte array operations.
 */
public class StringUtils {
    private StringUtils(){

    }

    private static final String TAG = "StringUtils";

    /**
     * Converts a byte array to a hexadecimal string representation.
     *
     * @param bytes The input byte array.
     * @return A hexadecimal string representation of the input byte array.
     */
    public static String bytesToHex(byte[] bytes) {
        Log.d(TAG,"Entering bytesToHex()");
        byte[] hexArray = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        Log.d(TAG,"Exiting byteArrayToHexString() with return value:"+ new String(hexChars, StandardCharsets.UTF_8));
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    /**
     * Converts a hexadecimal string to its ASCII representation.
     *
     * @param hexStr The input hexadecimal string.
     * @return A string containing the ASCII representation of the input hexadecimal string.
     */
    public static String hexToAscii(String hexStr) {
        Log.d(TAG,"Entering hexToAscii()");
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        Log.d(TAG,"Exiting hexToAscii() with value : " + output.toString());
        return output.toString();
    }

    public static byte[] hexStringToBCD(String hexString) {
        int length = hexString.length();
        byte[] result = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            int highNibble = Character.digit(hexString.charAt(i), 16);
            int lowNibble = Character.digit(hexString.charAt(i + 1), 16);

            // Combine high and low nibbles to form a BCD byte
            byte bcdByte = (byte) ((highNibble << 4) | lowNibble);
            result[i / 2] = bcdByte;
        }

        return result;
    }
}
