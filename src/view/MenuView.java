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
    private JButton btnPlay;
    private JButton btnDelete;
    private JButton btnQuit;

    public MenuView() {
        // [UBAH] 1. Ganti Judul Window sesuai Tema
        setTitle("Galactic Desert Defense - Main Menu");
        setSize(700, 550); // Agak dipertinggi sedikit
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // [BARU] 2. Gunakan Custom Background Panel sebagai Content Pane utama
        BackgroundPanel mainPanel = new BackgroundPanel("/assets/bg_menu.png");
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel); // Tempel panel background ke frame

        // ===================== HEADER AREA (JUDUL + INPUT) =====================
        // Kita buat panel penampung vertikal untuk Judul Besar dan Tombol-tombol
        JPanel headerContainer = new JPanel();
        headerContainer.setLayout(new BoxLayout(headerContainer, BoxLayout.Y_AXIS));
        headerContainer.setOpaque(false); // Transparan biar background kelihatan

        // A. Judul Besar di Tengah Atas
        JLabel titleLabel = new JLabel("GALACTIC DESERT DEFENSE");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE); // Warna teks putih biar kontras
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Jarak atas bawah
        headerContainer.add(titleLabel);

        // B. Panel Input dan Tombol (yang lama)
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false); // Transparan

        JLabel lblUser = new JLabel("Username: ");
        lblUser.setForeground(Color.WHITE); // Label jadi putih
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(lblUser);

        inputUsername = new JTextField(15);
        inputPanel.add(inputUsername);

        btnPlay = new JButton("Play / Continue");
        btnPlay.setBackground(new Color(50, 205, 50)); // Hijau yang lebih solid
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setFocusPainted(false);
        inputPanel.add(btnPlay);

        btnDelete = new JButton("Delete User");
        btnDelete.setBackground(new Color(220, 20, 60)); // Merah Crimson
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        inputPanel.add(btnDelete);

        btnQuit = new JButton("Quit");
        btnQuit.setBackground(Color.DARK_GRAY);
        btnQuit.setForeground(Color.WHITE);
        btnQuit.setFocusPainted(false);
        inputPanel.add(btnQuit);

        headerContainer.add(inputPanel);

        // Masukkan container header ke bagian ATAS (NORTH) layout utama
        add(headerContainer, BorderLayout.NORTH);


        // ===================== CENTER AREA: TABLE (SCROLLABLE) =====================
        table = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true); // Agar tabel mengisi area kosong
        table.getTableHeader().setReorderingAllowed(false);

        // Styling Tabel sedikit agar lebih rapi
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.DARK_GRAY);
        table.getTableHeader().setForeground(Color.WHITE);

        // Scroll Pane
        JScrollPane scroll = new JScrollPane(table);
        // Membuat area viewport transparan agar background terlihat jika tabel sedikit
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Padding kiri kanan bawah

        add(scroll, BorderLayout.CENTER);

        // FITUR AUTO-FILL
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

        // ===================== EVENT BUTTONS =====================
        btnPlay.addActionListener(e -> {
            if (presenter != null) presenter.onPlayClicked();
        });

        btnDelete.addActionListener(e -> {
            if (presenter != null) presenter.onDeleteClicked();
        });

        btnQuit.addActionListener(e -> {
            if (presenter != null) presenter.onQuitClicked();
        });
    }

    public void setPresenter(MenuPresenter presenter) {
        this.presenter = presenter;
    }

    public String getUsernameInput() {
        return inputUsername.getText().trim();
    }

    public void setUsernameInput(String text) {
        inputUsername.setText(text);
    }

    public void updateTable(Object[][] data, String[] cols) {
        table.setModel(new DefaultTableModel(data, cols));
    }

    // =================================================================
    // INNER CLASS: PANEL KHUSUS UNTUK BACKGROUND GAMBAR
    // =================================================================
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                // Load gambar dari assets
                backgroundImage = ImageIO.read(getClass().getResource(imagePath));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Background menu gagal dimuat: " + imagePath);
                // Fallback warna gelap jika gambar gagal
                setBackground(Color.DARK_GRAY);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Jika gambar berhasil diload, gambar memenuhi panel
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}