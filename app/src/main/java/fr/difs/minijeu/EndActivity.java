package fr.difs.minijeu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

// Activité de fin de jeu
public class EndActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRetry;
    private TextView txtScore;
    private TextView txtWinLose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        boolean win = getIntent().getBooleanExtra("WIN", false);
        txtWinLose = findViewById(R.id.txtWinLose);
        if (win)
            txtWinLose.setText("VICTOIRE");
        else
            txtWinLose.setText("défaite");


                    txtScore = (TextView) findViewById(R.id.txtScore);
        txtScore.setText(getIntent().getStringExtra("SCORE") + " sec.");

        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // On relance une partie
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}