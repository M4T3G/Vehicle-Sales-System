package com.carsales.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.carsales.database.DatabaseConnection;

public class AddCarDialog extends JDialog {
    private JTextField brandField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField packageField;
    private JTextField priceField;
    private Integer dealerId;
    
   
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color SECONDARY_COLOR = new Color(64, 156, 255);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    
    public AddCarDialog(Frame parent, Integer dealerId) {
        super(parent, "Yeni Araç Ekle", true);
        this.dealerId = dealerId;
        
        setSize(400, 450);
        setLocationRelativeTo(parent);
        
        
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
       
        JLabel titleLabel = new JLabel("Yeni Araç Ekle");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Form alanları
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Marka
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel brandLabel = createStyledLabel("Marka:");
        mainPanel.add(brandLabel, gbc);
        
        gbc.gridx = 1;
        brandField = createStyledTextField();
        mainPanel.add(brandField, gbc);
        
        // Model
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel modelLabel = createStyledLabel("Model:");
        mainPanel.add(modelLabel, gbc);
        
        gbc.gridx = 1;
        modelField = createStyledTextField();
        mainPanel.add(modelField, gbc);
        
        // Yıl
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel yearLabel = createStyledLabel("Yıl:");
        mainPanel.add(yearLabel, gbc);
        
        gbc.gridx = 1;
        yearField = createStyledTextField();
        mainPanel.add(yearField, gbc);
        
        // Paket
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel packageLabel = createStyledLabel("Paket:");
        mainPanel.add(packageLabel, gbc);
        
        gbc.gridx = 1;
        packageField = createStyledTextField();
        mainPanel.add(packageField, gbc);
        
        // Fiyat
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel priceLabel = createStyledLabel("Fiyat:");
        mainPanel.add(priceLabel, gbc);
        
        gbc.gridx = 1;
        priceField = createStyledTextField();
        mainPanel.add(priceField, gbc);
        
        // Butonlar için panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        
        buttonPanel.add(Box.createHorizontalGlue());
        
        // İptal butonu
        JButton cancelButton = createStyledButton("İptal", null);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        // Butonlar arası boşluk
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        // Kaydet butonu
        JButton saveButton = createStyledButton("Kaydet", PRIMARY_COLOR);
        saveButton.addActionListener(e -> saveCar());
        buttonPanel.add(saveButton);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color primaryColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setMinimumSize(new Dimension(120, 35));
        button.setMaximumSize(new Dimension(120, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (primaryColor != null) {
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(primaryColor));
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(TEXT_COLOR);
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        }
        
        return button;
    }
    
    private void saveCar() {
        try {
            // Validasyon
            if (brandField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                packageField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
                return;
            }
            
            int year = Integer.parseInt(yearField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO cars (brand, model, year, package_type, price, dealer, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?, 'Stokta')";
                
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, brandField.getText().trim());
                pstmt.setString(2, modelField.getText().trim());
                pstmt.setInt(3, year);
                pstmt.setString(4, packageField.getText().trim());
                pstmt.setDouble(5, price);
                pstmt.setInt(6, dealerId);
                
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Araç başarıyla eklendi!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Araç eklenirken bir hata oluştu!");
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli sayısal değerler girin!");
        }
    }
} 