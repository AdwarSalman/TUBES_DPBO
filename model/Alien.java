package model;

import java.awt.Rectangle;

public class Alien {

    public int x, y;
    public int w = 30, h = 30;
    public int type; // Tipe Alien: 0=Kecil, 1=Sedang, 2=Besar

    // Variabel untuk logika gerakan Zig-Zag (Sinusoidal Movement)
    private double startX;
    private double time = 0;
    private int speedY = 1;          // Kecepatan vertikal konstan
    private int amplitude = 80;      // Lebar gelombang gerakan (kiri-kanan)
    private double frequency = 0.05; // Kecepatan osilasi gelombang

    public Alien(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.startX = x;

        // Memberikan fase awal acak agar gerakan alien tidak seragam
        this.time = Math.random() * Math.PI * 2;
    }

    public void update(int screenWidth) {
        // [PERBAIKAN DI SINI]
        // Gerak Vertikal: HARUS KURANG (-) AGAR NAIK KE ATAS
        // Karena koordinat Y: 0 itu Atas, 600 itu Bawah.
        // Kita mau Alien dari Bawah (600) ke Atas (0).
        y -= speedY;

        // 2. Update posisi Horizontal (Pola Zig-Zag)
        time += frequency;
        x = (int) (startX + Math.sin(time) * amplitude);

        // Batasi posisi agar alien tidak keluar dari layar kiri/kanan
        if (x < 0) x = 0;
        else if (x > screenWidth - w) x = screenWidth - w;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
}