package fr.difs.minijeu.mapping.entities;

public abstract class Entity {

    private double x;
    private double y;
    private double size;

    public Entity(double x, double y, double size) {
        this.x = x / 20;
        this.y = y / 32;
        this.size = size / 20 / 2;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public void setDimensions(int width, int height) {
        x *= width;
        y *= height;
        size *= width;
    }
}
