package com.aware.plugin.template;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ActiveSensorReadingsDisplay extends Activity {

    private final static long TIME_BETWEEN_VIEW_UPDATES_IN_MILLISECONDS = TimeUnit.SECONDS.convert(1, TimeUnit.MILLISECONDS) / 3;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    final AtomicReference<ScheduledFuture<?>> updateViewTask = new AtomicReference<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.display_sensor_readings);

        updateViewTask.set(scheduledExecutorService.scheduleAtFixedRate(this::updateView, 0, TIME_BETWEEN_VIEW_UPDATES_IN_MILLISECONDS, TimeUnit.MILLISECONDS));
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != updateViewTask.get() && !(updateViewTask.get().isCancelled() || updateViewTask.get().isDone())) {
            updateViewTask.get().cancel(true);
            updateViewTask.set(null);
        }
    }

    private void updateView(){

    }
}


