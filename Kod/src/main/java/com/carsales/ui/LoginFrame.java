package com.carsales.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import com.carsales.database.DatabaseConnection;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String selectedUserType = "Müşteri"; // Varsayılan seçim
    private JButton customerButton;
    private JButton adminButton;
    
    
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color SECONDARY_COLOR = new Color(64, 156, 255);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    
    public LoginFrame() {
        setTitle("Araç Satış Sistemi - Giriş");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        
        // Ana panel
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Başlık
        JLabel titleLabel = new JLabel("Araç Satış Sistemi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Alt başlık
        JLabel subtitleLabel = new JLabel("Hoş Geldiniz", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        mainPanel.add(subtitleLabel, gbc);
        
        // Kullanıcı tipi seçimi - modern butonlar
        JPanel userTypePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        userTypePanel.setOpaque(false);
        
        customerButton = createStyledButton("Müşteri", null);
        adminButton = createStyledButton("Admin", null);
        
        // Buton tıklama olayları
        customerButton.addActionListener(e -> {
            selectedUserType = "Müşteri";
            updateButtonStyles();
        });
        
        adminButton.addActionListener(e -> {
            selectedUserType = "Admin";
            updateButtonStyles();
        });
        
        userTypePanel.add(customerButton);
        userTypePanel.add(adminButton);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 30, 10);
        mainPanel.add(userTypePanel, gbc);
        
        // Input alanları
        usernameField = createStyledTextField("Kullanıcı Adı");
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 5, 10);
        mainPanel.add(usernameField, gbc);
        
        passwordField = createStyledPasswordField("Şifre");
        gbc.gridy = 4;
        mainPanel.add(passwordField, gbc);
        
        // Giriş butonu
        JButton loginButton = createStyledActionButton("Giriş Yap");
        loginButton.addActionListener(e -> login());
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(loginButton, gbc);
        
        // Kayıt butonu
        JButton registerButton = createStyledLinkButton("Müşteri Olarak Üye Ol");
        registerButton.addActionListener(e -> showRegisterDialog());
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 10, 10, 10);
        mainPanel.add(registerButton, gbc);
        
        // Varsayılan seçili buton stilini ayarla
        updateButtonStyles();
        
        add(mainPanel);
    }
    
    private JButton createStyledButton(String text, ImageIcon icon) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 45));
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        if (icon != null) {
            button.setIcon(icon);
            button.setIconTextGap(10);
        }
        return button;
    }
    
    private JButton createStyledActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 45));
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
        
        return button;
    }
    
    private JButton createStyledLinkButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 40));
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(SECONDARY_COLOR);
                button.setBackground(new Color(245, 247, 250));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(PRIMARY_COLOR);
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
        
        return button;
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(0, 40));
        textField.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        
        return textField;
    }
    
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(0, 40));
        passwordField.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Placeholder text
        passwordField.setEchoChar((char) 0);
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.GRAY);
        
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(TEXT_COLOR);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText(placeholder);
                }
            }
        });
        
        return passwordField;
    }
    
    private void updateButtonStyles() {
        // Seçili buton için stil
        customerButton.setBackground(selectedUserType.equals("Müşteri") ? PRIMARY_COLOR : Color.WHITE);
        customerButton.setForeground(selectedUserType.equals("Müşteri") ? Color.WHITE : TEXT_COLOR);
        adminButton.setBackground(selectedUserType.equals("Admin") ? PRIMARY_COLOR : Color.WHITE);
        adminButton.setForeground(selectedUserType.equals("Admin") ? Color.WHITE : TEXT_COLOR);
        
        // Özel kenar çizgisi
        customerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(selectedUserType.equals("Müşteri") ? PRIMARY_COLOR : BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        adminButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(selectedUserType.equals("Admin") ? PRIMARY_COLOR : BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }
    
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String table = selectedUserType.equals("Müşteri") ? "customers" : "admins";
            String query = "SELECT * FROM " + table + " WHERE username = ? AND password = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Giriş başarılı!");
                this.dispose();
                
                if (selectedUserType.equals("Müşteri")) {
                    new CustomerMainFrame(username).setVisible(true);
                } else {
                    new AdminMainFrame(username).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!", 
                    "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası oluştu!", 
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Müşteri Kaydı", true);
        dialog.setSize(450, 600);
        dialog.setLocationRelativeTo(this);
        
        
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Başlık
        JLabel titleLabel = new JLabel("Yeni Müşteri Kaydı", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Alt başlık
        JLabel subtitleLabel = new JLabel("Lütfen bilgilerinizi giriniz", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        mainPanel.add(subtitleLabel, gbc);
        
        // Form alanları
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Kullanıcı adı
        JLabel usernameLabel = createStyledLabel("Kullanıcı Adı");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);
        
        JTextField usernameField = createStyledTextField("");
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // Şifre
        JLabel passwordLabel = createStyledLabel("Şifre");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);
        
        JPasswordField passwordField = createStyledPasswordField("");
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // Ad Soyad
        JLabel nameLabel = createStyledLabel("Ad Soyad");
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(nameLabel, gbc);
        
        JTextField nameField = createStyledTextField("");
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);
        
        // Yaş
        JLabel ageLabel = createStyledLabel("Yaş");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(ageLabel, gbc);
        
        JTextField ageField = createStyledTextField("");
        gbc.gridx = 1;
        mainPanel.add(ageField, gbc);
        
        // Telefon
        JLabel phoneLabel = createStyledLabel("Telefon");
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(phoneLabel, gbc);
        
        JTextField phoneField = createStyledTextField("");
        gbc.gridx = 1;
        mainPanel.add(phoneField, gbc);

        // Tarih seçici
        JLabel dateLabel = createStyledLabel("Kayıt Tarihi");
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(dateLabel, gbc);
        
        JSpinner dateSpinner = createStyledSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        mainPanel.add(dateSpinner, gbc);

        // Saat seçici
        JLabel timeLabel = createStyledLabel("Saat");
        gbc.gridx = 0;
        gbc.gridy = 8;
        mainPanel.add(timeLabel, gbc);
        
        // Saat ve dakika için panel
        JPanel timePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        timePanel.setOpaque(false);
        
        SpinnerNumberModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        JSpinner hourSpinner = createStyledSpinner(hourModel);
        
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner minuteSpinner = createStyledSpinner(minuteModel);
        
        timePanel.add(hourSpinner);
        timePanel.add(minuteSpinner);
        
        gbc.gridx = 1;
        mainPanel.add(timePanel, gbc);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = createStyledLinkButton("İptal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton registerButton = createStyledActionButton("Kayıt Ol");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText().trim();
            String ageStr = ageField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
                showStyledErrorMessage(dialog, "Lütfen tüm alanları doldurun!");
                return;
            }
            
            try {
                int age = Integer.parseInt(ageStr);
                
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
                
                java.sql.Timestamp registrationDateTime = new java.sql.Timestamp(calendar.getTimeInMillis());
                
                registerCustomer(username, password, name, age, phone, registrationDateTime);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showStyledErrorMessage(dialog, "Yaş alanı sayısal bir değer olmalıdır!");
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel, gbc);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JSpinner createStyledSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(0, 40));
        
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            textField.setForeground(TEXT_COLOR);
            
            // Kenarlık stilini ayarla
            spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        }
        
        return spinner;
    }
    
    private void showStyledErrorMessage(JDialog parent, String message) {
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{},
            null);
        
        // Özel stil
        optionPane.setBackground(BACKGROUND_COLOR);
        optionPane.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        
        JDialog dialog = optionPane.createDialog(parent, "Hata");
        dialog.setBackground(BACKGROUND_COLOR);
        dialog.setVisible(true);
    }
    
    private void registerCustomer(String username, String password, String name, int age, String phone, java.sql.Timestamp registrationDateTime) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kullanıcı adı kontrolü
            String checkQuery = "SELECT COUNT(*) FROM customers WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Bu kullanıcı adı zaten kullanılıyor!", 
                    "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Yeni müşteri kaydı
            String query = "INSERT INTO customers (username, password, name, age, phone, registration_date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setInt(4, age);
            pstmt.setString(5, phone);
            pstmt.setTimestamp(6, registrationDateTime);
            
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Kayıt başarıyla tamamlandı! Giriş yapabilirsiniz.");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kayıt sırasında bir hata oluştu!", 
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
} 