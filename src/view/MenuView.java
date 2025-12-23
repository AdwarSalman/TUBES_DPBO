package view;

import presenter.MenuPresenter;

import javax.swing.*;
import java.awt.*;

public class MenuView extends JFrame {

    private MenuPresenter presenter;

    private JTextField inputUsername;
    private JTable table;
    private JButton btnPlay;
    private JButton btnQuit;

    public MenuView() {
        setTitle("Hide and Seek - Menu");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // ===================== TOP AREA: INPUT USERNAME =====================
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Username: "));

        inputUsername = new JTextField(15);
        topPanel.add(inputUsername);

        btnPlay = new JButton("Play");
        topPanel.add(btnPlay);

        btnQuit = new JButton("Quit");
        topPanel.add(btnQuit);

        add(topPanel, BorderLayout.NORTH);


        // ===================== CENTER AREA: TABLE =====================
        table = new JTable();
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);


        // ===================== EVENT BUTTONS =====================
        btnPlay.addActionListener(e -> {
            if (presenter != null)
                presenter.onPlayClicked();
        });

        btnQuit.addActionListener(e -> {
            if (presenter != null)
                presenter.onQuitClicked();
        });
    }

    public void setPresenter(MenuPresenter presenter) {
        this.presenter = presenter;
    }

    public String getUsernameInput() {
        return inputUsername.getText().trim();
    }

    public void updateTable(Object[][] data, String[] cols) {
        table.setModel(new javax.swing.table.DefaultTableModel(data, cols));
    }
}
