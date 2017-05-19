package com.jiahuan.svgmapview.sample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.VelocityTracker;
import android.widget.TextView;

import com.jiahuan.svgmapview.SVGMapView;
import com.jiahuan.svgmapview.sample.helper.AssetsHelper;

import java.util.LinkedList;

public class SparkActivity extends ActionBarActivity implements SensorEventListener {
    static final float NS2S = 1.0f / 1000000000.0f;
    /*DEBUG TAG FOR VELOCITY*/
    private static final String DEBUG_TAG = "Velocity";
    private static final int SHAKE_THRESHOLD = 600;
    TextView tx;
    PointF init_point;
    PointF final_point;
    LinkedList<PointF> last_points = new LinkedList<>();
    float dpositon;
    float dpositon_last = 0;
    private SVGMapView mapView;

    private long steps = 0;

    private float[] gravity = {(float) 9.8,(float) 9.8,(float) 9.8};
    private float [] linear_acceleration={(float) 0.000,(float) 0.000,(float) 0.000};

    private SensorManager mSensorManager;

    private Sensor stepSensor;

    /*for distance*/
    private Sensor mAccelerometer;
    /*detecting Shake movement*/
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spark);

        init_point = new PointF(85, 350);
        final_point = new PointF(630, 350);

        mapView = (SVGMapView) findViewById(R.id.spark_mapview);
        mapView.loadMap(AssetsHelper.getContent(this, "dcc-piso1-cortado.svg"));
        tx = (TextView) findViewById(R.id.textView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Log.d(DEBUG_TAG, "Not Linear");
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // WITH G FORCE
            stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        mapView.getController().sparkAtPoint(init_point, 30, Color.RED, 8);
        last_points.add(init_point);
        /*Random random = new Random();
        for (int i = 0; i < 4; i++)
        {
            int color = i % 2 == 0 ? Color.RED : Color.BLUE;
            mapView.getController().sparkAtPoint(new PointF(random.nextInt(1000), random.nextInt(1000)), 100, color, 2);

        }*/

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        long curTime;

        float speed = 0, x, y, z;

        final float alpha = (float) 0.8;


        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            Log.d(DEBUG_TAG, String.valueOf(x));
            Log.d(DEBUG_TAG, String.valueOf(y));
            Log.d(DEBUG_TAG, String.valueOf(z));

        /*
            gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
            gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
            gravity[2] = alpha * gravity[2] + (1 - alpha) * z;
            linear_acceleration[0] = x - gravity[0];
            linear_acceleration[1] = y - gravity[1];
            linear_acceleration[2] = z - gravity[2];

*/
            curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 1000) {

                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                linear_acceleration[0]=(linear_acceleration[0]+x)/diffTime;
                linear_acceleration[1]=(linear_acceleration[1]+y)/diffTime;
                linear_acceleration[2]=(linear_acceleration[2]+z)/diffTime;

                float acc = (float) (Math.sqrt(linear_acceleration[0]*linear_acceleration[0] + linear_acceleration[1]*linear_acceleration[1]+linear_acceleration[2]*linear_acceleration[2]));
                Log.i("acc", String.valueOf((int) acc));
                speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                //
                float dt = curTime * NS2S;
                //NS2S;
                //speed=acc *dt
                // ;
                dpositon = speed * (dt / 2);
                tx.setText(String.valueOf((int) dpositon));
                Log.i("DIST", String.valueOf((int) dpositon));

                if ((dpositon - dpositon_last) > 0) {
                    int result = (int) (dpositon - dpositon_last);
                    result = result / 1000;
                    PointF newpoint = calculatePoint(result);
                    mapView.getController().sparkAtPoint(newpoint, 30, Color.RED, 8);
                    //Log.i("result",String.valueOf(result));
                }


                // Log.i("DIST","Steps:" + String.valueOf(step.getDistanceRun()));
                //mapView.getController().sparkAtPoint();
                if (speed > SHAKE_THRESHOLD) {
                    //onPause();
                    //AlertShakeMovement();
                }
                dpositon_last = dpositon;
                last_x = x;
                last_y = y;
                last_z = z;
                Log.i("SPEED", String.valueOf(speed));
            }

        } else if (mySensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            steps++;
            Log.d("Step", String.valueOf(event.values[0]));
        }
    }

    private PointF calculatePoint(int result) {
        //PointF newpoint;
        //corversion for SVG
        int x = (545 * result) / 100;
        Log.i("result", String.valueOf(x));
        if (x + last_points.getLast().x < init_point.x) {
            return init_point;
        } else if (x + last_points.getLast().x > final_point.x) {
            return final_point;
        } else {
            x = (int) (x + last_points.getLast().x);
            last_points.add(new PointF(x, last_points.getLast().y));
        }


        //newpoint=new PointF(last_points.getLast().x,init_point.y);
        return last_points.getLast();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i(DEBUG_TAG, "onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        /*It's good practice to unregister  the sensor when the application hibernates  */
        mSensorManager.unregisterListener(this);
        Log.i(DEBUG_TAG, "onPause");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(DEBUG_TAG, "onDestroy");
    }

    /**
     * here are many ways to determine step length: you can measure it yourself,
     * estimate by multiplying your height in centimeters by 0.415 for men and 0.413 for women
     * or if you’re not overly concerned with accuracy you can use the averages 78cm for men and 70cm for women.
     *
     * @param steps
     * @return
     */

    //function to determine the distance run in kilometers using average step length for men and number of steps
    public float getDistanceRun(long steps) {
        float distance = (float) (steps * 78) / (float) 100000;
        return distance;
    }
}

