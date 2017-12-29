package com.aware.plugin.template;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import com.aware.plugin.template.communication.MessageSender;
import com.aware.plugin.template.communication.messages.DeviceSelectedMessage;
import com.mbientlab.bletoolbox.scanner.BleScannerFragment;
import com.mbientlab.metawear.MetaWearBoard;

import java.util.UUID;

/**
 * Created by lmarek on 19.12.2017.
 */

public class ChooseDeviceActivity extends Activity implements BleScannerFragment.ScannerCommunicationBus {

    private final static long SCAN_DURATION_MILLISECONDS = 30000;

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
        return SCAN_DURATION_MILLISECONDS;
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {

        final String deviceMacAddress = device.getAddress();

        MessageSender.sendMessageAsync(Plugin.RECIPIENT_NAME, new DeviceSelectedMessage(deviceMacAddress));
        this.finish();
    }
}
