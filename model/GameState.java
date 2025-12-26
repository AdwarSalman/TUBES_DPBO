package model;

import java.util.ArrayList;
import java.awt.Rectangle; // [PENTING] Import ini diperlukan untuk logika cek tabrakan spawn

/**
 * Kelas kontainer yang menyimpan seluruh "state" atau kondisi permainan saat ini.
 * Memudahkan passing data antar kelas dan reset permainan.
 */
public class GameState {

    // Entitas Game
    public Player player;
    public ArrayList<Alien> aliens;
    public ArrayList<Bullet> bullets;
    public ArrayList<Rock> rocks;
    public ArrayList<Explosion> explosions;

    // Status Permainan
    public boolean running;
    public int score;
    public int missCount;
    public int wave = 1;

    // Dimensi Layar (Konstanta)
    public int screenW = 800;
    public int screenH = 600;

    public GameState() {
        // Inisialisasi List
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        rocks = new ArrayList<>();
        explosions = new ArrayList<>();
    }

    // Mereset permainan ke kondisi awal (atau kondisi load save)
    public void reset(int savedScore, int savedMiss, int savedAmmo) {
        aliens.clear();
        bullets.clear();
        rocks.clear();
        explosions.clear();

        // Mengembalikan statistik ke nilai yang tersimpan di DB
        this.score = savedScore;
        this.missCount = savedMiss;
        this.wave = 1;
        this.running = true;

        // 1. Spawn Player DULUAN di tengah layar
        player = new Player(screenW / 2, screenH / 2, savedAmmo);

        // 2. Procedural Generation Batu dengan "Safe Zone Check"
        int jumlahBatu = 3 + (int)(Math.random() * 3); // Random 3-5 batu

        for (int i = 0; i < jumlahBatu; i++) {
            int rx, ry;
            boolean isOverlapping;
            int attempts = 0; // Penjaga loop (safety break)

            // Loop: Terus cari posisi acak SAMPAI nemu posisi yang aman
            do {
                isOverlapping = false;
                rx = (int)(Math.random() * (screenW - 70)); // 70 = lebar batu
                ry = (int)(Math.random() * (screenH - 70));

                // Buat kotak imajiner untuk batu calon spawn
                Rectangle rockBounds = new Rectangle(rx, ry, 70, 70);

                // Buat "Safe Zone" di sekitar Player (Player bounds + buffer 30px)
                // Agar batu tidak muncul nempel banget dengan player
                Rectangle playerSafeZone = new Rectangle(player.x - 30, player.y - 30, player.w + 60, player.h + 60);

                // Cek 1: Apakah menabrak Player?
                if (rockBounds.intersects(playerSafeZone)) {
                    isOverlapping = true;
                }

                // Cek 2: Apakah menumpuk dengan Batu lain yang sudah ada? (Opsional, tapi bagus biar rapi)
                for (Rock existingRock : rocks) {
                    if (rockBounds.intersects(existingRock.getBounds())) {
                        isOverlapping = true;
                        break;
                    }
                }

                attempts++;
            } while (isOverlapping && attempts < 100); // Batasi max 100x coba biar gak hang jika layar penuh

            // Jika sudah aman, tambahkan batu ke list
            rocks.add(new Rock(rx, ry));
        }
    }
}