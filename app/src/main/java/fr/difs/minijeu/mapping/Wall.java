package fr.difs.minijeu.mapping;

public class Wall {

    private double left;
    private double top;
    private double right;
    private double bottom;
    private boolean night;

    public Wall(double left, double top, double right, double bottom, boolean night) {
        this.left = left / 20;
        this.top = top / 32;
        this.right = right / 20;
        this.bottom = bottom / 32;
        this.night = night;
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

    public boolean isNight() {
        return night;
    }

    public void setDimensions(int width, int height) {
        left *= width;
        right *= width;
        top *= height;
        bottom *= height;
    }
}
