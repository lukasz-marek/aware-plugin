package com.aware.plugin.template.sensor.listener;

import com.mbientlab.metawear.MetaWearBoard;

/**
 * Created by lmarek on 02.01.2018.
 */

public interface MetaWearSensorObserver {

    void register(MetaWearBoard metaWearBoard);

    void terminate();
}
