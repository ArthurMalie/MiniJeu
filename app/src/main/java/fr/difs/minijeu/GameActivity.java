package fr.difs.minijeu;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static android.view.MotionEvent.ACTION_DOWN;

// Activité du jeu
public class GameActivity extends Activity implements View.OnTouchListener, SensorEventListener {

    private GameView gameView;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fenêtre de jeu
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Pour masquer la barre de navigation en bas de l'écran
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        // Accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        gameView = new GameView(this);
        gameView.setOnTouchListener(this);
        setContentView(gameView);
    }

    // Quand on touche l'écran, on change de direction
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == ACTION_DOWN){}
        return true;
    }

    // Quand le joueur touche un bord de l'écran, on passe à l'activité de fin de partie
    public void endGame(int score) {
        Intent intent = new Intent(this, EndActivity.class);
        // On lui passe la variable score
        intent.putExtra("SCORE", String.valueOf(score));
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gameView.move(event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}