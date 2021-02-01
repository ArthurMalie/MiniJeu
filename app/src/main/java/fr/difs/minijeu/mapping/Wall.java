package fr.difs.minijeu.mapping;

public class Wall {

    private double left;
    private double top;
    private double right;
    private double bottom;

    public Wall(double left, double top, double right, double bottom) {
        this.left = left / 20;
        this.top = top / 32;
        this.right = right / 20;
        this.bottom = bottom / 32;
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

    public void setDimensions(int width, int height) {
        left *= width;
        right *= width;
        top *= height;
        bottom *= height;
    }
}
