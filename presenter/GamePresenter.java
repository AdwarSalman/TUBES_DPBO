package presenter;

import model.*;
import view.GameView;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class GamePresenter implements Runnable {

    // --- Komponen Utama ---
    private BenefitModel model;
    private GameState state;
    private MenuPresenter menuPresenter;
    private GameView gameView;
    private JFrame frame;
    private Thread loop; // Thread untuk menjalankan Game Loop agar UI tidak freeze

    // --- Data Game ---
    private String username;
    private Random rand = new Random();

    // --- Aset Visual & Audio ---
    private BufferedImage imgPlayerCenter, imgPlayerLeft, imgPlayerRight;
    private BufferedImage[] imgAliens, imgExplosion;
    private BufferedImage imgBulletPlayer, imgBulletAlien;
    private BufferedImage imgBackground, imgClouds, imgRock;
    private Clip musicClip;

    // --- Variabel Scrolling & Input ---
    private int bgY = 0, cloudY = 0;
    private boolean isLeft = false, isRight = false, isUp = false, isDown = false;

    // --- Timer Logika ---
    private int alienShootTimer = 0;
    private int nextShootTime = 60;

    public GamePresenter(BenefitModel model, String username, int savedScore, int savedMiss, int savedAmmo, MenuPresenter menuPresenter) {
        this.model = model;
        this.username = username;
        this.menuPresenter = menuPresenter;

        // Inisialisasi GameState
        state = new GameState();
        // Memuat data permainan sebelumnya (Fitur Save & Continue)
        state.reset(savedScore, savedMiss, savedAmmo);

        loadAssets();
    }

    // Memisahkan logika loading aset agar kode lebih terstruktur
    private void loadAssets() {
        try {
            // Load Spritesheet & Images
            imgAliens = new BufferedImage[3];
            imgAliens[0] = loadSprite("/assets/alien_small.png");
            imgAliens[1] = loadSprite("/assets/alien_medium.png");
            imgAliens[2] = loadSprite("/assets/alien_big.png");

            imgPlayerCenter = ImageIO.read(getClass().getResource("/assets/player_center.png"));
            imgPlayerLeft   = ImageIO.read(getClass().getResource("/assets/player_left.png"));
            imgPlayerRight  = ImageIO.read(getClass().getResource("/assets/player_right.png"));
            imgBulletPlayer = ImageIO.read(getClass().getResource("/assets/bullet_player.png"));
            imgBulletAlien  = ImageIO.read(getClass().getResource("/assets/bullet_alien.png"));
            imgBackground   = ImageIO.read(getClass().getResource("/assets/bg_desert.png"));
            imgClouds       = ImageIO.read(getClass().getResource("/assets/bg_clouds.png"));

            // Load Frame Animasi Ledakan
            imgExplosion = new BufferedImage[10];
            for (int i = 0; i < 10; i++) {
                imgExplosion[i] = ImageIO.read(getClass().getResource("/assets/explode_" + i + ".png"));
            }

            // Teknik Resizing Awal: Mengubah ukuran gambar 'Rock' sekali di awal
            // untuk performa rendering yang lebih baik saat runtime.
            BufferedImage originalRock = ImageIO.read(getClass().getResource("/assets/rock.png"));
            imgRock = new BufferedImage(70, 70, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imgRock.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalRock, 0, 0, 70, 70, null);
            g2d.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        frame = new JFrame("Galactic Desert Defense");
        frame.setSize(state.screenW, state.screenH);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameView = new GameView(this);
        frame.add(gameView);
        frame.setVisible(true);

        playMusic("/assets/music_bg.wav");

        // Memulai Thread game
        loop = new Thread(this);
        loop.start();
    }

    @Override
    public void run() {
        // Implementasi Game Loop dengan Delta Time.
        // Menjamin game berjalan stabil (konsisten) di 60 FPS pada spesifikasi komputer yang berbeda.
        long last = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 60.0; // Target update 60 kali per detik
        double delta = 0;

        while (state.running) {
            long now = System.nanoTime();
            delta += (now - last) / nsPerTick;
            last = now;

            while (delta >= 1) {
                update(); // Update logika fisika & aturan game
                delta--;
            }

            gameView.repaint(); // Render grafis

            try { Thread.sleep(2); } catch (Exception e) {} // Mengurangi beban CPU
        }
        gameOver();
    }

    private void update() {
        // 1. Logika Parallax Scrolling (Background bergerak beda kecepatan)
        bgY -= 2;
        if (bgY <= -state.screenH) bgY = 0;
        cloudY -= 5;
        if (cloudY <= -state.screenH) cloudY = 0;

        // 2. Pergerakan Player dengan Solid Collision
        // Mengecek tabrakan sebelum memindahkan posisi. Jika menabrak batu, gerakan dibatalkan.
        // Dipisah X dan Y agar player bisa 'sliding' di sisi objek.
        int dx = 0;
        if (isLeft)  dx = -1;
        if (isRight) dx = 1;
        if (dx != 0) {
            state.player.move(dx, 0);
            for (Rock r : state.rocks) {
                if (state.player.getBounds().intersects(r.getBounds())) {
                    state.player.move(-dx, 0); // Batalkan gerakan X
                    break;
                }
            }
        }

        int dy = 0;
        if (isUp)    dy = -1;
        if (isDown)  dy = 1;
        if (dy != 0) {
            state.player.move(0, dy);
            for (Rock r : state.rocks) {
                if (state.player.getBounds().intersects(r.getBounds())) {
                    state.player.move(0, -dy); // Batalkan gerakan Y
                    break;
                }
            }
        }

        // Membatasi Player agar tetap di dalam layar
        state.player.x = Math.max(0, Math.min(state.player.x, state.screenW - state.player.w));
        state.player.y = Math.max(0, Math.min(state.player.y, state.screenH - state.player.h));

        // 3. Sistem Wave (Gelombang Musuh)
        if (state.aliens.isEmpty()) spawnNextWave();

        // 4. Update Animasi Ledakan
        for (int i = 0; i < state.explosions.size(); i++) {
            Explosion e = state.explosions.get(i);
            e.update();
            if (e.finished) { state.explosions.remove(i); i--; }
        }

        // 5. Update Posisi Alien
        for (int i = 0; i < state.aliens.size(); i++) {
            Alien a = state.aliens.get(i);
            a.update(state.screenW);

            // Deteksi Game Over (Tabrakan Badan)
            if (a.getBounds().intersects(state.player.getBounds())) {
                state.running = false; return;
            }
            // Hapus alien yang lewat batas bawah
            if (a.y + a.h < 0) { state.aliens.remove(i); i--; }
        }

        // 6. Logika Tembakan Alien (Interval Dinamis)
        alienShootTimer++;
        if (alienShootTimer >= nextShootTime) {
            if (!state.aliens.isEmpty()) {
                Alien shooter = state.aliens.get(rand.nextInt(state.aliens.size()));
                state.bullets.add(new Bullet(shooter.x + shooter.w/2, shooter.y, 0, -4, false));
                alienShootTimer = 0;
                nextShootTime = 40 + rand.nextInt(60);
            }
        }

        // 7. Logika Peluru & Deteksi Tabrakan (Collision Detection)
        for (int i = 0; i < state.bullets.size(); i++) {
            Bullet b = state.bullets.get(i);
            b.update();

            // A. Peluru kena Batu?
            boolean hitRock = false;
            for (Rock r : state.rocks) {
                if (b.getBounds().intersects(r.getBounds())) { hitRock = true; break; }
            }
            if (hitRock) { state.bullets.remove(i); i--; continue; }

            // B. Peluru Keluar Layar?
            if (b.y < -10 || b.y > state.screenH + 10) {
                // Mekanisme Reward: Jika Player menghindari peluru Alien, tambah Ammo
                if (!b.fromPlayer) {
                    state.missCount++;
                    state.player.ammo++;
                }
                state.bullets.remove(i); i--; continue;
            }

            // C. Peluru Alien kena Player?
            if (!b.fromPlayer && b.getBounds().intersects(state.player.getBounds())) {
                state.running = false; return;
            }

            // D. Peluru Player kena Alien?
            if (b.fromPlayer) {
                for (int j = 0; j < state.aliens.size(); j++) {
                    Alien a = state.aliens.get(j);
                    if (b.getBounds().intersects(a.getBounds())) {
                        state.score += 100;
                        state.explosions.add(new Explosion(a.x, a.y)); // Spawn efek visual
                        state.aliens.remove(j);
                        state.bullets.remove(i); i--;
                        break;
                    }
                }
            }
        }
    }

    public void onKeyPressed(int key) {
        switch (key) {
            case java.awt.event.KeyEvent.VK_LEFT:  isLeft = true; break;
            case java.awt.event.KeyEvent.VK_RIGHT: isRight = true; break;
            case java.awt.event.KeyEvent.VK_UP:    isUp = true; break;
            case java.awt.event.KeyEvent.VK_DOWN:  isDown = true; break;
            case java.awt.event.KeyEvent.VK_SPACE: state.running = false; backToMenu(); break;
            case java.awt.event.KeyEvent.VK_Z:
                if (state.player.ammo > 0) {
                    state.player.ammo--;
                    state.bullets.add(new Bullet(state.player.x + state.player.w/2, state.player.y + state.player.h, 0, 6, true));
                }
                break;
        }
    }

    public void onKeyReleased(int key) {
        switch (key) {
            case java.awt.event.KeyEvent.VK_LEFT:  isLeft = false; break;
            case java.awt.event.KeyEvent.VK_RIGHT: isRight = false; break;
            case java.awt.event.KeyEvent.VK_UP:    isUp = false; break;
            case java.awt.event.KeyEvent.VK_DOWN:  isDown = false; break;
        }
    }

    private void gameOver() {
        stopMusic();
        // Menyimpan status terakhir ke database (Persistence)
        model.updateStats(username, state.score, state.missCount, state.player.ammo);
        JOptionPane.showMessageDialog(frame, "Game Over! Skor Akhir: " + state.score);
        backToMenu();
    }

    private void backToMenu() {
        stopMusic();
        state.running = false;
        frame.dispose();
        if (menuPresenter != null) menuPresenter.showMenuAgain();
    }

    private void spawnNextWave() {
        int jumlahAlien = 2 + state.wave;
        for (int i = 0; i < jumlahAlien; i++) {
            state.aliens.add(new Alien(rand.nextInt(state.screenW - 50), state.screenH + (i * 60), rand.nextInt(3)));
        }
        state.wave++;
    }

    public void render(Graphics g) {
        // Teknik Layering: Gambar Background -> Objek -> UI

        // 1. Background
        if (imgBackground != null) {
            g.drawImage(imgBackground, 0, bgY, state.screenW, state.screenH, null);
            g.drawImage(imgBackground, 0, bgY + state.screenH, state.screenW, state.screenH, null);
        } else { g.setColor(Color.BLACK); g.fillRect(0, 0, state.screenW, state.screenH); }

        if (imgClouds != null) {
            g.drawImage(imgClouds, 0, cloudY, state.screenW, state.screenH, null);
            g.drawImage(imgClouds, 0, cloudY + state.screenH, state.screenW, state.screenH, null);
        }

        // 2. Objects (Rock, Player, Alien)
        for (Rock r : state.rocks) g.drawImage(imgRock, r.x, r.y, r.w, r.h, null);

        BufferedImage playerSprite = imgPlayerCenter;
        if (isLeft) playerSprite = imgPlayerLeft;
        else if (isRight) playerSprite = imgPlayerRight;
        g.drawImage(playerSprite, state.player.x, state.player.y, state.player.w, state.player.h, null);

        for (Alien a : state.aliens) {
            BufferedImage sprite = (imgAliens != null) ? imgAliens[a.type] : null;
            if (sprite != null) { a.w = sprite.getWidth(); a.h = sprite.getHeight(); g.drawImage(sprite, a.x, a.y, null); }
            else { g.setColor(Color.RED); g.fillOval(a.x, a.y, a.w, a.h); }
        }

        for (Explosion e : state.explosions) {
            if (imgExplosion != null && e.frameIndex < imgExplosion.length)
                g.drawImage(imgExplosion[e.frameIndex], e.x - 10, e.y - 10, 50, 50, null);
        }

        for (Bullet b : state.bullets) {
            BufferedImage bSprite = b.fromPlayer ? imgBulletPlayer : imgBulletAlien;
            int size = b.size * 3;
            if (bSprite != null) g.drawImage(bSprite, (int)b.x - size/2, (int)b.y - size/2, size, size, null);
            else { g.setColor(Color.YELLOW); g.fillOval((int)b.x, (int)b.y, b.size, b.size); }
        }

        // 3. UI / HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.drawString("Score: " + state.score, 10, 20);
        g.drawString("Ammo : " + state.player.ammo, 10, 40);
        g.drawString("Miss : " + state.missCount, 10, 60);
        g.drawString("WAVE " + (state.wave - 1), state.screenW - 80, 20);
    }

    private BufferedImage loadSprite(String path) throws IOException {
        BufferedImage sheet = ImageIO.read(getClass().getResource(path));
        return sheet.getSubimage(0, 0, sheet.getWidth() / 2, sheet.getHeight());
    }

    // Method Helper Audio
    private void playMusic(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return;
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInput);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) { musicClip.stop(); musicClip.close(); }
    }
}