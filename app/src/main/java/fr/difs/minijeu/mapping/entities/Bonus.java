package fr.difs.minijeu.mapping.entities;

public abstract class Bonus extends Entity{

    private boolean usable;
    private int nbUses;

    public Bonus(double x, double y, double size, int nbUses) {
        super(x, y, size);
        this.nbUses = nbUses;
        if (nbUses <= 0)
            usable = false;
        else
            usable = true;
    }

    public boolean isUsable() {
        return usable;
    }

    public void use() {
        if(usable)
            nbUses--;
        if(nbUses <= 0)
            usable = false;
    }
}
