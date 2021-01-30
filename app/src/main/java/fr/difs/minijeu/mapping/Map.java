package fr.difs.minijeu.mapping;

import java.util.List;

public class Map {

    private int level;
    private List<Wall> walls;

    public Map(int level, List<Wall> walls) {
        this.level = level;
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
}
