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

        // Agar panel bisa menerima input keyboard
        setFocusable(true);
        requestFocusInWindow();

        // KeyListener untuk meneruskan input ke Presenter
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                presenter.onKeyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                presenter.onKeyReleased(e.getKeyCode());
            }
        });
    }

    // Override metode paintComponent untuk menggambar grafik game
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Delegate rendering ke Presenter agar View tetap "bodoh" (hanya kanvas)
        if (presenter != null) {
            presenter.render(g);
        }
    }
}