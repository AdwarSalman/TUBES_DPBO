package presenter;

import model.BenefitModel;
import view.MenuView;
import javax.swing.*;

public class MenuPresenter {

    private BenefitModel model;
    private MenuView view;

    public MenuPresenter(BenefitModel model, MenuView view) {
        this.model = model;
        this.view = view;
        refreshTable();
    }

    public void refreshTable() {
        var list = model.getAllUsers();
        Object[][] data = new Object[list.size()][4];
        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i);
        }
        String[] cols = {"Username", "Skor", "Peluru Meleset", "Sisa Peluru"};
        view.updateTable(data, cols);
    }

    public void onPlayClicked() {
        String username = view.getUsernameInput();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Username tidak boleh kosong!");
            return;
        }

        if (!model.userExists(username)) {
            model.insertUser(username);
        }

        int[] stats = model.getUserStats(username);
        int savedScore = stats[0];
        int savedMiss = stats[1];
        int savedAmmo = stats[2];

        view.setVisible(false);

        GamePresenter game = new GamePresenter(model, username, savedScore, savedMiss, savedAmmo, this);
        game.startGame();
    }

    // [BARU] Logika Tombol Hapus
    public void onDeleteClicked() {
        String username = view.getUsernameInput();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pilih user di tabel atau ketik username yang ingin dihapus!");
            return;
        }

        if (!model.userExists(username)) {
            JOptionPane.showMessageDialog(view, "User tidak ditemukan!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Yakin ingin menghapus progress milik '" + username + "'?",
                "Hapus Data", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            model.deleteUser(username);
            view.setUsernameInput(""); // Kosongkan input
            refreshTable();
            JOptionPane.showMessageDialog(view, "Data berhasil dihapus.");
        }
    }

    public void showMenuAgain() {
        refreshTable();
        view.setVisible(true);
    }

    public void onQuitClicked() {
        System.exit(0);
    }
}