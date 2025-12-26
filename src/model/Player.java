package model;

public class Player {

    public int x, y;
    public int w = 50, h = 50;
    public int speed = 3;

    public int ammo;

    public Player(int x, int y, int ammoAwal) {
        this.x = x;
        this.y = y;
        this.ammo = ammoAwal;
    }

    public void move(int dx, int dy) {
        x += dx * speed;
        y += dy * speed;
    }

    public java.awt.Rectangle getBounds() {
        return new java.awt.Rectangle(x, y, w, h);
    }
}
