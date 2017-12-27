package com.aware.plugin.template.sensor.listener.impl;

import com.aware.plugin.template.sensor.listener.MetaWearSensorObserver;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by lmarek on 27.12.2017.
 */

public final class AccelerometerObserver extends MetaWearSensorObserver {

    public AccelerometerObserver(MetaWearBoard metaWearBoard) {
        super(metaWearBoard);
    }

    @Override
    protected void registerObserver(MetaWearBoard metaWearBoard) {
        final Accelerometer accelerometer= metaWearBoard.getModule(Accelerometer.class);

        accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
            @Override
            public void configure(RouteComponent source) {
                source.stream(new Subscriber() {
                    @Override
                    public void apply(Data data, Object... env) {
                       final Acceleration producedData = data.value(Acceleration.class);

                       processData(producedData);
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                accelerometer.acceleration().start();
                accelerometer.start();
                return null;
            }
        });
    }

    private void processData(Acceleration acceleration){

    }
}
