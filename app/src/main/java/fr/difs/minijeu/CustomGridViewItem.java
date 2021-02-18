package fr.difs.minijeu;


public class CustomGridViewItem {

    private int level;
    private float score;

    public CustomGridViewItem(int level, float score) {
        this.level = level;
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

}
