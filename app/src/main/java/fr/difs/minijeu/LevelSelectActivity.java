package fr.difs.minijeu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class LevelSelectActivity extends AppCompatActivity {

    private List<CustomGridViewItem> gridLevels = new ArrayList<>();
    private SharedPreferences optionsPreferences;
    private SharedPreferences scorePreferences;
    private SharedPreferences unlockPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        optionsPreferences = getSharedPreferences("OptionsPreferences", MODE_PRIVATE);
        scorePreferences = getSharedPreferences("ScorePreferences", MODE_PRIVATE);
        unlockPreferences = getSharedPreferences("UnlockPreferences", MODE_PRIVATE);

        SharedPreferences.Editor unlockEditor = unlockPreferences.edit();
        unlockEditor.putBoolean("0", true);
        unlockEditor.putBoolean("1", true);
        unlockEditor.apply();

        // Chargement de la map du niveau MAP_LEVEL
        XmlResourceParser parser = getResources().getXml(R.xml.maps);
        try {
            parseXml(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GridView gridView = findViewById(R.id.gridViewLevels);
        CustomGridViewAdapter adapter = new CustomGridViewAdapter(this, gridLevels);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int level = gridLevels.get(position).getLevel();
                if (optionsPreferences.getBoolean("unlock_mode", false) || unlockPreferences.getBoolean(level + "", false)) {
                    Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
                    intent.putExtra("LEVEL", level);
                    startActivity(intent);
                }
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.levelSelectToolbar);
        toolbar.setTitle("Niveaux");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    private void parseXml(XmlResourceParser parser) {
        int eventType = -1;
        try {
            while (eventType != parser.END_DOCUMENT) {
                if (eventType == parser.START_TAG && parser.getName().equals("map")) {
                    int level = Integer.valueOf(parser.getAttributeValue(null, "level"));
                    float score = scorePreferences.getFloat(level + "", 0);
                    boolean unlocked = optionsPreferences.getBoolean("unlock_mode", false) || unlockPreferences.getBoolean(level + "", false);
                    gridLevels.add(new CustomGridViewItem(level, score, unlocked));
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
