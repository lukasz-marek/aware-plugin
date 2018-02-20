package com.aware.plugin.template.data.transmission;

import android.content.ContentValues;

/**
 * Created by lmarek on 20.02.2018.
 */

public interface BusConnection {

    void transmit(ContentValues data);

    void terminate();
}
