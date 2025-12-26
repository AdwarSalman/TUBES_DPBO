package model;

public class Explosion {
    public int x, y;
    public int frameIndex; // Frame animasi saat ini (0 sampai 9)
    public boolean finished; // Penanda kalau animasi sudah selesai

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.frameIndex = 0;
        this.finished = false;
    }

    public void update() {
        frameIndex++;
        // Jika sudah melewati frame terakhir (9), tandai selesai
        if (frameIndex > 9) {
            finished = true;
        }
    }
}