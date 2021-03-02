package fr.difs.minijeu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Activité d'accueil : bouton START
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnPlay;
    private ImageButton btnLevelSelect;
    private Button btnOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        btnPlay = findViewById(R.id.btnPlay);
        btnLevelSelect = findViewById(R.id.btnLevelSelect);
        btnOption = findViewById(R.id.btnOptions);
        btnPlay.setOnClickListener(this);
        btnLevelSelect.setOnClickListener(this);
        btnOption.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnPlay:
                // Lancement du jeu (reprise au dernier niveau JOUÉ ou au niveau suivant si terminé)
                SharedPreferences optionsPrefs = getSharedPreferences("OptionsPreferences", MODE_PRIVATE);
                intent = new Intent(this, GameActivity.class);
                intent.putExtra("LEVEL", optionsPrefs.getInt("resume_level", 1));
                startActivity(intent);
                break;
            case R.id.btnLevelSelect:
                // Choix du niveau
                intent = new Intent(this, LevelSelectActivity.class);
                startActivity(intent);
                break;
            case R.id.btnOptions:
                // Menu des options
                intent = new Intent(this, OptionActivity.class);
                startActivity(intent);
                break;
        }
    }
}
