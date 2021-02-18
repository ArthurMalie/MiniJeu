package fr.difs.minijeu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.Inet4Address;

// Activité de fin de jeu
public class EndActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRetry;
    private Button btnMenu;
    private TextView txtScore;
    private TextView txtWinLose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        float score = getIntent().getFloatExtra("SCORE", 0);
        boolean win = getIntent().getBooleanExtra("WIN", false);
        txtWinLose = findViewById(R.id.txtWinLose);
        if (win)
            txtWinLose.setText("VICTOIRE");
        else
            txtWinLose.setText("défaite");


        txtScore = (TextView) findViewById(R.id.txtScore);
        txtScore.setText(score + " sec.");

        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);
        btnMenu = (Button) findViewById(R.id.btnEndMenu);
        btnMenu.setOnClickListener(this);

        if(win) {
            // On inscrit le score dans les SharedPreferences
            int level = getIntent().getIntExtra("LEVEL", 0);
            SharedPreferences sharedPreferences = getSharedPreferences("ScorePreferences", MODE_PRIVATE);
            // si le score est inferieur au highscore
            if (sharedPreferences.getFloat(level + "", 0) < score) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(level + "", score);
                editor.apply();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnRetry:
                // On relance une partie
                intent = new Intent(this, GameActivity.class);
                int level = getIntent().getIntExtra("LEVEL", 0);
                intent.putExtra("LEVEL", level);
                startActivity(intent);
                break;
            case R.id.btnEndMenu:
                intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                break;
        }
    }
}