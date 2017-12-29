package com.aware.plugin.template.sensor.listener.impl;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.aware.plugin.template.Provider;
import com.aware.plugin.template.sensor.listener.MetaWearSensorObserver;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by lmarek on 27.12.2017.
 */

public final class AccelerometerObserver extends MetaWearSensorObserver {

    private final static String LOG_TAG = AccelerometerObserver.class.getSimpleName();


    public AccelerometerObserver(MetaWearBoard metaWearBoard, Context context) {
        super(metaWearBoard, context);
    }

    @Override
    protected void registerObserver(MetaWearBoard metaWearBoard) {
        final Accelerometer accelerometer = metaWearBoard.getModule(Accelerometer.class);


        accelerometer.acceleration().addRouteAsync(source -> source.stream((Subscriber) (data, env) -> {
            final Acceleration producedData = data.value(Acceleration.class);
            final Calendar timestamp = data.timestamp();

            processData(producedData, timestamp);
        })).continueWith((Continuation<Route, Void>) task -> {
            addTerminationTask(() -> {
                accelerometer.acceleration().stop();
                accelerometer.stop();
                return null;
            });
            accelerometer.acceleration().start();
            accelerometer.start();
            return null;
        });
    }

    private void processData(Acceleration acceleration, Calendar timestamp) {
        Log.v(LOG_TAG, acceleration.toString());

        final ContentValues contentValues = convertToContentValues(acceleration, timestamp);
        try {
            providerClient.insert(Provider.Acceleration_Data.CONTENT_URI, contentValues);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ContentValues convertToContentValues(Acceleration acceleration, Calendar timestamp) {
        final ContentValues contentValues = new ContentValues();
        final Timestamp sqlTimestamp = new Timestamp(timestamp.getTime().getTime());

        contentValues.put(Provider.Acceleration_Data.TIMESTAMP, sqlTimestamp.toString());
        contentValues.put(Provider.Acceleration_Data.X, acceleration.x());
        contentValues.put(Provider.Acceleration_Data.Y, acceleration.y());
        contentValues.put(Provider.Acceleration_Data.Z, acceleration.y());

        return contentValues;
    }
}
