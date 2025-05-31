package com.carsales.ui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import com.carsales.database.DatabaseConnection;

public class CustomerMainFrame extends JFrame {
    private String username;
    private JTable carTable;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField packageField;
    private JTextField dealerField;
    private JComboBox<String> dealerCombo;
    private DefaultTableModel tableModel;
    
   
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color SECONDARY_COLOR = new Color(64, 156, 255);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    private final Color TABLE_HEADER_COLOR = new Color(240, 242, 245);
    private final Color TABLE_SELECTED_COLOR = new Color(232, 240, 255);
    private final Color DANGER_COLOR = new Color(220, 53, 69);
    
    public CustomerMainFrame(String username) {
        try {
            
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            
            UIManager.put("Button.background", Color.WHITE);
            UIManager.put("Button.opaque", true);
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 10, 5, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.username = username;
        setTitle("Araç Satış Sistemi - Müşteri Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        
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
        
        add(mainPanel);
        
        // İlk yükleme
        loadDealers();
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
        
        // Bayi filtresi
        fieldsPanel.add(createStyledLabel("Bayi:"));
        JPanel dealerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dealerPanel.setOpaque(false);
        
        dealerCombo = new JComboBox<>();
        dealerCombo.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        dealerCombo.setPreferredSize(new Dimension(180, 32));
        dealerCombo.setBackground(new Color(209, 215, 221));
        dealerCombo.setForeground(TEXT_COLOR);
        dealerPanel.add(dealerCombo);
        fieldsPanel.add(dealerPanel);
        
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
        
        JButton priceOfferButton = createStyledButton("Fiyat Teklifi Ver", PRIMARY_COLOR);
        priceOfferButton.setPreferredSize(new Dimension(150, 40));
        priceOfferButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        priceOfferButton.addActionListener(e -> createPriceOffer());
        panel.add(priceOfferButton);
        
        JButton testDriveButton = createStyledButton("Test Sürüşü İste", PRIMARY_COLOR);
        testDriveButton.setPreferredSize(new Dimension(150, 40));
        testDriveButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        testDriveButton.addActionListener(e -> requestTestDrive());
        panel.add(testDriveButton);
        
        JButton orderButton = createStyledButton("Sipariş Ver", PRIMARY_COLOR);
        orderButton.setPreferredSize(new Dimension(150, 40));
        orderButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        orderButton.addActionListener(e -> createOrder());
        panel.add(orderButton);

        JButton logoutButton = createStyledButton("Çıkış Yap", DANGER_COLOR);
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
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
        
        
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });
        
        // Açılır menü özelleştirmesi
        Object comp = comboBox.getUI().getAccessibleChild(comboBox, 0);
        if (comp instanceof JPopupMenu) {
            JPopupMenu popup = (JPopupMenu) comp;
            popup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        }
        
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
    
    private void clearFilters() {
        brandField.setText("");
        modelField.setText("");
        yearField.setText("");
        packageField.setText("");
        dealerCombo.setSelectedItem("Tümü");
        loadCars();
    }
    
    private void loadDealers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT DISTINCT name FROM dealers ORDER BY name";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            dealerCombo.removeAllItems();
            dealerCombo.addItem("Tümü");
            
            while (rs.next()) {
                dealerCombo.addItem(rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bayiler yüklenirken hata oluştu!");
        }
    }
    
    private void loadCars() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT c.*, d.name as dealer_name FROM cars c " +
                "JOIN dealers d ON c.dealer = d.id WHERE (c.status = 'Stokta' OR c.status = 'Gösterimde')");
            
