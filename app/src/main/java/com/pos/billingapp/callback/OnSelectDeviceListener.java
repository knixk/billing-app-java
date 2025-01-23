package com.pos.billingapp.callback;

import com.pos.poslib.model.DeviceDetails;

import java.util.List;

/**
 * An interface that provides callbacks for selecting devices in a list.
 */
public interface OnSelectDeviceListener {

    /**
     * Callback method when a device is successfully selected.
     *
     * @param itemSelected The index of the selected item in the list.
     * @param devices      A List of DeviceDetails containing the available devices.
     */
    void onSuccess(int itemSelected, List<DeviceDetails> devices);

    /**
     * Callback method when there is a failure during device selection.
     *
     * @param errorMsg   A descriptive error message explaining the failure.
     * @param errorCode  An error code indicating the type of failure.
     */
    void onFailure(String errorMsg, int errorCode);
}
