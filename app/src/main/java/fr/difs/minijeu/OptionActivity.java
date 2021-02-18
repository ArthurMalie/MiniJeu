package fr.difs.minijeu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OptionActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private SharedPreferences sharedPref;
    private Button btnDebug;
    private TextView txtSpeed;

    private SensorManager sensorManager;

    private float xSpeed;
    private float ySpeed;
    private float light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        sharedPref = getSharedPreferences("OptionsPreferences", MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Accelerometer
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        // Light sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);


        txtSpeed = findViewById(R.id.txtSpeed);

        btnDebug = findViewById(R.id.btnDebug);
        Button btnPlus = findViewById(R.id.btnVitessePlus);
        Button btnMoins = findViewById(R.id.btnVitesseMoins);
        Button btnMenu = findViewById(R.id.btnOptionsMenu);
        Button btnCalibAccel = findViewById(R.id.btnCalibrerAccel);
        Button btnCalibLight = findViewById(R.id.btnCalibrerLight);
        btnDebug.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnMoins.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnCalibAccel.setOnClickListener(this);
        btnCalibLight.setOnClickListener(this);

        refreshDebug();
        refreshSpeed();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (v.getId()) {
            case R.id.btnDebug:
                editor.putBoolean("debug_mode", !sharedPref.getBoolean("debug_mode", false));
                editor.apply();
                refreshDebug();
                break;
            case R.id.btnVitessePlus:
                float speed1 = sharedPref.getFloat("speed", 1);
                if (speed1 < 10) {
                    editor.putFloat("speed", speed1 + .25f);
                    editor.apply();
                    refreshSpeed();
                }
                break;
            case R.id.btnVitesseMoins:
                float speed2 = sharedPref.getFloat("speed", 1);
                if (speed2 > 0) {
                    editor.putFloat("speed", speed2 - .25f);
                    editor.apply();
                    refreshSpeed();
                }
                break;
            case R.id.btnCalibrerAccel:
                editor.putFloat("xSpeed", xSpeed);
                editor.putFloat("ySpeed", ySpeed);
                editor.apply();
                break;
            case R.id.btnCalibrerLight:
                editor.putFloat("light", light);
                editor.apply();
                break;
            case R.id.btnOptionsMenu:
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void refreshDebug() {
        if (sharedPref.getBoolean("debug_mode", false))
            btnDebug.setText("ON");
        else
            btnDebug.setText("OFF");
    }

    private void refreshSpeed() {
        txtSpeed.setText(sharedPref.getFloat("speed", 1) + "");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xSpeed = event.values[0];
            ySpeed = event.values[1];
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}