package fr.difs.minijeu.mapping;

import java.util.List;

import fr.difs.minijeu.mapping.entities.Entity;

public class Map {

    private int level;
    private double spawnX;
    private double spawnY;
    private double playerSize;
    private List<Wall> walls;
    private List<Entity> entities;
    private int screenWidth;
    private int screenHeight;

    public Map(int level, double spawnX, double spawnY, double playerSize, List<Wall> walls, List<Entity> entities) {
        this.level = level;
        this.spawnX = spawnX / 20;
        this.spawnY = spawnY / 32;
        this.playerSize = playerSize / 40;
        this.walls = walls;
        this.entities = entities;
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

    public double getPlayerSize() {
        return playerSize;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setDimensions(int width, int height) {
        spawnX *= width;
        spawnY *= height;
        playerSize *= width;
        for(Wall wall : walls)
            wall.setDimensions(width, height);
        for(Entity entity : entities)
            entity.setDimensions(width, height);
    }
}
