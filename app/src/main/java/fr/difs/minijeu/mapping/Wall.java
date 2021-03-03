package fr.difs.minijeu.mapping;

public class Wall {

    private double left;
    private double top;
    private double right;
    private double bottom;
    private String light;

    public Wall(double left, double top, double right, double bottom, String light) {
        this.left = left / 20;
        this.top = top / 32;
        this.right = right / 20;
        this.bottom = bottom / 32;
        this.light = light;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public String getLight() {
        return light;
    }

    public void setDimensions(int width, int height) {
        left *= width;
        right *= width;
        top *= height;
        bottom *= height;
    }
}
