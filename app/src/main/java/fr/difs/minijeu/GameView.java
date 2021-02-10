package fr.difs.minijeu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import fr.difs.minijeu.mapping.entities.Bonus;
import fr.difs.minijeu.mapping.entities.MaxiBonus;
import fr.difs.minijeu.mapping.entities.Entity;
import fr.difs.minijeu.mapping.Map;
import fr.difs.minijeu.mapping.Wall;
import fr.difs.minijeu.mapping.entities.Hole;
import fr.difs.minijeu.mapping.entities.MiniBonus;
import fr.difs.minijeu.mapping.entities.WinBonus;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // Mode debug : invincibilité des bords + croix de précision pour l'accelérometre + affichage de la fréquence de rafraichissement du jeu
    private final boolean ACCELEROMETER_DEBUG_MODE = true;
    // Vitesse du joueur (multiplicateur)
    private final double SPEED = 4;

    // Niveau sur lequel la partie va s'effectuer (contient les murs et les entités)
    private Map map;
    // Position du joueur
    private double x;
    private double y;
    // Taille actuelle du joueur
    private float playerSize;
    // Vitesse actuelle du joueur
    private double xSpeed;
    private double ySpeed;
    // Luminosité
    private int light;
    // Score actuel du joueur
    private double score;
    // Dimensions de l'écran
    private int screenWidth;
    private int screenHeight;
    // temps en ms pour le calcul de la fréquence de rafraichissement
    private long time;
    // Bitmap de l'image de la bille
    Bitmap bitmap;

    private GameThread thread;
    private Handler handler;
    private Runnable compteur;

    // Constructeur
    public GameView(Context context, Map map) {
        super(context);

        // Récupération de la résolution de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        // Image de la bille
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bille);

        this.map = map;
        map.setDimensions(screenWidth, screenHeight);

        time = 0;
        score = 0;
        xSpeed = 0;
        ySpeed = 0;

        // Tout au long de la partie, on rajoute 1 au score tous les centiemes de seconde
        handler = new Handler();
        compteur = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 10);
                score++;
            }
        };
        // On lance le compteur
        handler.postDelayed(compteur, 0);

        // taille du joueur
        playerSize = (float) (map.getPlayerSize());

        // Apparition du joueur
        x = map.getSpawnX();
        y = map.getSpawnY();

        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);


        setFocusable(true);
    }

    public void update() {

        double newXSpeed = xSpeed;
        double newYSpeed = ySpeed;

        List<Wall> collides = collide(xSpeed, ySpeed);

        while (collides.size() > 0) {
            while (collides.size() > 1) {
                newXSpeed *= 0.9;
                newYSpeed *= 0.9;
                collides = collide(newXSpeed, newYSpeed);
            }
            if (collide(0, newYSpeed).isEmpty()) {
                newXSpeed *= 0.9;
            } else if (collide(newXSpeed, 0).isEmpty()) {
                newYSpeed *= 0.9;
            } else {
                while (collide(newXSpeed, newYSpeed).size() == 1) {
                    if (xSpeed > 0)
                        newXSpeed -= 0.001;
                    else
                        newXSpeed += 0.001;
                    if (ySpeed > 0)
                        newYSpeed -= 0.001;
                    else
                        newYSpeed += 0.001;
                }
            }
            collides = collide(newXSpeed, newYSpeed);
        }

        x += newXSpeed;
        y += newYSpeed;


        // Si le joueur touche une entité
        double distance = 0;
        for (Entity entity : map.getEntities()) {
            distance = Math.sqrt((x - entity.getX()) * (x - entity.getX()) + (y - entity.getY()) * (y - entity.getY()));
            if (entity instanceof Hole && playerSize <= (entity.getSize())
                    && distance <= entity.getSize()) {
                end(false);
            }
            if (entity instanceof Bonus && ((Bonus) entity).isUsable()
                    && distance <= entity.getSize() + playerSize) {
                if (entity instanceof WinBonus && playerSize <= entity.getSize())
                    end(true);
                if (entity instanceof MiniBonus) {
                    playerSize /= 2;
                    ((MiniBonus) entity).use();
                }
                if (entity instanceof MaxiBonus) {
                    playerSize *= 2;
                    ((MaxiBonus) entity).use();
                }
            }
        }

        // Si le joueur touche un bord de l'écran, on stoppe le compteur, on arrête le GameThread et on appelle la méthode endGame qui passe le score et false (perdu) à l'activité de fin de jeu.
        if ((x <= playerSize || y <= playerSize || x >= screenWidth - playerSize || y >= screenHeight - playerSize)
                && !ACCELEROMETER_DEBUG_MODE) {
            end(false);
        }

    }

    public void setLight(int light) {
        this.light = light;
    }

    public void move(double x, double y) {
        xSpeed = -x * SPEED;
        ySpeed = y * SPEED;
        if (xSpeed > playerSize)
            xSpeed = playerSize;
        if (ySpeed > playerSize)
            ySpeed = playerSize;
        if (xSpeed < -playerSize)
            xSpeed = -playerSize;
        if (ySpeed < -playerSize)
            ySpeed = -playerSize;
    }

    public List<Wall> collide(double xSpeed, double ySpeed) {

        List<Wall> walls = new ArrayList<>();

        for (Wall wall : map.getWalls()) {
            double testX = x + xSpeed;
            double testY = y + ySpeed;

            // which edge is closest?
            if (x + xSpeed < wall.getLeft())
                testX = wall.getLeft();      // test left edge
            else if (x + xSpeed > wall.getRight())
                testX = wall.getRight();   // right edge
            if (y + ySpeed < wall.getTop())
                testY = wall.getTop();      // top edge
            else if (y + ySpeed > wall.getBottom())
                testY = wall.getBottom();   // bottom edge

            // get distance from closest edges
            double distX = x + xSpeed - testX;
            double distY = y + ySpeed - testY;
            double distance = Math.sqrt((distX * distX) + (distY * distY));

            // if the distance is less than the radius, collision!
            if (distance <= playerSize)
                walls.add(wall);
        }
        return walls;
    }

    public void end(boolean win) {
        handler.removeCallbacks(compteur);
        thread.setRunning(false);
        ((GameActivity) getContext()).endGame(score / 100, win);
    }

    public void pause() {
        thread.setRunning(false);
        // On stoppe le compteur
        handler.removeCallbacks(compteur);
    }

    public void resume() {
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
        // On relance le compteur
        handler.postDelayed(compteur, 0);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            Paint paint = new Paint();

            // background
            canvas.drawColor(Color.rgb(56, 40, 14));

            // entities
            for (Entity entity : map.getEntities()) {
                if (entity instanceof Hole) {
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(
                            (float) entity.getX(),
                            (float) entity.getY(),
                            (float) entity.getSize(), paint);
                }
                if (entity instanceof WinBonus) {
                    paint.setColor(Color.rgb(255, 225, 0));
                    canvas.drawCircle(
                            (float) entity.getX(),
                            (float) entity.getY(),
                            (float) entity.getSize(), paint);
                    paint.setColor(Color.rgb(16, 0, 161));
                    paint.setTextSize((float) (entity.getSize()));
                    canvas.drawText("W",
                            (float) (entity.getX() - (entity.getSize()) / 2.2),
                            (float) (entity.getY() + (entity.getSize() / 2.2)), paint);
                }
                if (entity instanceof MiniBonus && ((MiniBonus) entity).isUsable()) {
                    paint.setColor(Color.rgb(246, 71, 255));
                    canvas.drawRect(
                            (float) (entity.getX() - entity.getSize()),
                            (float) (entity.getY() - entity.getSize()),
                            (float) (entity.getX() + entity.getSize()),
                            (float) (entity.getY() + entity.getSize()), paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawLine(
                            (float) (entity.getX() - entity.getSize() / 2),
                            (float) (entity.getY()),
                            (float) (entity.getX() + entity.getSize() / 2),
                            (float) (entity.getY()), paint);
                }
                if (entity instanceof MaxiBonus && ((MaxiBonus) entity).isUsable()) {
                    paint.setColor(Color.GREEN);
                    canvas.drawRect(
                            (float) (entity.getX() - entity.getSize()),
                            (float) (entity.getY() - entity.getSize()),
                            (float) (entity.getX() + entity.getSize()),
                            (float) (entity.getY() + entity.getSize()), paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawLine(
                            (float) (entity.getX() - entity.getSize() / 2),
                            (float) (entity.getY()),
                            (float) (entity.getX() + entity.getSize() / 2),
                            (float) (entity.getY()), paint);
                    canvas.drawLine(
                            (float) (entity.getX()),
                            (float) (entity.getY() - entity.getSize() / 2),
                            (float) (entity.getX()),
                            (float) (entity.getY() + entity.getSize() / 2), paint);
                }
            }

            // player
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (playerSize * 2), (int) (playerSize * 2), true);
            canvas.drawBitmap(bitmap, (float) x - playerSize, (float) y - playerSize, null);

            // smiley
//            paint.setColor(Color.rgb(240, 154, 5));
//            canvas.drawCircle((int) x, (int) y, playerSize, paint);
//            paint.setColor(Color.rgb(255, 196, 0));
//            canvas.drawCircle((int) x - 3, (int) y + 1, (int) (playerSize * 0.95), paint);
//            paint.setColor(Color.WHITE);
//            canvas.drawCircle((int) (x - playerSize / 2.5), (int) y - 5, playerSize / 5, paint);
//            canvas.drawCircle((int) (x + playerSize / 2.5), (int) y - 5, playerSize / 5, paint);
//            paint.setColor(Color.BLACK);
//            canvas.drawCircle((int) x - playerSize / 2, (int) (y - playerSize / 6.25), 3, paint);
//            canvas.drawCircle((int) x + playerSize / 2, (int) (y - playerSize / 6.25), 3, paint);
//            canvas.drawOval((float) (x - playerSize / 3.125), (float) y + 5, (float) (x + playerSize / 3.125), (float) y + playerSize - 5, paint);
//            paint.setColor(Color.RED);
//            canvas.drawOval((float) (x - playerSize / 6.25), (float) (y + playerSize / 1.5), (float) (x + playerSize / 6.25), (float) (y + playerSize / 1.16), paint);

            // murs de la map
            paint.setColor(Color.rgb(150, 125, 100));
            if (map != null) {
                for (Wall wall : map.getWalls()) {
                    if (light < 10 && wall.isNight()
                            || light >= 10 && !wall.isNight())
                        canvas.drawRect(
                                (float) wall.getLeft(),
                                (float) wall.getTop(),
                                (float) wall.getRight(),
                                (float) wall.getBottom(), paint);
                }
            }

            // score
            paint.setColor(Color.WHITE);
            paint.setTextSize(screenHeight / 16);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText((int) score / 100 + "", screenWidth / 2, 80, paint);

            if (ACCELEROMETER_DEBUG_MODE) {
                paint.setTextSize(15);
                paint.setColor(Color.GREEN);
                canvas.drawText("light : " + light, 3 * (screenWidth / 4), 100, paint);
                canvas.drawText("y : " + ySpeed, 3 * (screenWidth / 4), 125, paint);
                canvas.drawCircle(screenWidth / 2, screenHeight / 2, 2, paint);
                canvas.drawLine(
                        (float) ((screenWidth / 2) - 50 + xSpeed / SPEED * 30),
                        (float) ((screenHeight / 2) + ySpeed / SPEED * 30),
                        (float) ((screenWidth / 2) + 50 + xSpeed / SPEED * 30),
                        (float) ((screenHeight / 2) + ySpeed / SPEED * 30), paint);
                canvas.drawLine(
                        (float) ((screenWidth / 2) + xSpeed / SPEED * 30),
                        (float) ((screenHeight / 2) - 50 + ySpeed / SPEED * 30),
                        (float) ((screenWidth / 2) + xSpeed / SPEED * 30),
                        (float) ((screenHeight / 2) + 50 + ySpeed / SPEED * 30), paint);
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
