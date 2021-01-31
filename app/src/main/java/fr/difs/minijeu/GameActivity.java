package fr.difs.minijeu;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import fr.difs.minijeu.mapping.Map;
import fr.difs.minijeu.mapping.Wall;

import static android.view.MotionEvent.ACTION_DOWN;

// Activité du jeu
public class GameActivity extends Activity implements View.OnTouchListener, SensorEventListener {

    private GameView gameView;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Chargement de la map du niveau 1
            XmlResourceParser parser = getResources().getXml(R.xml.maps);
            Map map = parseXml(parser, 1);

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

            gameView = new GameView(this, map);
            gameView.setOnTouchListener(this);
            setContentView(gameView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map parseXml(XmlResourceParser parser, int i) {
        int eventType = -1;
        List<Wall> walls = new ArrayList<>();
        double spawnX = 0.5;
        double spawnY = 0.5;

        try {
            while (eventType != parser.END_DOCUMENT) {
                if (eventType == parser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equals("map") && parser.getAttributeValue(null, "level").equals(String.valueOf(i))) {
                        spawnX = Double.parseDouble(parser.getAttributeValue(null, "spawnX"));
                        spawnY = Double.parseDouble(parser.getAttributeValue(null, "spawnY"));
                        eventType = parser.next();
                        while (parser.getName().equals("wall")) {
                            if (eventType == parser.START_TAG) {
                                double left = Double.parseDouble(parser.getAttributeValue(null, "left"));
                                double top = Double.parseDouble(parser.getAttributeValue(null, "top"));
                                double right = Double.parseDouble(parser.getAttributeValue(null, "right"));
                                double bottom = Double.parseDouble(parser.getAttributeValue(null, "bottom"));
                                walls.add(new Wall(left, top, right, bottom));
                            }
                            eventType = parser.next();
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Map(i, spawnX, spawnY, walls);
    }

    // Quand on touche l'écran, on change de direction
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == ACTION_DOWN) {
        }
        return true;
    }

    // Quand le joueur touche un bord de l'écran, on passe à l'activité de fin de partie
    public void endGame(int score, boolean win) {
        Intent intent = new Intent(this, EndActivity.class);
        // On lui passe la variable score
        intent.putExtra("SCORE", String.valueOf(score));
        intent.putExtra("WIN", win);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gameView.move(event.values[0], event.values[1]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}