package model;

import java.awt.Rectangle;

public class Player {

    public int x, y;
    public int w = 50, h = 50; // Ukuran Hitbox
    public int speed = 3;      // Kecepatan Gerak
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

    // Mendapatkan batas kotak (bounding box) untuk deteksi tabrakan
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
}