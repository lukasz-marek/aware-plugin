package com.aware.plugin.template;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import com.mbientlab.bletoolbox.scanner.BleScannerFragment;
import com.mbientlab.metawear.MetaWearBoard;

import java.util.UUID;

/**
 * Created by lmarek on 19.12.2017.
 */

public class ChooseDeviceActivity extends Activity implements BleScannerFragment.ScannerCommunicationBus {

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

    }
}
