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

    private BenefitModel model;
    private String username;
    private GameState state;
    private MenuPresenter menuPresenter;

    // --- ASET GRAFIS ---
    private BufferedImage imgPlayerCenter;
    private BufferedImage imgPlayerLeft;
    private BufferedImage imgPlayerRight;

    private BufferedImage[] imgAliens;
    private BufferedImage[] imgExplosion;

    private BufferedImage imgBulletPlayer;
    private BufferedImage imgBulletAlien;

    private BufferedImage imgBackground;
    private BufferedImage imgClouds;
    private BufferedImage imgRock;

    // --- ASET AUDIO ---
    private Clip musicClip;

    // --- VARIABEL PARALLAX SCROLLING ---
    private int bgY = 0;
    private int cloudY = 0;

    // --- FLAGS INPUT ---
    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isUp = false;
    private boolean isDown = false;

    private GameView gameView;
    private JFrame frame;
    private Thread loop;

    private Random rand = new Random();
    private int alienShootTimer = 0;
    private int nextShootTime = 60;

    // [UBAH DI SINI] Constructor menerima data savedScore, savedMiss, savedAmmo
    public GamePresenter(BenefitModel model, String username, int savedScore, int savedMiss, int savedAmmo, MenuPresenter menuPresenter) {
        this.model = model;
        this.username = username;
        this.menuPresenter = menuPresenter;

        state = new GameState();

        // [PENTING] Masukkan data lama ke state agar bisa lanjut main
        // (Pastikan file GameState.java sudah kamu update juga sesuai instruksi sebelumnya)
        state.reset(savedScore, savedMiss, savedAmmo);

        loadAssets(); // Memuat gambar dipisah biar rapi
    }

    // Method untuk memuat semua gambar & aset
    private void loadAssets() {
        try {
            // 1. Load Gambar Alien
            imgAliens = new BufferedImage[3];
            imgAliens[0] = loadSprite("/assets/alien_small.png");
            imgAliens[1] = loadSprite("/assets/alien_medium.png");
            imgAliens[2] = loadSprite("/assets/alien_big.png");

            // 2. Load Player
            imgPlayerCenter = ImageIO.read(getClass().getResource("/assets/player_center.png"));
            imgPlayerLeft   = ImageIO.read(getClass().getResource("/assets/player_left.png"));
            imgPlayerRight  = ImageIO.read(getClass().getResource("/assets/player_right.png"));

            // 3. Load Bullet
            imgBulletPlayer = ImageIO.read(getClass().getResource("/assets/bullet_player.png"));
            imgBulletAlien  = ImageIO.read(getClass().getResource("/assets/bullet_alien.png"));

            // 4. Load Explosion
            imgExplosion = new BufferedImage[10];
            for (int i = 0; i < 10; i++) {
                imgExplosion[i] = ImageIO.read(getClass().getResource("/assets/explode_" + i + ".png"));
            }

            // 5. Load Background
            imgBackground = ImageIO.read(getClass().getResource("/assets/bg_desert.png"));
            imgClouds = ImageIO.read(getClass().getResource("/assets/bg_clouds.png"));

            // 6. Load & Resize Rock
            BufferedImage originalRock = ImageIO.read(getClass().getResource("/assets/rock.png"));
            int targetSize = 70;
            imgRock = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imgRock.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalRock, 0, 0, targetSize, targetSize, null);
            g2d.dispose();

            System.out.println("Semua aset visual berhasil dimuat!");

        } catch (Exception e) {
            System.err.println("GAGAL MEMUAT GAMBAR!");
            e.printStackTrace();
        }
    }

    public void startGame() {
        frame = new JFrame("Hide and Seek - Game");
        frame.setSize(state.screenW, state.screenH);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameView = new GameView(this);
        frame.add(gameView);

        frame.setVisible(true);

        // Putar musik
        playMusic("/assets/music_bg.wav");

        loop = new Thread(this);
        loop.start();
    }

    private void playMusic(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("File lagu tidak ditemukan: " + path);
                return;
            }
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInput);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 60.0;
        double delta = 0;

        while (state.running) {
            long now = System.nanoTime();
            delta += (now - last) / nsPerTick;
            last = now;

            while (delta >= 1) {
                update();
                delta--;
            }

            gameView.repaint();

            try {
                Thread.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        gameOver();
    }

    private void update() {
        // --- 1. Scrolling Background ---
        bgY -= 2;
        if (bgY <= -state.screenH) bgY = 0;

        cloudY -= 5;
        if (cloudY <= -state.screenH) cloudY = 0;

        // --- 2. Gerak Player + Tabrakan Batu ---
        int dx = 0;
        if (isLeft)  dx = -1;
        if (isRight) dx = 1;

        if (dx != 0) {
            state.player.move(dx, 0);
            for (Rock r : state.rocks) {
                if (state.player.getBounds().intersects(r.getBounds())) {
                    state.player.move(-dx, 0);
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
                    state.player.move(0, -dy);
                    break;
                }
            }
        }

        if (state.player.x < 0) state.player.x = 0;
        if (state.player.x > state.screenW - state.player.w) state.player.x = state.screenW - state.player.w;
        if (state.player.y < 0) state.player.y = 0;
        if (state.player.y > state.screenH - state.player.h) state.player.y = state.screenH - state.player.h;

        // --- 3. Cek Wave ---
        if (state.aliens.isEmpty()) {
            spawnNextWave();
        }

        // --- 4. Update Ledakan ---
        for (int i = 0; i < state.explosions.size(); i++) {
            Explosion e = state.explosions.get(i);
            e.update();
            if (e.finished) {
                state.explosions.remove(i);
                i--;
            }
        }

        // --- 5. Update Alien ---
        for (int i = 0; i < state.aliens.size(); i++) {
            Alien a = state.aliens.get(i);
            a.update(state.screenW);

            if (a.getBounds().intersects(state.player.getBounds())) {
                state.running = false;
                return;
            }

            if (a.y + a.h < 0) {
                state.aliens.remove(i);
                i--;
            }
        }

        // --- 6. Alien Shooting ---
        alienShootTimer++;
        if (alienShootTimer >= nextShootTime) {
            if (!state.aliens.isEmpty()) {
                int shooterIndex = rand.nextInt(state.aliens.size());
                Alien shooter = state.aliens.get(shooterIndex);
                state.bullets.add(new Bullet(shooter.x + shooter.w / 2, shooter.y, 0, -4, false));
                alienShootTimer = 0;
                nextShootTime = 40 + rand.nextInt(60);
            }
        }

        // --- 7. Update Bullets ---
        for (int i = 0; i < state.bullets.size(); i++) {
            Bullet b = state.bullets.get(i);
            b.update();

            // Kena Batu
            boolean hitRock = false;
            for (Rock r : state.rocks) {
                if (b.getBounds().intersects(r.getBounds())) {
                    hitRock = true;
                    break;
                }
            }
            if (hitRock) {
                state.bullets.remove(i);
                i--;
                continue;
            }

            // Keluar Layar
            if (b.y < -10 || b.y > state.screenH + 10) {
                // BONUS AMMO KALAU MENGHINDARI PELURU ALIEN
                if (!b.fromPlayer) {
                    state.missCount++;
                    state.player.ammo++;
                }
                state.bullets.remove(i);
                i--;
                continue;
            }

            // Kena Player
            if (!b.fromPlayer && b.getBounds().intersects(state.player.getBounds())) {
                state.running = false;
                return;
            }

            // Kena Alien
            if (b.fromPlayer) {
                for (int j = 0; j < state.aliens.size(); j++) {
                    Alien a = state.aliens.get(j);
                    if (b.getBounds().intersects(a.getBounds())) {
                        state.score += 100;
                        state.explosions.add(new Explosion(a.x, a.y));
                        state.aliens.remove(j);
                        state.bullets.remove(i);
                        i--;
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

        // [UBAH] Simpan Skor & Ammo Terakhir ke Database
        // Karena sistemnya sekarang "Lanjut Main", kita timpa data lama dengan data terbaru
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
            int x = rand.nextInt(state.screenW - 50);
            int y = state.screenH + (i * 60);
            state.aliens.add(new Alien(x, y, rand.nextInt(3)));
        }
        state.wave++;
    }

    public void render(Graphics g) {
        // 1. BG
        if (imgBackground != null) {
            g.drawImage(imgBackground, 0, bgY, state.screenW, state.screenH, null);
            g.drawImage(imgBackground, 0, bgY + state.screenH, state.screenW, state.screenH, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, state.screenW, state.screenH);
        }

        // 2. CLOUDS
        if (imgClouds != null) {
            g.drawImage(imgClouds, 0, cloudY, state.screenW, state.screenH, null);
            g.drawImage(imgClouds, 0, cloudY + state.screenH, state.screenW, state.screenH, null);
        }

        // 3. ROCKS
        for (Rock r : state.rocks) {
            if (imgRock != null) {
                g.drawImage(imgRock, r.x, r.y, r.w, r.h, null);
            } else {
                g.setColor(Color.GRAY);
                g.fillOval(r.x, r.y, r.w, r.h);
            }
        }

        // 4. PLAYER
        BufferedImage playerSprite = imgPlayerCenter;
        if (isLeft) playerSprite = imgPlayerLeft;
        else if (isRight) playerSprite = imgPlayerRight;

        if (playerSprite != null) {
            g.drawImage(playerSprite, state.player.x, state.player.y, state.player.w, state.player.h, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillOval(state.player.x, state.player.y, state.player.w, state.player.h);
        }

        // 5. ALIENS
        for (Alien a : state.aliens) {
            BufferedImage sprite = null;
            if (imgAliens != null && a.type < imgAliens.length) {
                sprite = imgAliens[a.type];
            }
            if (sprite != null) {
                a.w = sprite.getWidth();
                a.h = sprite.getHeight();
                g.drawImage(sprite, a.x, a.y, null);
            } else {
                g.setColor(Color.RED);
                g.fillOval(a.x, a.y, a.w, a.h);
            }
        }

        // 6. EXPLOSIONS
        for (Explosion e : state.explosions) {
            if (imgExplosion != null && e.frameIndex < imgExplosion.length) {
                g.drawImage(imgExplosion[e.frameIndex], e.x - 10, e.y - 10, 50, 50, null);
            }
        }

        // 7. BULLETS
        for (Bullet b : state.bullets) {
            BufferedImage bSprite = b.fromPlayer ? imgBulletPlayer : imgBulletAlien;
            if (bSprite != null) {
                int size = b.size * 3;
                g.drawImage(bSprite, (int)b.x - size/2, (int)b.y - size/2, size, size, null);
            } else {
                g.setColor(Color.YELLOW);
                g.fillOval((int)b.x, (int)b.y, b.size, b.size);
            }
        }

        // 8. HUD
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.drawString("Score: " + state.score, 10, 20);
        g.drawString("Ammo : " + state.player.ammo, 10, 40);
        g.drawString("Miss : " + state.missCount, 10, 60);
        g.drawString("WAVE " + (state.wave - 1), state.screenW - 80, 20);
    }

    private BufferedImage loadSprite(String path) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResource(path));
            return sheet.getSubimage(0, 0, sheet.getWidth() / 2, sheet.getHeight());
        } catch (Exception e) {
            System.err.println("Gagal load sprite: " + path);
            return null;
        }
    }
}