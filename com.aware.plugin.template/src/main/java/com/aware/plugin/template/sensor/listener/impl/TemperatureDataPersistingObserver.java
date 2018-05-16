package com.aware.plugin.template.sensor.listener.impl;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.aware.plugin.template.Provider;
import com.aware.plugin.template.sensor.listener.MetaWearForcedSensorDataPersistingObserver;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.module.Temperature;

import bolts.Continuation;

/**
 * Created by lmarek on 03.01.2018.
 */

public class TemperatureDataPersistingObserver extends MetaWearForcedSensorDataPersistingObserver {

    public final static String SENSOR_NAME = TemperatureDataPersistingObserver.class.getSimpleName();

    public TemperatureDataPersistingObserver(Context context) {
        super(context);
    }

    protected void fillWithSensorSpecificData(ContentValues contentValues, Data data) {
        final Float temperature = data.value(Float.class);

        contentValues.put(Provider.Temperature_Data.TEMPERATURE_IN_CELSIUS, temperature);
    }

    @Override
    public void register(MetaWearBoard metaWearBoard) {

        final Temperature temperature = metaWearBoard.getModule(Temperature.class);
        final Temperature.Sensor sensor = temperature != null ? temperature.findSensors(Temperature.SensorType.PRESET_THERMISTOR)[0] : null;

        if (null != sensor) {
            sensor.addRouteAsync(source -> source.stream((Subscriber) (data, env) -> processData(data))).continueWith((Continuation<Route, Void>) task -> {
                addSensorReadTask(sensor::read);

                return null;
            });
        }

    }

    @Override
    protected Uri getDatabaseContentUri() {
        return Provider.Temperature_Data.CONTENT_URI;
    }
}
