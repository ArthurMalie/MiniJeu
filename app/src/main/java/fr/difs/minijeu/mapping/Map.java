package fr.difs.minijeu.mapping;

import java.util.List;

public class Map {

    private int level;
    private double spawnX;
    private double spawnY;
    private List<Wall> walls;

    public Map(int level, double spawnX, double spawnY, List<Wall> walls) {
        this.level = level;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.walls = walls;
    }

    public int getLevel() {
        return level;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public Wall getWall(int i) {
        return walls.get(i);
    }

    public double getSpawnX() {
        return spawnX;
    }

    public double getSpawnY() {
        return spawnY;
    }
}
