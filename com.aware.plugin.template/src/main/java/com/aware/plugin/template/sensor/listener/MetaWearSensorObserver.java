package com.aware.plugin.template.sensor.listener;

import com.mbientlab.metawear.MetaWearBoard;

/**
 * Created by lmarek on 27.12.2017.
 */

public abstract class MetaWearSensorObserver {

    public MetaWearSensorObserver(MetaWearBoard metaWearBoard){
        registerObserver(metaWearBoard);
    }

    protected abstract void registerObserver(MetaWearBoard metaWearBoard);
}
