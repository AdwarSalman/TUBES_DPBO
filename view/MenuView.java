package view;

import presenter.MenuPresenter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MenuView extends JFrame {

    private MenuPresenter presenter;
    private JTextField inputUsername;
    private JTable table;
    private JButton btnPlay, btnDelete, btnQuit;

    public MenuView() {
        setTitle("Galactic Desert Defense - Main Menu");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menggunakan Panel Custom untuk Background Gambar
        BackgroundPanel mainPanel = new BackgroundPanel("/assets/bg_menu.png");
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // --- Header Area ---
        JPanel headerContainer = new JPanel();
        headerContainer.setLayout(new BoxLayout(headerContainer, BoxLayout.Y_AXIS));
        headerContainer.setOpaque(false); // Transparan

        JLabel titleLabel = new JLabel("GALACTIC DESERT DEFENSE");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerContainer.add(titleLabel);

        // --- Input & Buttons ---
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Username: ");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(lblUser);

        inputUsername = new JTextField(15);
        inputPanel.add(inputUsername);

        btnPlay = new JButton("Play / Continue");
        btnPlay.setBackground(new Color(50, 205, 50));
        btnPlay.setForeground(Color.WHITE);
        inputPanel.add(btnPlay);

        btnDelete = new JButton("Delete User");
        btnDelete.setBackground(new Color(220, 20, 60));
        btnDelete.setForeground(Color.WHITE);
        inputPanel.add(btnDelete);

        btnQuit = new JButton("Quit");
        btnQuit.setBackground(Color.DARK_GRAY);
        btnQuit.setForeground(Color.WHITE);
        inputPanel.add(btnQuit);

        headerContainer.add(inputPanel);
        add(headerContainer, BorderLayout.NORTH);

        // --- Table Area ---
        table = new JTable() {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(false); // Transparan
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        // Event Listener: Klik tabel untuk Auto-Fill
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String username = table.getValueAt(selectedRow, 0).toString();
                    inputUsername.setText(username);
                }
            }
        });

        // Binding Actions
        btnPlay.addActionListener(e -> { if (presenter != null) presenter.onPlayClicked(); });
        btnDelete.addActionListener(e -> { if (presenter != null) presenter.onDeleteClicked(); });
        btnQuit.addActionListener(e -> { if (presenter != null) presenter.onQuitClicked(); });
    }

    public void setPresenter(MenuPresenter presenter) { this.presenter = presenter; }
    public String getUsernameInput() { return inputUsername.getText().trim(); }
    public void setUsernameInput(String text) { inputUsername.setText(text); }
    public void updateTable(Object[][] data, String[] cols) {
        table.setModel(new DefaultTableModel(data, cols));
    }

    // Inner Class untuk Background Painting
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(getClass().getResource(imagePath));
            } catch (Exception e) {
                setBackground(Color.DARK_GRAY);
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}