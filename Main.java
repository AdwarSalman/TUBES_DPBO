import model.BenefitModel;
import presenter.MenuPresenter;
import view.MenuView;

/**
 * Saya Muhammad Adwar Salman 2401539 mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah
 * Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya
 * tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
 */

/** Credit Game assets
 * Rock (Creative Commons Zero, CC0) - http://creativecommons.org/publicdomain/zero/1.0/
 * Player, Bullets, Explosion Background, Clouds & Alien/Enemy (RAVENMORE INDUSTRIES) - https://ravenmore.itch.io/space-shooter-assets-space-rage
 */

public class Main {
    public static void main(String[] args) {
        // Menerapkan pola arsitektur MVP (Model-View-Presenter)
        // untuk memisahkan logika bisnis, tampilan, dan data.

        // 1. Init Model (Data & Database)
        BenefitModel model = new BenefitModel();

        // 2. Init View (Tampilan Menu)
        MenuView menu = new MenuView();

        // 3. Init Presenter (Penghubung Logika)
        MenuPresenter presenter = new MenuPresenter(model, menu);

        // 4. Menghubungkan View ke Presenter agar interaksi tombol bisa diproses
        menu.setPresenter(presenter);

        // 5. Menampilkan GUI Utama
        menu.setVisible(true);
    }
}