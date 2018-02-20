package com.aware.plugin.template.sensor.listener;

import android.content.Context;

import com.aware.plugin.template.data.transmission.DataBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by lmarek on 03.01.2018.
 */

public abstract class MetaWearForcedSensorDataPersistingObserver extends MetaWearAsyncSensorDataPersistingObserver {

    private final ScheduledExecutorService scheduledExecutorService;

    public MetaWearForcedSensorDataPersistingObserver(DataBus dataBus) {
        super(dataBus);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        addTerminationTask(scheduledExecutorService::shutdown);
    }

    /**
     * Simulate async sensor behaviour by forcing sensor read in a loop.
     * @param sensorReadTask task to be executed in a loop, usually a reference to read method of forced data producer.
     */
    protected void addSensorReadTask(Runnable sensorReadTask) {
        scheduledExecutorService.scheduleAtFixedRate(sensorReadTask, 0, DATA_READ_LIMIT_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }
}
