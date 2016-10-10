package com.sensors.sensortestapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by JW1 on 10/10/2016.
 */

public class SensorDataCollector implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyro;

    public SensorDataCollector(SensorManager senMngr) {
        senSensorManager = senMngr;
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        senSensorManager.registerListener(this, senAccelerometer, senSensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senGyro, senSensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        float x,y,z = 0.0f;

        switch (mySensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                //Log.d("MAIN", "AcX = "+x+", AcY = "+y+", AcZ = "+z);
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                //Log.d("MAIN", "GyX = "+x+", GyY = "+y+", GyZ = "+z);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void registerListeners() {
        senSensorManager.registerListener(this, senAccelerometer, senSensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senGyro, senSensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unRegisterListeners() {
        senSensorManager.unregisterListener(this);
    }
}
