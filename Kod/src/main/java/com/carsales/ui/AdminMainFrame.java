package com.carsales.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import com.carsales.database.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import java.awt.BasicStroke;

public class AdminMainFrame extends JFrame {
    private String username;
    private JTable carTable;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField packageField;
    private JComboBox<String> statusCombo;
    private Integer dealerId;
    private DefaultTableModel tableModel;
    
  
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color SECONDARY_COLOR = new Color(64, 156, 255);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    private final Color TABLE_HEADER_COLOR = new Color(240, 242, 245);
    private final Color TABLE_SELECTED_COLOR = new Color(232, 240, 255);
    private final Color DANGER_COLOR = new Color(220, 53, 69);
    
    private JPanel chartPanel;
    
    public AdminMainFrame(String username) {
        try {
            // Metal Look and Feel'i kullan
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // Button UI'ları için özel ayarlar
            UIManager.put("Button.background", Color.WHITE);
            UIManager.put("Button.opaque", true);
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 10, 5, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.username = username;
        setTitle("Araç Satış Sistemi - Admin Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Admin'in bayi ID'sini al
        loadDealerId();
        
       
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(), new Color(240, 242, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Filtre paneli
        JPanel filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Araç tablosu
        createCarTable();
        JScrollPane tableScrollPane = new JScrollPane(carTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Buton paneli
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Grafik paneli
        JPanel chartPanel = createChartPanel();
        mainPanel.add(chartPanel, BorderLayout.EAST);
        
        add(mainPanel);
        
        // İlk yükleme
        loadCars();
    }
    
    private void createCarTable() {
        String[] columns = {"ID", "Marka", "Model", "Yıl", "Paket", "Fiyat", "Bayi", "Durum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        carTable = new JTable(tableModel);
        carTable.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        carTable.setRowHeight(35);
        carTable.setShowGrid(true);
        carTable.setGridColor(BORDER_COLOR);
        carTable.setSelectionBackground(TABLE_SELECTED_COLOR);
        carTable.setSelectionForeground(TEXT_COLOR);
        
        // Başlık stili
        JTableHeader header = carTable.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        
        // Hücre stili
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        for (int i = 0; i < carTable.getColumnCount(); i++) {
            carTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Filtre başlığı
        JLabel filterTitle = new JLabel("Filtrele");
        filterTitle.setFont(new Font("SF Pro Display", Font.BOLD, 16));
        filterTitle.setForeground(TEXT_COLOR);
        
        // Filtre alanları için panel
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        fieldsPanel.setOpaque(false);
        
        fieldsPanel.add(filterTitle);
        
        // Marka filtresi
        fieldsPanel.add(createStyledLabel("Marka:"));
        brandField = createStyledTextField(10);
        fieldsPanel.add(brandField);
        
        // Model filtresi
        fieldsPanel.add(createStyledLabel("Model:"));
        modelField = createStyledTextField(10);
        fieldsPanel.add(modelField);
        
        // Yıl filtresi
        fieldsPanel.add(createStyledLabel("Yıl:"));
        yearField = createStyledTextField(10);
        fieldsPanel.add(yearField);
        
        // Paket filtresi
        fieldsPanel.add(createStyledLabel("Paket:"));
        packageField = createStyledTextField(10);
        fieldsPanel.add(packageField);
        
        // Durum filtresi
        fieldsPanel.add(createStyledLabel("Durum:"));
        statusCombo = createStyledComboBox(new String[]{"Tümü", "Stokta", "Satıldı", "Gösterimde", "Test Sürüşünde"});
        statusCombo.setPreferredSize(new Dimension(150, 35));
        fieldsPanel.add(statusCombo);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Butonlar için ayrı panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton filterButton = createStyledButton("Filtrele", PRIMARY_COLOR);
        filterButton.setPreferredSize(new Dimension(120, 35));
        filterButton.addActionListener(e -> loadCars());
        buttonPanel.add(filterButton);
        
        JButton clearButton = createStyledButton("Temizle", null);
        clearButton.setPreferredSize(new Dimension(120, 35));
        clearButton.addActionListener(e -> clearFilters());
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton addCarButton = createStyledButton("Araç Ekle", PRIMARY_COLOR);
        addCarButton.setPreferredSize(new Dimension(150, 40));
        addCarButton.addActionListener(e -> addCar());
        panel.add(addCarButton);
        
        JButton showCarButton = createStyledButton("Gösterime Al", PRIMARY_COLOR);
        showCarButton.setPreferredSize(new Dimension(150, 40));
        showCarButton.addActionListener(e -> showCar());
        panel.add(showCarButton);
        
        JButton testDriveButton = createStyledButton("Test Sürüşüne Çıkar", PRIMARY_COLOR);
        testDriveButton.setPreferredSize(new Dimension(150, 40));
        testDriveButton.addActionListener(e -> assignTestDrive());
        panel.add(testDriveButton);
        
        JButton returnToStockButton = createStyledButton("Stoğa Al", PRIMARY_COLOR);
        returnToStockButton.setPreferredSize(new Dimension(150, 40));
        returnToStockButton.addActionListener(e -> returnToStock());
        panel.add(returnToStockButton);
        
        JButton sellCarButton = createStyledButton("Araç Sat", PRIMARY_COLOR);
        sellCarButton.setPreferredSize(new Dimension(150, 40));
        sellCarButton.addActionListener(e -> sellCar());
        panel.add(sellCarButton);
        
        JButton notificationsButton = createStyledButton("Bildirimler", PRIMARY_COLOR);
        notificationsButton.setPreferredSize(new Dimension(150, 40));
        notificationsButton.addActionListener(e -> showNotifications());
        panel.add(notificationsButton);
        
        JButton reportsButton = createStyledButton("Raporlar", PRIMARY_COLOR);
        reportsButton.setPreferredSize(new Dimension(150, 40));
        reportsButton.addActionListener(e -> showReports());
        panel.add(reportsButton);

        JButton logoutButton = createStyledButton("Çıkış Yap", DANGER_COLOR);
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.addActionListener(e -> logout());
        panel.add(logoutButton);
        
        return panel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(150, 35));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        return comboBox;
    }
    
    private JButton createStyledButton(String text, Color primaryColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(getBackground().brighter());
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(0, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (primaryColor != null) {
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(51, 51, 51));
            button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        }
        
        return button;
    }
    
    private void loadDealerId() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT dealer_id FROM admins WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                dealerId = rs.getInt("dealer_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bayi bilgisi yüklenirken hata oluştu!");
        }
    }
    
    private void clearFilters() {
        brandField.setText("");
        modelField.setText("");
        yearField.setText("");
        packageField.setText("");
        statusCombo.setSelectedItem("Tümü");
        loadCars();
    }
    
    private void loadCars() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT c.*, d.name as dealer_name FROM cars c " +
                "JOIN dealers d ON c.dealer = d.id WHERE 1=1");
            
            List<Object> params = new ArrayList<>();
            
            if (dealerId != null) {
                query.append(" AND c.dealer = ?");
                params.add(dealerId);
            }
            
            String brand = brandField.getText().trim();
            if (!brand.isEmpty()) {
                query.append(" AND LOWER(c.brand) LIKE LOWER(?)");
                params.add("%" + brand + "%");
            }
            
            String model = modelField.getText().trim();
            if (!model.isEmpty()) {
                query.append(" AND LOWER(c.model) LIKE LOWER(?)");
                params.add("%" + model + "%");
            }
            
            String year = yearField.getText().trim();
            if (!year.isEmpty()) {
                try {
                    int yearValue = Integer.parseInt(year);
                    query.append(" AND c.year = ?");
                    params.add(yearValue);
                } catch (NumberFormatException ex) {
                    
                }
            }
            
            String packageType = packageField.getText().trim();
            if (!packageType.isEmpty()) {
                query.append(" AND LOWER(c.package_type) LIKE LOWER(?)");
                params.add("%" + packageType + "%");
            }
            
            if (!statusCombo.getSelectedItem().equals("Tümü")) {
                query.append(" AND c.status = ?");
                params.add(statusCombo.getSelectedItem());
            }
            
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            // Tabloyu temizle
            tableModel.setRowCount(0);
            
            // Sonuçları tabloya ekle
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("package_type"),
                    rs.getDouble("price"),
                    rs.getString("dealer_name"),
                    rs.getString("status")
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Araçlar yüklenirken hata oluştu!");
        }
    }
    
