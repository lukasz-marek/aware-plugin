package com.aware.plugin.template;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.aware.plugin.template.communication.MessageSender;
import com.aware.plugin.template.communication.messages.DeviceSelectedMessage;
import com.mbientlab.bletoolbox.scanner.BleScannerFragment;
import com.mbientlab.metawear.MetaWearBoard;

import java.util.UUID;

/**
 * Created by lmarek on 19.12.2017.
 */

public class ChooseDeviceActivity extends Activity implements BleScannerFragment.ScannerCommunicationBus {

    public final static String SELECTED_MAC_ADDRESS_PREFERENCE_KEY = "DEVICE_MAC_ADDRESS";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_device);
    }


    @Override
    public UUID[] getFilterServiceUuids() {
        return new UUID[]{MetaWearBoard.METAWEAR_GATT_SERVICE};
    }

    @Override
    public long getScanDuration() {
        return 10000;
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        final String deviceMacAddress = device.getAddress();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_MAC_ADDRESS_PREFERENCE_KEY, deviceMacAddress);
        editor.apply();

        MessageSender.sendMessage(Plugin.RECIPIENT_NAME, new DeviceSelectedMessage(deviceMacAddress));
    }
}
