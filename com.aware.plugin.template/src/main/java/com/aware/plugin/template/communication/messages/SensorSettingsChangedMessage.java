package com.aware.plugin.template.communication.messages;

public class SensorSettingsChangedMessage extends Message {

    private final String sensorName;

    public SensorSettingsChangedMessage(String sensorName) {
        super(Message.MessageType.SENSOR_SETTINGS_CHANGED);
        this.sensorName = sensorName;
    }

    public String getSensorName() {
        return sensorName;
    }
}
