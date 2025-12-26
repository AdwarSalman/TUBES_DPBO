package model;

public class Explosion {
    public int x, y;
    public int frameIndex; // Frame animasi saat ini (0-9)
    public boolean finished;

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.frameIndex = 0;
        this.finished = false;
    }

    public void update() {
        // Increment frame animasi setiap tick
        frameIndex++;
        if (frameIndex > 9) {
            finished = true; // Tandai selesai agar dihapus dari memori
        }
    }
}