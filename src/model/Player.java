package model;

public class Player {

    public int x, y;
    public int w = 30, h = 30;
    public int speed = 5;

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
