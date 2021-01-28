package fr.difs.minijeu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private int x;
    private int y;
    private int screenWidth;
    private int screenHeight;
    private Direction direction;
    private int score = -1;
    private int speed = 40;
    private Handler handler = new Handler();
    private Runnable compteur = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);
            score++;
            speed++;
        }
    };

    private enum Direction {
        HAUT, DROITE, BAS, GAUCHE
    }

    public GameView(Context context) {
        super(context);
        direction = Direction.DROITE;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        x = screenWidth / 2;
        y = screenHeight / 2;

        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);

        handler.postDelayed(compteur, 0);

        setFocusable(true);
    }

    public void update() {
        switch (direction) {
            case HAUT:
                y -= speed/5;
                break;
            case BAS:
                y += speed/5;
                break;
            case DROITE:
                x += speed/5;
                break;
            case GAUCHE:
                x -= speed/5;
                break;
        }
        if (x <= 50 || y <= 50 || x >= screenWidth - 50 || y >= screenHeight - 50) {
            handler.removeCallbacks(compteur);
            thread.setRunning(false);
            ((GameActivity) getContext()).endGame(score);
        }
    }

    public void changeDirection() {
        direction = Direction.values()[(direction.ordinal() + 1) % 4];
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(100, 255, 100));
            canvas.drawCircle(x, y, 50, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText(score + "s", screenWidth / 2, 100, paint);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }
}