            List<Object> params = new ArrayList<>();
            
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
                    // Yıl sayısal değilse, bu filtreyi atla
                }
            }
            
            String packageType = packageField.getText().trim();
            if (!packageType.isEmpty()) {
                query.append(" AND LOWER(c.package_type) LIKE LOWER(?)");
                params.add("%" + packageType + "%");
            }
            
            String selectedDealer = (String) dealerCombo.getSelectedItem();
            if (selectedDealer != null && !selectedDealer.equals("Tümü")) {
                query.append(" AND LOWER(d.name) = LOWER(?)");
                params.add(selectedDealer);
            }
            
            query.append(" ORDER BY c.id DESC");
            
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
    
    private void createPriceOffer() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir araç seçin!");
            return;
        }
        
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        double currentPrice = (double) tableModel.getValueAt(selectedRow, 5);
        
        // Önceki teklif kontrolü
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT status FROM price_offers WHERE car_id = ? AND customer_id = ? AND status = 'Bekliyor'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, carId);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Bu araç için zaten bekleyen bir teklifiniz var!");
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kontrol sırasında hata oluştu!");
            return;
        }
        
        
        JDialog dialog = new JDialog(this, "Fiyat Teklifi Ver", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        // Gradient arka plan için özel panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 245, 250),
                        0, getHeight(), new Color(230, 235, 240));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Başlık
        JLabel titleLabel = new JLabel("Fiyat Teklifi Oluştur");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Tarih seçici
        JLabel dateLabel = new JLabel("Teklif Tarihi");
        dateLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(dateLabel, gbc);
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        styleSpinner(dateSpinner);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(dateSpinner, gbc);
        
        // Saat seçici
        JLabel timeLabel = new JLabel("Saat");
        timeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(timeLabel, gbc);
        
        JPanel timePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        timePanel.setOpaque(false);
        
        SpinnerNumberModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        styleSpinner(hourSpinner);
        
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner minuteSpinner = new JSpinner(minuteModel);
        styleSpinner(minuteSpinner);
        
        timePanel.add(hourSpinner);
        timePanel.add(minuteSpinner);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(timePanel, gbc);
        
        // Fiyat girişi
        JLabel priceLabel = new JLabel("Teklif Fiyatı (TL)");
        priceLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        priceLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(priceLabel, gbc);
        
        JTextField priceField = new JTextField();
        styleTextField(priceField);
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(priceField, gbc);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = createStyledButton("İptal", null);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton submitButton = createStyledButton("Teklif Ver", PRIMARY_COLOR);
        submitButton.addActionListener(e -> {
            try {
                double offerPrice = Double.parseDouble(priceField.getText().trim());
                
                // Seçilen tarih ve saati birleştir
                java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
                int hour = (Integer) hourSpinner.getValue();
                int minute = (Integer) minuteSpinner.getValue();
                
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedDate);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                
                java.sql.Timestamp offerDateTime = new java.sql.Timestamp(calendar.getTimeInMillis());
                
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "INSERT INTO price_offers (car_id, customer_id, dealer_id, offer_price, status, offer_date) " +
                                 "SELECT ?, ?, dealer, ?, 'Bekliyor', ? FROM cars WHERE id = ?";
                    
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, carId);
                    pstmt.setString(2, username);
                    pstmt.setDouble(3, offerPrice);
                    pstmt.setTimestamp(4, offerDateTime);
                    pstmt.setInt(5, carId);
                    
                    pstmt.executeUpdate();
                    
                    dialog.dispose();
                    JOptionPane.showMessageDialog(CustomerMainFrame.this, "Fiyat teklifi başarıyla gönderildi!");
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(CustomerMainFrame.this, "Fiyat teklifi gönderilirken hata oluştu!");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CustomerMainFrame.this, "Lütfen geçerli bir fiyat girin!");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(0, 40));
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            textField.setForeground(new Color(51, 51, 51));
            
            spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
    }
    
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        textField.setForeground(new Color(51, 51, 51));
        textField.setPreferredSize(new Dimension(0, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    private void requestTestDrive() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir araç seçin!");
            return;
        }
        
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if (!currentStatus.equals("Gösterimde") && !currentStatus.equals("Stokta")) {
            JOptionPane.showMessageDialog(this, "Sadece gösterimde veya stokta olan araçlar için test sürüşü talep edilebilir!");
            return;
        }
        
        // Önceki test sürüşü talebi kontrolü
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT status FROM test_drive_requests WHERE car_id = ? AND customer_id = ? AND status = 'Bekliyor'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, carId);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Bu araç için zaten bekleyen bir test sürüşü talebiniz var!");
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kontrol sırasında hata oluştu!");
            return;
        }
        
        
        JDialog dialog = new JDialog(this, "Test Sürüşü İste", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        // Gradient arka plan için özel panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 245, 250),
                        0, getHeight(), new Color(230, 235, 240));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Başlık
        JLabel titleLabel = new JLabel("Test Sürüşü Talebi Oluştur");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Tarih seçici
        JLabel dateLabel = new JLabel("Test Sürüşü Tarihi");
        dateLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(dateLabel, gbc);
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        styleSpinner(dateSpinner);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(dateSpinner, gbc);
        
        // Saat seçici
        JLabel timeLabel = new JLabel("Saat");
        timeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(timeLabel, gbc);
        
        JPanel timePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        timePanel.setOpaque(false);
        
        SpinnerNumberModel hourModel = new SpinnerNumberModel(9, 9, 17, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        styleSpinner(hourSpinner);
        
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 30);
        JSpinner minuteSpinner = new JSpinner(minuteModel);
        styleSpinner(minuteSpinner);
        
        timePanel.add(hourSpinner);
        timePanel.add(minuteSpinner);
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(timePanel, gbc);
        
        // Not: Çalışma saatleri bilgisi
        JLabel noteLabel = new JLabel("Not: Test sürüşleri 09:00 - 17:00 saatleri arasında yapılmaktadır.");
        noteLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        noteLabel.setForeground(new Color(128, 128, 128));
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(noteLabel, gbc);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = createStyledButton("İptal", null);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton submitButton = createStyledButton("Talep Gönder", PRIMARY_COLOR);
        submitButton.addActionListener(e -> {
        // Seçilen tarih ve saati birleştir
        java.util.Date testDriveDate = (java.util.Date) dateSpinner.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testDriveDate);
        calendar.set(Calendar.HOUR_OF_DAY, (Integer) hourSpinner.getValue());
        calendar.set(Calendar.MINUTE, (Integer) minuteSpinner.getValue());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        java.sql.Timestamp testDriveTimestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO test_drive_requests (car_id, customer_id, dealer_id, request_date, status) " +
                          "SELECT ?, ?, dealer, ?, 'Bekliyor' FROM cars WHERE id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, carId);
            pstmt.setString(2, username);
            pstmt.setTimestamp(3, testDriveTimestamp);
            pstmt.setInt(4, carId);
            
                int result = pstmt.executeUpdate();
            
                if (result > 0) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(CustomerMainFrame.this, "Test sürüşü talebi başarıyla gönderildi!");
            } else {
                    JOptionPane.showMessageDialog(CustomerMainFrame.this, "Test sürüşü talebi gönderilemedi!");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
                JOptionPane.showMessageDialog(CustomerMainFrame.this, "İşlem sırasında hata oluştu!");
        }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void createOrder() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir araç seçin!");
            return;
        }
        
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7);
        
        if (!currentStatus.equals("Gösterimde") && !currentStatus.equals("Stokta")) {
            JOptionPane.showMessageDialog(this, "Sadece gösterimde veya stokta olan araçlar sipariş edilebilir!");
            return;
        }
        
        // Önceki sipariş kontrolü
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT status FROM orders WHERE car_id = ? AND customer_id = ? AND status = 'Bekliyor'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, carId);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Bu araç için zaten bekleyen bir siparişiniz var!");
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kontrol sırasında hata oluştu!");
            return;
        }
        
        
        JDialog dialog = new JDialog(this, "Sipariş Ver", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 245, 250),
                        0, getHeight(), new Color(230, 235, 240));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Başlık
        JLabel titleLabel = new JLabel("Sipariş Oluştur");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Tarih seçici
        JLabel dateLabel = new JLabel("Sipariş Tarihi");
        dateLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(dateLabel, gbc);
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        styleSpinner(dateSpinner);
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(dateSpinner, gbc);
        
        // Saat seçici
        JLabel timeLabel = new JLabel("Saat");
        timeLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(51, 51, 51));
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(timeLabel, gbc);
        
        JPanel timePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        timePanel.setOpaque(false);
        
        SpinnerNumberModel hourModel = new SpinnerNumberModel(9, 9, 17, 1);
        JSpinner hourSpinner = new JSpinner(hourModel);
        styleSpinner(hourSpinner);
        
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 30);
        JSpinner minuteSpinner = new JSpinner(minuteModel);
        styleSpinner(minuteSpinner);
        
        timePanel.add(hourSpinner);
        timePanel.add(minuteSpinner);
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(timePanel, gbc);
        
        // Not: Çalışma saatleri bilgisi
        JLabel noteLabel = new JLabel("Not: Siparişler 09:00 - 17:00 saatleri arasında işleme alınmaktadır.");
        noteLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 12));
        noteLabel.setForeground(new Color(128, 128, 128));
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(noteLabel, gbc);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = createStyledButton("İptal", null);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton submitButton = createStyledButton("Sipariş Ver", PRIMARY_COLOR);
        submitButton.addActionListener(e -> {
        // Seçilen tarih ve saati birleştir
        java.util.Date orderDate = (java.util.Date) dateSpinner.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(orderDate);
        calendar.set(Calendar.HOUR_OF_DAY, (Integer) hourSpinner.getValue());
        calendar.set(Calendar.MINUTE, (Integer) minuteSpinner.getValue());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        java.sql.Timestamp orderTimestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO orders (car_id, customer_id, dealer_id, order_date, status) " +
                          "SELECT ?, ?, dealer, ?, 'Bekliyor' FROM cars WHERE id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, carId);
            pstmt.setString(2, username);
            pstmt.setTimestamp(3, orderTimestamp);
            pstmt.setInt(4, carId);
            
                int result = pstmt.executeUpdate();
            
                if (result > 0) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(CustomerMainFrame.this, "Sipariş başarıyla oluşturuldu!");
            } else {
                    JOptionPane.showMessageDialog(CustomerMainFrame.this, "Sipariş oluşturulamadı!");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
                JOptionPane.showMessageDialog(CustomerMainFrame.this, "İşlem sırasında hata oluştu!");
        }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        dialog.add(mainPanel);
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
} 