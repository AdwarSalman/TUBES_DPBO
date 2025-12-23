package model;

public class Rock {
    public int x, y;
    public int w = 40, h = 40;

    public Rock(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public java.awt.Rectangle getBounds() {
        return new java.awt.Rectangle(x, y, w, h);
    }
}
