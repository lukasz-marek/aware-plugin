package com.aware.plugin.template.data.transmission.impl;

import android.content.ContentValues;
import android.net.Uri;

import com.aware.plugin.template.data.transmission.BusConnection;
import com.aware.plugin.template.data.transmission.DataBus;

/**
 * Data bus template - creates a connection to the data bus.
 * Created by lmarek on 20.02.2018.
 */
public abstract class DataBusTemplate implements DataBus {


    /**
     * Default implementation of bus connection - delegates its methods to the data bus.
     * Immutable.
     */
    protected class SimpleBusConnection implements BusConnection {

        private final Uri channel;


        protected SimpleBusConnection(Uri channel) {
            this.channel = channel;
        }

        @Override
        public void transmit(ContentValues data) {
            DataBusTemplate.this.handleTransmission(channel, data);
        }

        @Override
        public void terminate() {
            DataBusTemplate.this.terminateChannel(channel);
        }
    }

    public BusConnection registerChannel(Uri channelId) {
        return new SimpleBusConnection(channelId);
    }

    protected abstract void handleTransmission(Uri channel, ContentValues data);

    protected abstract void terminateChannel(Uri channel);
}
