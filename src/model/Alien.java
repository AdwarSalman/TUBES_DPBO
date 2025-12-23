package model;

import java.awt.Rectangle;

public class Alien {

    public int x, y;
    public int w = 25, h = 25;

    // Kecepatan
    private int speedY = 2;   // gerak ke atas
    private int speedX = 2;   // zig-zag horizontal (amplitudo)

    private int dirX;         // -1 atau +1

    public Alien(int x, int y) {
        this.x = x;
        this.y = y;

        // arah awal zig-zag random
        dirX = Math.random() < 0.5 ? -1 : 1;
    }

    // === GERAK ALIEN ===
    public void update(int screenWidth) {

        // Gerak vertikal: SELALU ke atas
        y -= speedY;

        // Gerak horizontal zig-zag
        x += dirX * speedX;

        // Jika mentok kiri / kanan → balik arah
        if (x <= 0) {
            x = 0;
            dirX = 1;
        }
        else if (x >= screenWidth - w) {
            x = screenWidth - w;
            dirX = -1;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
}
