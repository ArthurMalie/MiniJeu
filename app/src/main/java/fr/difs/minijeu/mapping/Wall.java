package fr.difs.minijeu.mapping;

public class Wall {

    private double xa;
    private double ya;
    private double xb;
    private double yb;

    public Wall(double xa, double ya, double xb, double yb) {
        this.xa = xa;
        this.ya = ya;
        this.xb = xb;
        this.yb = yb;
    }

    public double getXa() {
        return xa;
    }

    public double getXb() {
        return xb;
    }

    public double getYa() {
        return ya;
    }

    public double getYb() {
        return yb;
    }
}
