package com.aware.plugin.template.communication.messages;

/**
 * Created by lmarek on 19.12.2017.
 */

public final class DeviceSelectedMessage extends Message {

    private final String macAddress;

    public DeviceSelectedMessage(String macAddress) {
        super(MessageType.DEVICE_SELECTED);
        this.macAddress = macAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
