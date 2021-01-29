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

import java.util.Random;

import static java.lang.Thread.sleep;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // Mode debug : invincibilité + croix de précision pour l'accelérometre + affichage de la fréquence de rafraichissement du jeu
    private final boolean ACCELEROMETER_DEBUG_MODE = false;
    // temps en ms pour le calcul de la fréquence de rafraichissement
    private long time;

    // Taille du joueur et du trou objectif
    private final int PLAYER_SIZE = 40;
    // Nombre de pixel de marge pour rentrer dans le trou objectif
    private final int PRECISION = 15;
    // Vitesse du joueur (multiplicateur)
    private final double SPEED = 4;

    // Position du joueur
    private double x;
    private double y;
    // Position de l'arrivée
    private int xWin;
    private int yWin;
    // Score actuel du joueur
    private int score;
    private double xSpeed;
    private double ySpeed;
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
        xSpeed = 0;
        ySpeed = 0;

        // Tout au long de la partie, on rajoute 1 au score toutes les secondes
        handler = new Handler();
        compteur = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                score++;
            }
        };
        // On lance le compteur
        handler.postDelayed(compteur, 0);

        // Récupération de la résolution de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        x = screenWidth / 2;
        y = screenHeight / 2;

        // Positionnement aléatoire de l'arrivée
        Random random = new Random();
        xWin = random.nextInt(screenWidth - 100) + PLAYER_SIZE;
        yWin = random.nextInt(screenHeight - 100) + PLAYER_SIZE;

        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);


        setFocusable(true);
    }

    public void update() {

        if (x + xSpeed >= 0 && x + xSpeed <= screenWidth)
            x += xSpeed;
        if (y + ySpeed >= 0 && y + ySpeed <= screenHeight)
            y += ySpeed;

        // Si le joueur touche un bord de l'écran, on stoppe le compteur, on arrête le GameThread et on appelle la méthode endGame qui passe le score et false (perdu) à l'activité de fin de jeu.
        if ((x <= PLAYER_SIZE || y <= PLAYER_SIZE || x >= screenWidth - PLAYER_SIZE || y >= screenHeight - PLAYER_SIZE) && !ACCELEROMETER_DEBUG_MODE) {
            handler.removeCallbacks(compteur);
            thread.setRunning(false);
            ((GameActivity) getContext()).endGame(score, false);
        }
        // Si le joueur gagne, on stoppe le compteur, on arrête le GameThread et on appelle la méthode endGame qui passe le score et true (gagné) à l'activité de fin de jeu.
        if (x >= xWin - PRECISION && x <= xWin + PRECISION && y >= yWin - PRECISION && y <= yWin + PRECISION) {
            handler.removeCallbacks(compteur);
            thread.setRunning(false);
            ((GameActivity) getContext()).endGame(score, true);
        }
    }

    public void move(double x, double y) {
        xSpeed = -x * SPEED;
        ySpeed = y * SPEED;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            Paint paint = new Paint();

            // background
            canvas.drawColor(Color.BLACK);
            // win hole
            paint.setColor(Color.BLUE);
            canvas.drawCircle(xWin, yWin, PLAYER_SIZE, paint);
            // player
            paint.setColor(Color.rgb(240, 154, 5));
            canvas.drawCircle((int) x, (int) y, PLAYER_SIZE, paint);
            paint.setColor(Color.rgb(255, 196, 0));
            canvas.drawCircle((int) x - 3, (int) y + 1, (int) (PLAYER_SIZE * 0.95), paint);
            paint.setColor(Color.WHITE);
            canvas.drawCircle((int) (x - PLAYER_SIZE / 2.5), (int) y - 5, PLAYER_SIZE / 5, paint);
            canvas.drawCircle((int) (x + PLAYER_SIZE / 2.5), (int) y - 5, PLAYER_SIZE / 5, paint);
            paint.setColor(Color.BLACK);
            canvas.drawCircle((int) x - PLAYER_SIZE / 2, (int) (y - PLAYER_SIZE / 6.25), 3, paint);
            canvas.drawCircle((int) x + PLAYER_SIZE / 2, (int) (y - PLAYER_SIZE / 6.25), 3, paint);
            canvas.drawOval((float) (x - PLAYER_SIZE / 3.125), (float) y + 5, (float) (x + PLAYER_SIZE / 3.125), (float) y + PLAYER_SIZE - 5, paint);
            paint.setColor(Color.RED);
            canvas.drawOval((float) (x - PLAYER_SIZE / 6.25), (float) (y + PLAYER_SIZE / 1.5), (float) (x + PLAYER_SIZE / 6.25), (float) (y + PLAYER_SIZE / 1.16), paint);
            // score
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText(score + "s", screenWidth / 2, 100, paint);
            paint.setTextSize(15);

            if (ACCELEROMETER_DEBUG_MODE) {
                paint.setColor(Color.GREEN);
                canvas.drawText("xSpeed : " + xSpeed, 3 * (screenWidth / 4), 100, paint);
                canvas.drawText("ySpeed : " + ySpeed, 3 * (screenWidth / 4), 125, paint);
                canvas.drawCircle(screenWidth / 2, screenHeight / 2, 2, paint);
                canvas.drawLine((float) ((screenWidth / 2) - 50 + xSpeed / SPEED * 30), (float) ((screenHeight / 2) + ySpeed / SPEED * 30), (float) ((screenWidth / 2) + 50 + xSpeed / SPEED * 30), (float) ((screenHeight / 2) + ySpeed / SPEED * 30), paint);
                canvas.drawLine((float) ((screenWidth / 2) + xSpeed / SPEED * 30), (float) ((screenHeight / 2) - 50 + ySpeed / SPEED * 30), (float) ((screenWidth / 2) + xSpeed / SPEED * 30), (float) ((screenHeight / 2) + 50 + ySpeed / SPEED * 30), paint);
                canvas.drawText(System.currentTimeMillis() - time + " Hz", screenWidth - 75, 30, paint);
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
