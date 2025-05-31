package com.carsales.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.carsales.database.DatabaseConnection;

public class SelectCustomerDialog extends JDialog {
    private JTable customerTable;
    private JTextField searchField;
    private DefaultTableModel tableModel;
    private String selectedUsername;
    
    // Modern renk paleti
    private final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private final Color SECONDARY_COLOR = new Color(64, 156, 255);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color TEXT_COLOR = new Color(33, 37, 41);
    private final Color BORDER_COLOR = new Color(206, 212, 218);
    private final Color TABLE_HEADER_COLOR = new Color(240, 242, 245);
    private final Color TABLE_SELECTED_COLOR = new Color(232, 240, 255);
    
    public SelectCustomerDialog(Frame parent) {
        super(parent, "Müşteri Seç", true);
        
        setSize(800, 500);
        setLocationRelativeTo(parent);
        
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
        
        // Başlık
        JLabel titleLabel = new JLabel("Müşteri Seç");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Arama paneli
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = createStyledLabel("Müşteri Ara:");
        searchPanel.add(searchLabel);
        
        searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchPanel.add(searchField);
        
        JButton searchButton = createStyledButton("Ara", PRIMARY_COLOR);
        searchButton.addActionListener(e -> loadCustomers());
        searchPanel.add(searchButton);
        
        JButton clearButton = createStyledButton("Temizle", null);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadCustomers();
        });
        searchPanel.add(clearButton);
        
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Müşteri tablosu
        String[] columns = {"Kullanıcı Adı", "Ad Soyad", "Yaş", "Telefon"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        customerTable.setRowHeight(35);
        customerTable.setShowGrid(true);
        customerTable.setGridColor(BORDER_COLOR);
        customerTable.setSelectionBackground(TABLE_SELECTED_COLOR);
        customerTable.setSelectionForeground(TEXT_COLOR);
        
        // Başlık stili
        JTableHeader header = customerTable.getTableHeader();
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
        
        for (int i = 0; i < customerTable.getColumnCount(); i++) {
            customerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Çift tıklama olayı
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectCustomer();
                }
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(customerTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        buttonPanel.add(Box.createHorizontalGlue());
        
        JButton cancelButton = createStyledButton("İptal", null);
        cancelButton.addActionListener(e -> {
            selectedUsername = null;
            dispose();
        });
        
        JButton selectButton = createStyledButton("Seç", PRIMARY_COLOR);
        selectButton.addActionListener(e -> selectCustomer());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(selectButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        loadCustomers();
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
    
    private void loadCustomers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT username, name, age, phone FROM customers WHERE 1=1");
            
            List<Object> params = new ArrayList<>();
            
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                query.append(" AND (LOWER(username) LIKE LOWER(?) OR " +
                           "LOWER(name) LIKE LOWER(?))");
                String searchPattern = "%" + searchText + "%";
                params.add(searchPattern);
                params.add(searchPattern);
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
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("phone")
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Müşteriler yüklenirken hata oluştu!");
        }
    }
    
    private void selectCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir müşteri seçin!");
            return;
        }
        
        selectedUsername = (String) tableModel.getValueAt(selectedRow, 0);
        dispose();
    }
    
    public String getSelectedUsername() {
        return selectedUsername;
    }
} 