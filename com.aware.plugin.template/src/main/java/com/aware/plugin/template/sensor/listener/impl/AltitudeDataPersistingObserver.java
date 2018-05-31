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

public class AltitudeDataPersistingObserver extends MetaWearAsyncSensorDataPersistingObserver {

    public final static String SENSOR_NAME = AltitudeDataPersistingObserver.class.getSimpleName();


    public AltitudeDataPersistingObserver(Context context) {
        super(context);
    }

    @Override
    protected void fillWithSensorSpecificData(ContentValues contentValues, Data data) {
        final Float altitude = data.value(Float.class);
        contentValues.put(Provider.Altitude_Data.ALTITUDE, altitude);
    }

    @Override
    public void register(MetaWearBoard metaWearBoard) {
        final BarometerBosch baroBosch = metaWearBoard.getModule(BarometerBosch.class);

        if(null != baroBosch) {

            baroBosch.configure()
                    .filterCoeff(BarometerBosch.FilterCoeff.AVG_16)
                    .pressureOversampling(BarometerBosch.OversamplingMode.ULTRA_HIGH)
                    .standbyTime(TimeUnit.MILLISECONDS.convert(DATA_READ_LIMIT_IN_MILLISECONDS, TimeUnit.SECONDS))
                    .commit();

            baroBosch.altitude().addRouteAsync(source -> source.stream((Subscriber) (data, env) -> {

                processData(data);

            })).continueWith((Continuation<Route, Void>) task -> {

                addTerminationTask(() -> baroBosch.altitude().stop());


                baroBosch.altitude().start();
                baroBosch.start();

                return null;
            });

        }
    }

    @Override
    protected Uri getDatabaseContentUri() {
        return Provider.Altitude_Data.CONTENT_URI;    }

    @Override
    public String getForwardingId() {
        return SENSOR_NAME;
    }
}
