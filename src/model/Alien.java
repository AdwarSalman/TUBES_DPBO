package model;

import java.awt.Rectangle;

public class Alien {

    public int x, y;
    public int w = 30, h = 30;

    // [BARU] Menyimpan tipe alien (0 = Kecil, 1 = Sedang, 2 = Besar)
    public int type;

    // Variabel gerak zig-zag
    private double startX;
    private double time = 0;
    private int speedY = 1;
    private int amplitude = 80;
    private double frequency = 0.05;

    // [UBAH] Constructor menerima parameter 'type'
    public Alien(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type; // Simpan tipe alien ini selamanya

        this.startX = x;
        this.time = Math.random() * Math.PI * 2;
    }

    public void update(int screenWidth) {
        y -= speedY;

        time += frequency;
        x = (int) (startX + Math.sin(time) * amplitude);

        if (x < 0) x = 0;
        else if (x > screenWidth - w) x = screenWidth - w;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
}