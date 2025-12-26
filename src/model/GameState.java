package model;

import java.util.ArrayList;

public class GameState {

    public Player player;
    public ArrayList<Alien> aliens;
    public ArrayList<Bullet> bullets;
    public ArrayList<Rock> rocks;
    public ArrayList<Explosion> explosions;

    public boolean running;
    public int score;        // skor round ini
    public int missCount;    // peluru alien meleset round ini

    public int wave = 1;

    public int screenW = 800;
    public int screenH = 600;

    public GameState() {
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        rocks = new ArrayList<>();
        explosions = new ArrayList<>();
    }

    public void reset(int ammoAwal) {
        aliens.clear();
        bullets.clear();
        rocks.clear();
        explosions.clear();

        score = 0;
        missCount = 0;
        wave = 1;
        running = true;

        player = new Player(screenW / 2, screenH / 2, ammoAwal);

        // ================================
        // Generate batu secara random (3–5)
        // ================================
        int jumlahBatu = 3 + (int)(Math.random() * 3); // 3–5 batu
        for (int i = 0; i < jumlahBatu; i++) {
            int rx = (int)(Math.random() * (screenW - 50));
            int ry = (int)(Math.random() * (screenH - 50));

            rocks.add(new Rock(rx, ry));
        }
    }
}
