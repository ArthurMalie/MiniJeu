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
    // Position du joueur
    private int x;
    private int y;
    // Dimensions de l'écran
    private int screenWidth;
    private int screenHeight;
    // Direction actuelle du joueur
    private Direction direction;
    // Score actuel du joueur
    private int score;
    // Vitesse actuelle du joueur
    private int speed;
    private double ax;
    private double ay;


    private Handler handler;
    private Runnable compteur;

    // Les 4 directions possibles
    private enum Direction {
        HAUT, DROITE, BAS, GAUCHE
    }

    // Constructeur
    public GameView(Context context) {
        super(context);

        score = -1;
        speed = 40;
        ax = 0;
        ay = 0;
        direction = Direction.DROITE;
        handler = new Handler();
        // Tout au long de la partie, on rajoute 1 au score et à la vitesse toutes les secondes
        compteur = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                score++;
                speed++;
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
//        switch (direction) {
//            case HAUT:
//                y -= speed/5;
//                break;
//            case BAS:
//                y += speed/5;
//                break;
//            case DROITE:
//                x += speed/5;
//                break;
//            case GAUCHE:
//                x -= speed/5;
//                break;
//        }

        x -= ax/2;
        y += ay/2;

        // Si le joueur touche un bord de l'écran, on stoppe le compteur, on arrête le GameThread et on appelle la méthode endGame qui passe le score à l'activité de fin de jeu.
        if (x <= 50 || y <= 50 || x >= screenWidth - 50 || y >= screenHeight - 50) {
            handler.removeCallbacks(compteur);
            thread.setRunning(false);
            ((GameActivity) getContext()).endGame(score);
        }
    }

    // On passe à la direction suivante (sens horaire)
    public void changeDirection() {
        direction = Direction.values()[(direction.ordinal() + 1) % 4];
    }

    public void move(double x, double y, double z) {
        ax = x*10;
        ay = y*10;
        if(ax > -1 && ax < 1)
            ax = 0;
        if(ay > -1 && ay < 1)
            ay = 0;
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
            // Affichage du score
            canvas.drawText(score + "s", screenWidth / 2, 100, paint);
            paint.setTextSize(15);
            canvas.drawText(ax+"", screenWidth / 4, 200, paint);
            canvas.drawText(ay+"", screenWidth / 4, 250, paint);
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
