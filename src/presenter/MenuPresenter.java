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
            data[i] = list.get(i);   // username, skor, miss, sisa peluru
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

        int ammoAwal = model.getLastAmmo(username);

        // BUKA GAME
        GamePresenter game = new GamePresenter(model, username, ammoAwal);
        game.startGame();
    }

    public void onQuitClicked() {
        System.exit(0);
    }
}
