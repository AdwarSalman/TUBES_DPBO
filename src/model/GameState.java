package model;

import java.util.ArrayList;

public class GameState {

    public Player player;
    public ArrayList<Alien> aliens;
    public ArrayList<Bullet> bullets;
    public ArrayList<Rock> rocks;
    public ArrayList<Explosion> explosions;

    public boolean running;
    public int score;
    public int missCount;

    public int wave = 1;

    public int screenW = 800;
    public int screenH = 600;

    public GameState() {
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        rocks = new ArrayList<>();
        explosions = new ArrayList<>();
    }

    // [UBAH] Reset sekarang menerima data continue (skor, miss, ammo)
    public void reset(int savedScore, int savedMiss, int savedAmmo) {
        aliens.clear();
        bullets.clear();
        rocks.clear();
        explosions.clear();

        // Load data lama
        this.score = savedScore;
        this.missCount = savedMiss;

        this.wave = 1;
        this.running = true;

        // Init player dengan ammo tersimpan
        player = new Player(screenW / 2, screenH / 2, savedAmmo);

        // Generate batu random
        int jumlahBatu = 3 + (int)(Math.random() * 3);
        for (int i = 0; i < jumlahBatu; i++) {
            int rx = (int)(Math.random() * (screenW - 50));
            int ry = (int)(Math.random() * (screenH - 50));
            rocks.add(new Rock(rx, ry));
        }
    }
}