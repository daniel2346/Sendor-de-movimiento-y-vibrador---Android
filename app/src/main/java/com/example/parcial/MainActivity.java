package com.example.parcial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SensorManager sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        final Sensor rotationVectorSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(rotationVectorSensor == null) {

            finish(); // Close app
        }
        SensorEventListener rvListener  =new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, event.values);
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);

                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                    for (int i = 0; i < 3; i++) {
                        orientations[i] = (float) (Math.toDegrees(orientations[i]));
                    }
                if(vibrator.hasVibrator()) {
                    if (orientations[2] > 45) {
                        long[] pattern = {50, 100, 50,100};
                        vibrator.vibrate(pattern, 0);
                        getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    } else if (orientations[2] < -45) {
                        long[] pattern = {100,
                                100,
                                100,100,100};
                        // con -1 se indica desactivar repeticion del patron
                        vibrator.vibrate(pattern, 0);
                        getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    } else if ( Math.abs(orientations[2]) < 10 ) {
                        long[] pattern = {400, 200, 400,200};
                        vibrator.vibrate(pattern, -1);
                        getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    }
                    else if ( Math.abs(orientations[2]) < -10 ) {
                        long[] pattern = {400, 200, 400,200};
                        vibrator.vibrate(pattern, 0);
                        getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
