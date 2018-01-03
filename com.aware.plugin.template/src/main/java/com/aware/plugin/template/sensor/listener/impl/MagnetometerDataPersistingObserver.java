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
import com.mbientlab.metawear.data.MagneticField;
import com.mbientlab.metawear.module.MagnetometerBmm150;

import java.sql.Timestamp;
import java.util.Calendar;

import bolts.Continuation;

/**
 * Created by lmarek on 02.01.2018.
 */

public class MagnetometerDataPersistingObserver extends MetaWearAsyncSensorDataPersistingObserver {

    public MagnetometerDataPersistingObserver(Context context) {
        super(context);
    }

    @Override
    protected ContentValues convertToDatabaseRecord(Data data) {
        final MagneticField magneticField = data.value(MagneticField.class);
        final Calendar timestamp = data.timestamp();

        final ContentValues contentValues = new ContentValues();

        final Timestamp sqlTimestamp = new Timestamp(timestamp.getTime().getTime());
        contentValues.put(Provider.Magnetic_Data.TIMESTAMP, sqlTimestamp.toString());
        contentValues.put(Provider.Magnetic_Data.X, magneticField.x());
        contentValues.put(Provider.Magnetic_Data.Y, magneticField.y());
        contentValues.put(Provider.Magnetic_Data.Z, magneticField.y());

        return contentValues;
    }

    @Override
    public void register(MetaWearBoard metaWearBoard) {
        final MagnetometerBmm150 magnetometer = metaWearBoard.getModule(MagnetometerBmm150.class);

        if (magnetometer != null) {

            magnetometer.configure().outputDataRate(MagnetometerBmm150.OutputDataRate.ODR_6_HZ).commit();

            magnetometer.magneticField().addRouteAsync(source -> {
                source.limit(DATA_LIMIT_IN_MILLISECONDS);
                source.stream((Subscriber) (data, env) -> processData(data));
            }).continueWith((Continuation<Route, Void>) task -> {

                addTerminationTask(() -> {
                    magnetometer.magneticField().stop();
                    magnetometer.stop();
                });

                magnetometer.magneticField().start();
                magnetometer.start();
                return null;
            });
        }

    }

    @Override
    protected Uri getDatabaseContentUri() {
        return Provider.Magnetic_Data.CONTENT_URI;
    }
}
