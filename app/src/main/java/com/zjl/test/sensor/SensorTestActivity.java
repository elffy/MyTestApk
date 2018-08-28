package com.zjl.test.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.zjl.test.R;

/**
 * Created by zengjinlong on 18-6-8.
 */

public class SensorTestActivity extends Activity {

    private TextView mTvX;
    private TextView mTvY;
    private TextView mTvZ;

    private static final int CACHE_VALUE_NUM = 5;
    private float[] mXValues = new float[CACHE_VALUE_NUM];
    private float[] mYValues = new float[CACHE_VALUE_NUM];
    private float[] mZValues = new float[CACHE_VALUE_NUM];
    private int mAddIndex = 0;

    SensorManager sensorManager;
    Sensor linearSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_test_layout);
        mTvX = (TextView) findViewById(R.id.textView2);
        mTvY = (TextView) findViewById(R.id.textView4);
        mTvZ = (TextView) findViewById(R.id.textView6);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.requestTriggerSensor(new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent triggerEvent) {
//                triggerEvent.sensor.get
            }
        }, linearSensor);
        initValues(mXValues);
        initValues(mYValues);
        initValues(mZValues);
    }

    private void initValues(float[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
    }

    SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.e("zjltest", "onSensorChanged");
//            setTextViewValue(mTvX, getAverageValue(mXValues, sensorEvent.values[0]));
//            setTextViewValue(mTvY, getAverageValue(mYValues, sensorEvent.values[1]));
            setTextViewValue(mTvZ, getAverageValue(mZValues, sensorEvent.values[2]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            Log.d("zjltest", "onAccuracyChanged:" + i);
        }
    };

    private float getAverageValue(float[] values, float value) {
        values[mAddIndex] = value;
        mAddIndex++;
        if (mAddIndex > values.length - 1) {
            mAddIndex = 0;
        }
        float result = 0;
        int validNum = 0;
        for (float f : values) {
            if (f != 0) {
                result += f;
                validNum++;
            }
        }
//        Log.d("zjltest", "getAverageValue:" + ",value:" + value + ", result:" + result + ",validNum:" + validNum);
        return result / validNum;
    }

    private void setTextViewValue(TextView tv, float result) {
        if (Math.abs(result) > 0.02) {
//            Log.d("zjltest", "result:" + result);
            tv.setText("" + result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(mSensorEventListener, linearSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(mSensorEventListener);
    }
}


