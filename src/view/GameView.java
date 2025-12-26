package view;

import presenter.GamePresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameView extends JPanel {

    private GamePresenter presenter;

    public GameView(GamePresenter presenter) {
        this.presenter = presenter;

        setFocusable(true);

        // Key listener untuk kontrol game
        // Di dalam Constructor GameView
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                presenter.onKeyPressed(e.getKeyCode());
            }

            // [BARU] Tambahkan ini untuk mendeteksi tombol dilepas
            @Override
            public void keyReleased(KeyEvent e) {
                presenter.onKeyReleased(e.getKeyCode());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        presenter.render(g);
    }
}
