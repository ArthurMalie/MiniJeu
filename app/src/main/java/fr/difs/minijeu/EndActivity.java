package fr.difs.minijeu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.Inet4Address;

// Activité de fin de jeu
public class EndActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnNext;
    private Button btnRetry;
    private Button btnMenu;
    private TextView txtScore;
    private TextView txtWinLose;

    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        level = getIntent().getIntExtra("LEVEL", 0);
        float score = getIntent().getFloatExtra("SCORE", 0);
        boolean win = getIntent().getBooleanExtra("WIN", false);

        txtWinLose = findViewById(R.id.txtWinLose);
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtScore.setText(score + " sec.");

        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);
        btnMenu = (Button) findViewById(R.id.btnEndMenu);
        btnMenu.setOnClickListener(this);
        btnNext = (Button) findViewById(R.id.btnNextLevel);
        btnNext.setOnClickListener(this);

        if (win) {
            txtWinLose.setText("VICTOIRE");

            SharedPreferences unlockPrefs = getSharedPreferences("UnlockPreferences", MODE_PRIVATE);
            SharedPreferences.Editor unlockEditor = unlockPrefs.edit();
            unlockEditor.putBoolean((level + 1) + "", true);
            unlockEditor.apply();

            if (existsNext(level + 1))
                btnNext.setVisibility(View.VISIBLE);
            else
                btnNext.setVisibility(View.GONE);

            // On inscrit le score dans les SharedPreferences
            int level = getIntent().getIntExtra("LEVEL", 0);
            SharedPreferences sharedPreferences = getSharedPreferences("ScorePreferences", MODE_PRIVATE);
            // si le score est inferieur au highscore
            if (score < sharedPreferences.getFloat(level + "", 100)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(level + "", score);
                editor.apply();
            }
        } else {
            btnNext.setVisibility(View.GONE);
            txtWinLose.setText("défaite");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnRetry:
                // On relance une partie
                intent = new Intent(this, GameActivity.class);
                intent.putExtra("LEVEL", level);
                startActivity(intent);
                break;
            case R.id.btnEndMenu:
                intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.btnNextLevel:
                // On lance le prochain niveau
                intent = new Intent(this, GameActivity.class);
                intent.putExtra("LEVEL", level + 1);
                startActivity(intent);
                break;
        }
    }

    // Check dans le document xml "map" s'il existe une map qui contient le niveau "mapLevel"
    public boolean existsNext(int mapLevel) {
        XmlResourceParser parser = getResources().getXml(R.xml.maps);
        int eventType = -1;
        try {
            while (eventType != parser.END_DOCUMENT) {
                if (eventType == parser.START_TAG && parser.getName().equals("map")) {
                    int niveau = Integer.valueOf(parser.getAttributeValue(null, "level"));
                    if (niveau == mapLevel)
                        return true;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return false;
    }
}