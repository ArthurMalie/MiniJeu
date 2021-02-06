package fr.difs.minijeu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Activité d'accueil : bouton START
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPlay;
    private Button btnLevelSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_menu);
        btnPlay = findViewById(R.id.btnPlay);
        btnLevelSelect = findViewById(R.id.btnLevelSelect);
        btnPlay.setOnClickListener(this);
        btnLevelSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnPlay:
                // Lancement du jeu
                intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLevelSelect:
                // Choix du niveau
                intent = new Intent(this, LevelSelectActivity.class);
                startActivity(intent);
                break;
        }
    }
}
