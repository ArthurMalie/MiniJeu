package fr.difs.minijeu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OptionActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPref;
    Button btnDebug;
    TextView txtSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        sharedPref = getSharedPreferences("OptionsPreferences", MODE_PRIVATE);

        btnDebug = findViewById(R.id.btnDebug);
        btnDebug.setOnClickListener(this);

        txtSpeed = findViewById(R.id.txtSpeed);

        Button btnPlus = findViewById(R.id.btnVitessePlus);
        Button btnMoins = findViewById(R.id.btnVitesseMoins);
        btnPlus.setOnClickListener(this);
        btnMoins.setOnClickListener(this);

        Button btnMenu = findViewById(R.id.btnOptionsMenu);
        btnMenu.setOnClickListener(this);

        refreshDebug();
        refreshSpeed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDebug:
                SharedPreferences.Editor editor1 = sharedPref.edit();
                editor1.putBoolean("debug_mode", !sharedPref.getBoolean("debug_mode", false));
                editor1.apply();
                refreshDebug();
                break;
            case R.id.btnVitessePlus:
                float speed1 = sharedPref.getFloat("speed", 1);
                if(speed1 < 10) {
                    SharedPreferences.Editor editor2 = sharedPref.edit();
                    editor2.putFloat("speed", speed1 + .25f);
                    editor2.apply();
                    refreshSpeed();
                }
                break;
            case R.id.btnVitesseMoins:
                float speed2 = sharedPref.getFloat("speed", 1);
                if(speed2 > 0) {
                    SharedPreferences.Editor editor3 = sharedPref.edit();
                    editor3.putFloat("speed", speed2 - .25f);
                    editor3.apply();
                    refreshSpeed();
                }
                break;
            case R.id.btnOptionsMenu:
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void refreshDebug() {
        if (sharedPref.getBoolean("debug_mode", false))
            btnDebug.setText("ON");
        else
            btnDebug.setText("OFF");
    }

    private void refreshSpeed() {
        txtSpeed.setText(sharedPref.getFloat("speed", 1) + "");
    }
}