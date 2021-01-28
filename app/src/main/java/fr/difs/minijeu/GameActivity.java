package fr.difs.minijeu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static android.view.MotionEvent.ACTION_DOWN;

public class GameActivity extends Activity implements View.OnTouchListener {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        gameView = new GameView(this);
        gameView.setOnTouchListener(this);
        setContentView(gameView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == ACTION_DOWN)
            gameView.changeDirection();
        return true;
    }

    public void endGame(int score) {
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("SCORE", String.valueOf(score));
        startActivity(intent);
    }

}