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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import bolts.Continuation;

/**
 * Created by lmarek on 03.01.2018.
 */

public class PressureDataPersistingObserver extends MetaWearAsyncSensorDataPersistingObserver {

    public PressureDataPersistingObserver(Context context) {
        super(context);
    }

    @Override
    protected ContentValues convertToDatabaseRecord(Data data) {
        final Float temperature = data.value(Float.class);
        final Calendar timestamp = data.timestamp();

        final ContentValues contentValues = new ContentValues();

        final Timestamp sqlTimestamp = new Timestamp(timestamp.getTime().getTime());
        contentValues.put(Provider.Pressure_Data.TIMESTAMP, sqlTimestamp.toString());
        contentValues.put(Provider.Pressure_Data.PRESSURE_IN_PASCALS, temperature);

        return contentValues;
    }

    @Override
    public void register(MetaWearBoard metaWearBoard) {
        final BarometerBosch barometer = metaWearBoard.getModule(BarometerBosch.class);
        barometer.configure().standbyTime(TimeUnit.MILLISECONDS.convert(DATA_LIMIT_IN_MILLISECONDS, TimeUnit.SECONDS)).commit();

        barometer.pressure().addRouteAsync(source -> {

            source.limit(DATA_LIMIT_IN_MILLISECONDS);
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
}
