package com.aware.plugin.template;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import com.aware.plugin.template.communication.MessageSender;
import com.aware.plugin.template.communication.messages.SensorSettingsChangedMessage;
import com.aware.plugin.template.sensor.listener.impl.AccelerometerDataPersistingObserver;
import com.aware.plugin.template.sensor.listener.impl.AltitudeDataPersistingObserver;
import com.aware.plugin.template.sensor.listener.impl.GyroDataPersistingObserver;
import com.aware.plugin.template.sensor.listener.impl.MagnetometerDataPersistingObserver;
import com.aware.plugin.template.sensor.listener.impl.PressureDataPersistingObserver;
import com.aware.plugin.template.sensor.listener.impl.TemperatureDataPersistingObserver;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SelectActiveSensorsActivity extends Activity {

    public final static String SHARED_PREFERENCES_NAME = SelectActiveSensorsActivity.class.getCanonicalName();

    public final static Map<Integer, String> SENSOR_ID_TO_PREFERENCE_NAME_MAP = ImmutableMap.<Integer, String>builder().put(R.id.accelerometer_switch, AccelerometerDataPersistingObserver.SENSOR_NAME)
            .put(R.id.altitude_switch, AltitudeDataPersistingObserver.SENSOR_NAME)
            .put(R.id.gyroscope_switch, GyroDataPersistingObserver.SENSOR_NAME)
            .put(R.id.temperature_switch, TemperatureDataPersistingObserver.SENSOR_NAME)
            .put(R.id.magnetometer_switch, MagnetometerDataPersistingObserver.SENSOR_NAME)
            .put(R.id.pressure_switch, PressureDataPersistingObserver.SENSOR_NAME).build();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sensors);

        initializePreferences();
    }

    private void initializePreferences(){

        final SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        for(Map.Entry<Integer, String> entry : SENSOR_ID_TO_PREFERENCE_NAME_MAP.entrySet()){
            final String preferenceKey = entry.getValue();
            final Switch sensorSwitch = findViewById(entry.getKey());

            final Boolean isSensorEnabled = preferences.getBoolean(preferenceKey, true);
            sensorSwitch.setChecked(isSensorEnabled);

            sensorSwitch.setOnClickListener(view -> {
                final Switch element = (Switch) view;

                final Boolean newState = element.isChecked();
                preferences.edit().putBoolean(preferenceKey, newState).apply();

                MessageSender.sendMessageAsync(Plugin.RECIPIENT_NAME, new SensorSettingsChangedMessage(preferenceKey));
            });
        }
    }

}
