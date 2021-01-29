package fr.difs.minijeu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TimeUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.Timer;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // Mode debug : invincibilité + croix de précision pour l'accelérometre + affichage de la fréquence de rafraichissement du jeu
    private final boolean ACCELEROMETER_DEBUG_MODE = true;
    // temps en ms pour le calcul de la fréquence de rafraichissement
    private long time;

    // Vitesse du joueur (multiplicateur)
    private final double SPEED = 2;
    // Position du joueur
    private double x;
    private double y;
    // Score actuel du joueur
    private int score;
    private double ax;
    private double ay;
    // Dimensions de l'écran
    private int screenWidth;
    private int screenHeight;

    private GameThread thread;
    private Handler handler;
    private Runnable compteur;

    // Constructeur
    public GameView(Context context) {
        super(context);

        time = 0;
        score = -1;
        ax = 0;
        ay = 0;
        // Tout au long de la partie, on rajoute 1 au score toutes les secondes
        handler = new Handler();
        compteur = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                score++;
            }
        };

        // Récupération de la résolution de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        x = screenWidth / 2;
        y = screenHeight / 2;

        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);

        // On lance le compteur
        handler.postDelayed(compteur, 0);

        setFocusable(true);
    }

    public void update() {

        if (x - ax >= 0 && x - ax <= screenWidth)
            x -= ax;
        if (y + ay >= 0 && y + ay <= screenHeight)
            y += ay;

        // Si le joueur touche un bord de l'écran, on stoppe le compteur, on arrête le GameThread et on appelle la méthode endGame qui passe le score à l'activité de fin de jeu.
        if ((x <= 50 || y <= 50 || x >= screenWidth - 50 || y >= screenHeight - 50) && !ACCELEROMETER_DEBUG_MODE) {
            handler.removeCallbacks(compteur);
            thread.setRunning(false);
            ((GameActivity) getContext()).endGame(score);
        }
    }

    public void move(double x, double y, double z) {
        ax = x * SPEED;
        ay = y * SPEED;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(100, 255, 100));
            canvas.drawCircle((int) x, (int) y, 50, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            // Affichage du score
            canvas.drawText(score + "s", screenWidth / 2, 100, paint);
            paint.setTextSize(15);

            if (ACCELEROMETER_DEBUG_MODE) {
                canvas.drawText("ax : " + ax, 3 * (screenWidth / 4), 100, paint);
                canvas.drawText("ay : " + ay, 3 * (screenWidth / 4), 125, paint);
                canvas.drawCircle(screenWidth / 2, screenHeight / 2, 5, paint);
                canvas.drawLine((float) ((screenWidth / 2) - 50 - ax * 20), (float) ((screenHeight / 2) + ay * 20), (float) ((screenWidth / 2) + 50 - ax * 20), (float) ((screenHeight / 2) + ay * 20), paint);
                canvas.drawLine((float) ((screenWidth / 2) - ax * 20), (float) ((screenHeight / 2) - 50 + ay * 20), (float) ((screenWidth / 2) - ax * 20), (float) ((screenHeight / 2) + 50 + ay * 20), paint);
                canvas.drawText(System.currentTimeMillis() - time + "Hz", screenWidth - 75, 30, paint);
                time = System.currentTimeMillis();
            }
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
