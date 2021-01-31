package fr.difs.minijeu.mapping;

public class Wall {

    private double left;
    private double top;
    private double right;
    private double bottom;

    public Wall(double left, double top, double xb, double bottom) {
        this.left = left;
        this.top = top;
        this.right = xb;
        this.bottom = bottom;
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
}
