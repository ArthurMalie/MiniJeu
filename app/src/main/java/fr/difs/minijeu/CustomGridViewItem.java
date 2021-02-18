package fr.difs.minijeu;


public class CustomGridViewItem {

    private int level;
    private float score;
    private boolean unlocked;

    public CustomGridViewItem(int level, float score, boolean unlocked) {
        this.level = level;
        this.score = score;
        this.unlocked = unlocked;
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

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
