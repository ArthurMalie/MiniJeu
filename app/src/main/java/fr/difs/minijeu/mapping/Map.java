package fr.difs.minijeu.mapping;

import java.util.List;

public class Map {

    private int level;
    private double spawnX;
    private double spawnY;
    private double winX;
    private double winY;
    private List<Wall> walls;
    private List<Hole> Holes;

    public Map(int level, double spawnX, double spawnY, double winX, double winY, List<Wall> walls, List<Hole> holes) {
        this.level = level;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.winX = winX;
        this.winY = winY;
        this.walls = walls;
        Holes = holes;
    }

    public int getLevel() {
        return level;
    }

    public double getSpawnX() {
        return spawnX;
    }

    public double getSpawnY() {
        return spawnY;
    }

    public double getWinX() {
        return winX;
    }

    public double getWinY() {
        return winY;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Hole> getHoles() {
        return Holes;
    }
}
