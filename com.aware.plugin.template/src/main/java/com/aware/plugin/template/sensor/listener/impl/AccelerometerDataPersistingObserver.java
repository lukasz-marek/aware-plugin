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
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import bolts.Continuation;

/**
 * Created by lmarek on 27.12.2017.
 */

public final class AccelerometerDataPersistingObserver extends MetaWearAsyncSensorDataPersistingObserver {

    public final static String SENSOR_NAME = AccelerometerDataPersistingObserver.class.getSimpleName();

    public AccelerometerDataPersistingObserver(Context context) {
        super(context);
    }

    protected void fillWithSensorSpecificData(ContentValues contentValues, Data data) {
        final Acceleration acceleration = data.value(Acceleration.class);

        contentValues.put(Provider.Acceleration_Data.X, acceleration.x());
        contentValues.put(Provider.Acceleration_Data.Y, acceleration.y());
        contentValues.put(Provider.Acceleration_Data.Z, acceleration.y());
    }

    public void register(MetaWearBoard metaWearBoard) {
        final Accelerometer accelerometer = metaWearBoard.getModule(Accelerometer.class);

        if (accelerometer != null) {

            accelerometer.configure().odr(DATA_PRODUCTION_FREQUENCY).commit();

            accelerometer.acceleration().addRouteAsync(source -> {
                source.limit(DATA_READ_LIMIT_IN_MILLISECONDS);
                source.stream((Subscriber) (data, env) -> processData(data));
            }).continueWith((Continuation<Route, Void>) task -> {
                addTerminationTask(() -> {
                    accelerometer.acceleration().stop();
                    accelerometer.stop();
                });
                accelerometer.acceleration().start();
                accelerometer.start();
                return null;
            });

        }
    }

    @Override
    protected Uri getDatabaseContentUri() {
        return Provider.Acceleration_Data.CONTENT_URI;
    }

    @Override
    public String getForwardingId() {
        return SENSOR_NAME;
    }
}
