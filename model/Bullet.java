package model;

import java.awt.Rectangle;

public class Bullet {
    public double x, y;
    public double vx, vy; // Velocity Vector
    public boolean fromPlayer; // Penanda kepemilikan peluru
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

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
}