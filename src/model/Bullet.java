package model;

public class Bullet {

    public double x, y;
    public double vx, vy;
    public boolean fromPlayer;

    public int size = 6;

    public Bullet(double x, double y, double vx, double vy, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.fromPlayer = fromPlayer;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public java.awt.Rectangle getBounds() {
        return new java.awt.Rectangle((int)x, (int)y, size, size);
    }
}
