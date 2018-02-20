package com.aware.plugin.template.data.transmission;

import android.net.Uri;

/**
 * Created by lmarek on 20.02.2018.
 */

public interface DataBus {

    BusConnection registerChannel(Uri channelId);

    void terminate();
}
