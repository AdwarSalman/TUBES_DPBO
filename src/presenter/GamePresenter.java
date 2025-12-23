package presenter;

import model.*;
import view.GameView;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GamePresenter implements Runnable {

    private BenefitModel model;
    private String username;
    private GameState state;

    private GameView gameView;
    private JFrame frame;

    private Thread loop;
    private int ammoAwal;

    Random rand = new Random();

    public GamePresenter(BenefitModel model, String username, int ammoAwal) {
        this.model = model;
        this.username = username;
        this.ammoAwal = ammoAwal;

        state = new GameState();
        state.reset(ammoAwal);
    }

    public void startGame() {
        frame = new JFrame("Hide and Seek - Game");
        frame.setSize(state.screenW, state.screenH);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameView = new GameView(this);
        frame.add(gameView);

        frame.setVisible(true);

        loop = new Thread(this);
        loop.start();
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
            }
        }

        gameOver();
    }

    private void update() {

        // ========== SPawn Alien ==========
        if (rand.nextInt(20) == 0) { // tiap beberapa frame
            int x = rand.nextInt(state.screenW - 50);
            state.aliens.add(new Alien(x, state.screenH));
        }

        // ========== Update Alien ==========
        for (int i = 0; i < state.aliens.size(); i++) {
            Alien a = state.aliens.get(i);

            // a.update();  // HAPUS
            a.update(state.screenW); // GANTI

            // Jika alien terlalu dekat dengan player → Game Over
            if (a.getBounds().intersects(state.player.getBounds())) {
                state.running = false;
                return;
            }

            // Alien menembak random
            if (rand.nextInt(60) == 0) {
                state.bullets.add(new Bullet(
                        a.x + a.w / 2,
                        a.y + a.h,
                        0,
                        3,
                        false
                ));
            }
        }


        // ========== Update Bullet ==========
        for (int i = 0; i < state.bullets.size(); i++) {
            Bullet b = state.bullets.get(i);
            b.update();

            // Keluar layar = alien miss
            if (b.y < -10 || b.y > state.screenH + 10) {
                if (!b.fromPlayer) {
                    state.missCount++;
                }
                state.bullets.remove(i);
                i--;
                continue;
            }

            // Collision bullet alien → player
            if (!b.fromPlayer && b.getBounds().intersects(state.player.getBounds())) {
                state.running = false; // game over
                return;
            }

            // Collision bullet player → alien
            if (b.fromPlayer) {
                for (int j = 0; j < state.aliens.size(); j++) {
                    if (b.getBounds().intersects(state.aliens.get(j).getBounds())) {
                        state.score += 101;
                        state.aliens.remove(j);
                        state.bullets.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }

        // ========== Ammo bertambah kalau miss cukup ==========
        if (state.missCount >= 10) { // threshold contoh
            state.player.ammo += 10;
            state.missCount = 0; // reset
        }
    }

    public void onKeyPressed(int key) {

        switch (key) {
            case java.awt.event.KeyEvent.VK_LEFT:
                state.player.move(-5, 0);
                break;
            case java.awt.event.KeyEvent.VK_RIGHT:
                state.player.move(5, 0);
                break;
            case java.awt.event.KeyEvent.VK_UP:
                state.player.move(0, -5);
                break;
            case java.awt.event.KeyEvent.VK_DOWN:
                state.player.move(0, 5);
                break;

            case java.awt.event.KeyEvent.VK_SPACE:
                state.running = false;   // kembali menu
                break;

            case java.awt.event.KeyEvent.VK_Z:
                if (state.player.ammo > 0) {
                    state.player.ammo--;
                    state.bullets.add(new Bullet(state.player.x, state.player.y, 0, -6, true));
                }
                break;
        }
    }

    private void gameOver() {
        // simpan ke DB
        model.updateStats(
                username,
                state.score,
                state.missCount,
                state.player.ammo
        );

        JOptionPane.showMessageDialog(frame, "Game Over!");

        frame.dispose();
    }

    public void render(Graphics g) {

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, state.screenW, state.screenH);

        // ROCK (batu)
        g.setColor(Color.GRAY);
        for (Rock r : state.rocks) {
            g.fillRect(r.x, r.y, r.w, r.h);
        }

        // Player
        g.setColor(Color.GREEN);
        g.fillRect(state.player.x, state.player.y, state.player.w, state.player.h);

        // Alien
        g.setColor(Color.RED);
        for (Alien a : state.aliens) {
            g.fillRect(a.x, a.y, a.w, a.h);
        }

        // Bullet
        g.setColor(Color.YELLOW);
        for (Bullet b : state.bullets) {
            g.fillOval((int) b.x, (int) b.y, b.size, b.size);
        }

        // HUD
        g.setColor(Color.WHITE);
        g.drawString("Score: " + state.score, 10, 20);
        g.drawString("Ammo: " + state.player.ammo, 10, 35);
        g.drawString("Miss: " + state.missCount, 10, 50);
    }
}