    private void addCar() {
        AddCarDialog dialog = new AddCarDialog(this, dealerId);
        dialog.setVisible(true);
        loadCars(); // Tabloyu yenile
        updateCharts(); // Grafikleri güncelle
    }
    
    private void showCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir araç seçin!");
            return;
        }
        
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if (!currentStatus.equals("Stokta") && !currentStatus.equals("Test Sürüşünde")) {
            JOptionPane.showMessageDialog(this, "Sadece stokta veya test sürüşünde olan araçlar gösterime alınabilir!");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE cars SET status = 'Gösterimde' WHERE id = ? AND dealer = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, carId);
            pstmt.setInt(2, dealerId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Araç gösterime alındı!");
                loadCars(); // Tabloyu yenile
                updateCharts(); // Grafikleri güncelle
            } else {
                JOptionPane.showMessageDialog(this, "Araç gösterime alınamadı!");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "İşlem sırasında hata oluştu!");
        }
    }
    
    private void assignTestDrive() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir araç seçin!");
            return;
        }
        
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if (!currentStatus.equals("Stokta") && !currentStatus.equals("Gösterimde")) {
            JOptionPane.showMessageDialog(this, "Sadece stokta veya gösterimde olan araçlar test sürüşüne çıkarılabilir!");
            return;
        }
        
        // Müşteri seçme ekranını aç
        SelectCustomerDialog dialog = new SelectCustomerDialog(this);
        dialog.setVisible(true);
        
        String selectedUsername = dialog.getSelectedUsername();
        if (selectedUsername == null) {
            return; // Kullanıcı iptal etti
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Araç durumunu güncelle
                String updateCarQuery = "UPDATE cars SET status = 'Test Sürüşünde' WHERE id = ? AND dealer = ?";
                PreparedStatement updateCarStmt = conn.prepareStatement(updateCarQuery);
                updateCarStmt.setInt(1, carId);
                updateCarStmt.setInt(2, dealerId);
                updateCarStmt.executeUpdate();
                
                // Test sürüşü kaydı oluştur
                String insertTestDriveQuery = "INSERT INTO test_drives (car_id, customer_id, dealer_id, start_date, status) " +
                                           "VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'Devam Ediyor')";
                PreparedStatement insertTestDriveStmt = conn.prepareStatement(insertTestDriveQuery);
                insertTestDriveStmt.setInt(1, carId);
                insertTestDriveStmt.setString(2, selectedUsername);
                insertTestDriveStmt.setInt(3, dealerId);
                insertTestDriveStmt.executeUpdate();
                
                // Transaction'ı onayla
                conn.commit();
                
                JOptionPane.showMessageDialog(this, "Araç test sürüşüne çıkarıldı!");
                loadCars(); // Tabloyu yenile
                updateCharts(); // Grafikleri güncelle
                
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                throw ex;
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "İşlem sırasında hata oluştu!");
        }
    }
    
    private void returnToStock() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir araç seçin!");
            return;
        }
        
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if (!currentStatus.equals("Gösterimde") && !currentStatus.equals("Test Sürüşünde")) {
            JOptionPane.showMessageDialog(this, "Sadece gösterimde veya test sürüşünde olan araçlar stoğa alınabilir!");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Araç durumunu güncelle
                String updateCarQuery = "UPDATE cars SET status = 'Stokta' WHERE id = ? AND dealer = ?";
                PreparedStatement updateCarStmt = conn.prepareStatement(updateCarQuery);
                updateCarStmt.setInt(1, carId);
                updateCarStmt.setInt(2, dealerId);
                updateCarStmt.executeUpdate();

                // Transaction'ı onayla
                conn.commit();
                
                JOptionPane.showMessageDialog(this, "Araç stoğa alındı!");
                loadCars(); // Tabloyu yenile
                updateCharts(); // Grafikleri güncelle
                
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                throw ex;
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "İşlem sırasında hata oluştu!");
        }
    }
    
    private void sellCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir araç seçin!");
            return;
        }
        
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        double price = (double) tableModel.getValueAt(selectedRow, 5);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if (currentStatus.equals("Satıldı")) {
            JOptionPane.showMessageDialog(this, "Bu araç daha önce satılmış!");
            return;
        }
        
        // Müşteri seçme ekranını aç
        SelectCustomerDialog customerDialog = new SelectCustomerDialog(this);
        customerDialog.setVisible(true);
        
        String selectedUsername = customerDialog.getSelectedUsername();
        if (selectedUsername == null) {
            return; // Kullanıcı iptal etti
        }
        
        
        JDialog dialog = new JDialog(this, "Satış Tarihi ve Saati", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        // Ana panel - gradient arka plan
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_COLOR, 0, getHeight(), new Color(240, 242, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Başlık
        JLabel titleLabel = new JLabel("Satış Tarihi ve Saati Seçin");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form alanları
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Tarih seçici
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel dateLabel = createStyledLabel("Satış Tarihi:");
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        dateSpinner.setPreferredSize(new Dimension(200, 35));
        formPanel.add(dateSpinner, gbc);
        
        // Saat seçici
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel hourLabel = createStyledLabel("Saat:");
        formPanel.add(hourLabel, gbc);
        
        gbc.gridx = 1;
        SpinnerNumberModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        hourSpinner.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        hourSpinner.setPreferredSize(new Dimension(200, 35));
        formPanel.add(hourSpinner, gbc);
        
        // Dakika seçici
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel minuteLabel = createStyledLabel("Dakika:");
        formPanel.add(minuteLabel, gbc);
        
        gbc.gridx = 1;
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner minuteSpinner = new JSpinner(minuteModel);
        minuteSpinner.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        minuteSpinner.setPreferredSize(new Dimension(200, 35));
        formPanel.add(minuteSpinner, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        buttonPanel.add(Box.createHorizontalGlue());
        
        JButton cancelButton = new JButton("İptal");
        cancelButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setMinimumSize(new Dimension(120, 35));
        cancelButton.setMaximumSize(new Dimension(120, 35));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = new JButton("Kaydet");
        saveButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setMinimumSize(new Dimension(120, 35));
        saveButton.setMaximumSize(new Dimension(120, 35));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        saveButton.addActionListener(e -> {
        // Seçilen tarih ve saati birleştir
        java.util.Date saleDate = (java.util.Date) dateSpinner.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(saleDate);
        calendar.set(Calendar.HOUR_OF_DAY, (Integer) hourSpinner.getValue());
        calendar.set(Calendar.MINUTE, (Integer) minuteSpinner.getValue());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        java.sql.Timestamp saleTimestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Araç durumunu güncelle
                String updateCarQuery = "UPDATE cars SET status = 'Satıldı' WHERE id = ? AND dealer = ?";
                PreparedStatement updateCarStmt = conn.prepareStatement(updateCarQuery);
                updateCarStmt.setInt(1, carId);
                updateCarStmt.setInt(2, dealerId);
                updateCarStmt.executeUpdate();
                
                // Satış raporuna ekle
                String insertSalesQuery = "INSERT INTO sales_reports (car_id, customer_id, dealer_id, sale_date, sale_price) " +
                                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertSalesStmt = conn.prepareStatement(insertSalesQuery);
                insertSalesStmt.setInt(1, carId);
                insertSalesStmt.setString(2, selectedUsername);
                insertSalesStmt.setInt(3, dealerId);
                insertSalesStmt.setTimestamp(4, saleTimestamp);
                insertSalesStmt.setDouble(5, price);
                insertSalesStmt.executeUpdate();
                
                // Transaction'ı onayla
                conn.commit();
                
                    dialog.dispose();
                    JOptionPane.showMessageDialog(AdminMainFrame.this, "Araç başarıyla satıldı!");
                loadCars(); // Tabloyu yenile
                updateCharts(); // Grafikleri güncelle
                
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                throw ex;
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
                JOptionPane.showMessageDialog(AdminMainFrame.this, 
                    "Veritabanı hatası: " + ex.getMessage() + "\nHata kodu: " + ex.getErrorCode(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showNotifications() {
        NotificationsDialog dialog = new NotificationsDialog(this, dealerId);
        dialog.setOnNotificationProcessed(() -> {
            loadCars(); // Araç listesini yenile
            updateCharts(); // Grafiği güncelle
        });
        dialog.setVisible(true);
    }
    
    private void showReports() {
        ReportsDialog dialog = new ReportsDialog(this, dealerId);
        dialog.setVisible(true);
    }

    private void logout() {
        int choice = JOptionPane.showOptionDialog(
            this,
            "Çıkış yapmak istediğinizden emin misiniz?",
            "Çıkış Onayı",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[]{"Evet", "Hayır"},
            "Hayır"
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose(); // Mevcut pencereyi kapat
            new LoginFrame().setVisible(true); // Giriş ekranını aç
        }
    }

    private JPanel createChartPanel() {
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setPreferredSize(new Dimension(300, 0));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateCharts();
        return chartPanel;
    }

    private void updateCharts() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Araç durumlarının dağılımı için pasta grafik
            String statusQuery = "SELECT status, COUNT(*) as count FROM cars WHERE dealer = ? GROUP BY status";
            PreparedStatement statusStmt = conn.prepareStatement(statusQuery);
            statusStmt.setInt(1, dealerId);
            ResultSet statusRs = statusStmt.executeQuery();

            DefaultPieDataset statusDataset = new DefaultPieDataset();
            while (statusRs.next()) {
                statusDataset.setValue(statusRs.getString("status"), statusRs.getInt("count"));
            }

            JFreeChart statusChart = ChartFactory.createPieChart(
                "Araç Durumları",
                statusDataset,
                true,
                true,
                false
            );

            // Son 6 ayın satış trendi için çizgi grafik
            String salesQuery = "SELECT DATE_TRUNC('month', sale_date) as month, COUNT(*) as sales " +
                              "FROM sales_reports " +
                              "WHERE dealer_id = ? " +
                              "AND sale_date >= CURRENT_DATE - INTERVAL '6 months' " +
                              "GROUP BY DATE_TRUNC('month', sale_date) " +
                              "ORDER BY month";
            
            PreparedStatement salesStmt = conn.prepareStatement(salesQuery);
            salesStmt.setInt(1, dealerId);
            ResultSet salesRs = salesStmt.executeQuery();

            DefaultCategoryDataset salesDataset = new DefaultCategoryDataset();
            while (salesRs.next()) {
                java.sql.Timestamp month = salesRs.getTimestamp("month");
                String monthStr = String.format("%d/%d", month.getMonth() + 1, month.getYear() + 1900);
                salesDataset.addValue(salesRs.getInt("sales"), "Satışlar", monthStr);
            }

            JFreeChart salesChart = ChartFactory.createLineChart(
                "Aylık Satış Trendi",
                "Ay",
                "Satış Adedi",
                salesDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );

            // Grafikleri panele ekle
            JPanel chartsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            chartsPanel.add(new ChartPanel(statusChart));
            chartsPanel.add(new ChartPanel(salesChart));
            
            chartPanel.removeAll();
            chartPanel.add(chartsPanel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Grafik oluşturulurken hata oluştu!");
        }
    }
} 