package com.example.dani_pc.bobo;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import java.util.ArrayList;



public class Services extends Service implements SensorEventListener{

    private final IBinder binder = new LocalBinder();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnometer;
    private float[] accelerometerReading = new float[3];
    private float[] magnometerReading = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private float azimut, pitch, roll;
    private ArrayList<DevicePositionChangedListener> positionListeners = new ArrayList<>();
    private DevicePositionChangedListener tempListener;



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void initSensorService(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // if phone support this sensors - register

        if (accelerometer != null && magnometer != null){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            //show message device doesn't support game sensors
            showDeviceNotSupportSensorsMessage();
        }
    }


    public void showDeviceNotSupportSensorsMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("Sensor Settings");
        builder.setMessage("Device Not Support Sensor Service");
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, accelerometerReading, 0, event.values.length);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, magnometerReading, 0, event.values.length);
                break;

            default:
                return;
        }


        if (accelerometerReading != null && magnometerReading != null) {

            // checks that the rotation matrix is found
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, null  , accelerometerReading, magnometerReading);
            if (success) {
                //getOrientation Values
                SensorManager.getOrientation(rotationMatrix, orientationAngles);
                azimut = orientationAngles[0];
                pitch = orientationAngles[1];
                roll = orientationAngles[2];
                if (pitch > 0){

                    for (int i = 0; i < positionListeners.size(); i++)
                        if (positionListeners.get(i) != null) {

                            positionListeners.get(i).devicePositionChanged();

                            tempListener = positionListeners.get(i);
                            positionListeners.remove(i);

                            new CountDownTimer(3000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                }
                                public void onFinish() {
                                    positionListeners.add(tempListener);

                                }
                            }.start();
                        }

                }
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }


    public class LocalBinder extends Binder{

        public Services getService() {
            return Services.this;
        }

        public void registerListener(DevicePositionChangedListener listener){
            positionListeners.add(listener);
        }

        public void removeListeners(){
            positionListeners.clear();

        }

    }


    public interface DevicePositionChangedListener {
        void devicePositionChanged();
    }
}
