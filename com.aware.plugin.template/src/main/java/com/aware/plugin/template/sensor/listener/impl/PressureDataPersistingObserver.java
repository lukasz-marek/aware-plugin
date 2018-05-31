package com.aware.plugin.template.sensor.listener.impl;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.aware.plugin.template.Provider;
import com.aware.plugin.template.sensor.listener.MetaWearAsyncSensorDataPersistingObserver;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.module.BarometerBosch;

import java.util.concurrent.TimeUnit;

import bolts.Continuation;

/**
 * Created by lmarek on 03.01.2018.
 */

public class PressureDataPersistingObserver extends MetaWearAsyncSensorDataPersistingObserver {

    public final static String SENSOR_NAME = PressureDataPersistingObserver.class.getSimpleName();

    public PressureDataPersistingObserver(Context context) {
        super(context);
    }

    protected void fillWithSensorSpecificData(ContentValues contentValues, Data data) {
        final Float temperature = data.value(Float.class);

        contentValues.put(Provider.Pressure_Data.PRESSURE_IN_PASCALS, temperature);
    }

    @Override
    public void register(MetaWearBoard metaWearBoard) {
        final BarometerBosch barometer = metaWearBoard.getModule(BarometerBosch.class);
        barometer.configure().standbyTime(TimeUnit.MILLISECONDS.convert(DATA_READ_LIMIT_IN_MILLISECONDS, TimeUnit.SECONDS)).commit();

        barometer.pressure().addRouteAsync(source -> {

            source.limit(DATA_READ_LIMIT_IN_MILLISECONDS);
            source.stream((Subscriber) (data, env) -> processData(data));

        }).continueWith((Continuation<Route, Void>) task -> {

            addTerminationTask(() -> barometer.pressure().stop());

            barometer.start();
            barometer.pressure().start();

            return null;
        });

    }

    @Override
    protected Uri getDatabaseContentUri() {
        return Provider.Pressure_Data.CONTENT_URI;
    }

    @Override
    public String getForwardingId() {
        return SENSOR_NAME;
    }
}
