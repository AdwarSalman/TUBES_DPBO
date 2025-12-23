// Saya (NAMA / NIM) mengerjakan evaluasi Tugas Masa Depan
// dalam mata kuliah DPBO untuk keberkahanNya,
// maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.

import model.BenefitModel;
import presenter.MenuPresenter;
import view.MenuView;

public class Main {
    public static void main(String[] args) {

        BenefitModel model = new BenefitModel();
        MenuView menu = new MenuView();

        MenuPresenter presenter = new MenuPresenter(model, menu);
        menu.setPresenter(presenter);

        menu.setVisible(true);
    }
}
