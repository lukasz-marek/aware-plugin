package com.aware.plugin.template;

/**
 * Created by lmarek on 29.12.2017.
 */

public enum NotificationIdentifier {

    NO_DEVICE_SELECTED(0),
    CONNECTION_TO_DEVICE_SUCCESSFUL(1),
    CONNECTION_TO_DEVICE_FAILED(2),
    BLUETOOTH_NOT_SUPPORTED(3);

    private final int value;

    NotificationIdentifier(int id){
        this.value = id;
    }

    public int getIdentifier() {
        return value;
    }
}
