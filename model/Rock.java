package model;

import java.awt.Rectangle;

public class Rock {
    public int x, y;
    public int w = 70, h = 70;

    public Rock(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
}