package fr.difs.minijeu;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import fr.difs.minijeu.mapping.entities.MaxiBonus;
import fr.difs.minijeu.mapping.entities.Entity;
import fr.difs.minijeu.mapping.Map;
import fr.difs.minijeu.mapping.Wall;
import fr.difs.minijeu.mapping.entities.Hole;
import fr.difs.minijeu.mapping.entities.MiniBonus;
import fr.difs.minijeu.mapping.entities.WinBonus;

// Activité du jeu
public class GameActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private GameView gameView;
    private SensorManager sensorManager;
    private int mapLevel;

    private Button btnPause;
    private Button btnResume;
    private Button btnRetry;
    private Button btnMenu;
    private Button btnBlackFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Préparation de la fenêtre
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Accelerometer
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        // Light sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_GAME);

        // Récupération du niveau à charger mapLevel
        mapLevel = getIntent().getIntExtra("LEVEL", 1);

        // Chargement de la map du niveau mapLevel
        XmlResourceParser parser = getResources().getXml(R.xml.maps);
        Map map = null;

        try {
            map = parseXml(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lancement
        setContentView(R.layout.activity_game);

        RelativeLayout gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setOnClickListener(this);

        if (map != null) {
            gameView = new GameView(this, map);
            gameLayout.addView(gameView);
        }

        btnPause = findViewById(R.id.btnPause);
        btnResume = findViewById(R.id.btnResume);
        btnRetry = findViewById(R.id.btnRetry);
        btnMenu = findViewById(R.id.btnMainMenu);
        btnBlackFilter = findViewById(R.id.btnBlackFilter);

        btnPause.setOnClickListener(this);
        btnResume.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        btnMenu.setOnClickListener(this);

    }

    private Map parseXml(XmlResourceParser parser) {
        int eventType = -1;
        List<Wall> walls = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        double spawnX = 0;
        double spawnY = 0;
        double playerSize = 0;

        try {
            while (eventType != parser.END_DOCUMENT) {
                if (eventType == parser.START_TAG) {
                    if (parser.getName().equals("map") && parser.getAttributeValue(null, "level").equals(String.valueOf(mapLevel))) {
                        spawnX = Double.parseDouble(parser.getAttributeValue(null, "spawnX"));
                        spawnY = Double.parseDouble(parser.getAttributeValue(null, "spawnY"));
                        playerSize = Double.parseDouble(parser.getAttributeValue(null, "playerSize"));
                        eventType = parser.next();
                        if (eventType == parser.START_TAG && parser.getName().equals("walls")) {
                            eventType = parser.next();
                            while (parser.getName().equals("wall")) {
                                if (eventType == parser.START_TAG) {
                                    double left = Double.parseDouble(parser.getAttributeValue(null, "left"));
                                    double top = Double.parseDouble(parser.getAttributeValue(null, "top"));
                                    double right = Double.parseDouble(parser.getAttributeValue(null, "right"));
                                    double bottom = Double.parseDouble(parser.getAttributeValue(null, "bottom"));
                                    String light = parser.getAttributeValue(null, "light");
                                    walls.add(new Wall(left, top, right, bottom, light));
                                }
                                eventType = parser.next();
                            }
                        }
                        while (eventType != parser.START_TAG)
                            eventType = parser.next();
                        if (parser.getName().equals("entities")) {
                            eventType = parser.next();
                            while (parser.getName().equals("entity")) {
                                if (eventType == parser.START_TAG) {
                                    double x = Double.parseDouble(parser.getAttributeValue(null, "x"));
                                    double y = Double.parseDouble(parser.getAttributeValue(null, "y"));
                                    double size = Double.parseDouble(parser.getAttributeValue(null, "size"));
                                    String type = parser.getAttributeValue(null, "type");
                                    Entity entity;
                                    switch (type) {
                                        case "win":
                                            entity = new WinBonus(x, y, size);
                                            break;
                                        case "hole":
                                            entity = new Hole((x), y, size);
                                            break;
                                        case "big":
                                            entity = new MaxiBonus(x, y, size);
                                            break;
                                        case "small":
                                            entity = new MiniBonus(x, y, size);
                                            break;
                                        default:
                                            entity = new Hole(x, y, 1);
                                    }
                                    entities.add(entity);
                                }
                                eventType = parser.next();
                            }
                        }
                    }
                }
                eventType = parser.next();
            }
            return new Map(mapLevel, spawnX, spawnY, playerSize, walls, entities);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return null;
    }

    // Quand le joueur touche un bord de l'écran, on passe à l'activité de fin de partie
    public void endGame(float score, boolean win) {
        Intent intent = new Intent(this, EndActivity.class);
        // On lui passe la variable score
        intent.putExtra("SCORE", score);
        intent.putExtra("WIN", win);
        intent.putExtra("LEVEL", mapLevel);

        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gameView.move(event.values[0], event.values[1]);
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            gameView.setLight((int) event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPause:
                gameView.pause();
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
                btnMenu.setVisibility(View.VISIBLE);
                btnBlackFilter.setVisibility(View.VISIBLE);
                break;
            case R.id.btnResume:
                gameView.resume();
                btnPause.setVisibility(View.VISIBLE);
                btnResume.setVisibility(View.GONE);
                btnRetry.setVisibility(View.GONE);
                btnMenu.setVisibility(View.GONE);
                btnBlackFilter.setVisibility(View.GONE);
                break;
            case R.id.btnRetry:
                // On relance une partie
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("LEVEL", mapLevel);
                startActivity(intent);
                break;
            case R.id.btnMainMenu:
                intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                break;
        }
    }
}