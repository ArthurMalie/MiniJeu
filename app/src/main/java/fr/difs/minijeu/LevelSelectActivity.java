package fr.difs.minijeu;

import android.content.Intent;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        // Chargement de la map du niveau MAP_LEVEL
        XmlResourceParser parser = getResources().getXml(R.xml.maps);
        try {
            parseXml(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GridView gridView = findViewById(R.id.gridView1);
        CustomGridViewAdapter adapter = new CustomGridViewAdapter(this, gridLevels);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
                intent.putExtra("LEVEL", gridLevels.get(position).getLevel());
                startActivity(intent);
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
                    gridLevels.add(new CustomGridViewItem(Integer.valueOf(parser.getAttributeValue(null, "level")), 0));
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
