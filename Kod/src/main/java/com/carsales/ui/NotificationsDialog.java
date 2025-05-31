package com.carsales.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.carsales.database.DatabaseConnection;

public class NotificationsDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private static final Color BACKGROUND_START = new Color(240, 245, 255);
    private static final Color BACKGROUND_END = new Color(255, 255, 255);
    private static final Font SF_PRO_DISPLAY = new Font("SF Pro Display", Font.PLAIN, 13);
    private static final Font SF_PRO_DISPLAY_BOLD = new Font("SF Pro Display", Font.BOLD, 13);
    
    private JTable notificationTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;
    private JComboBox<String> statusCombo;
    private Integer dealerId;
    private Runnable onNotificationProcessed;

    public NotificationsDialog(Frame parent, Integer dealerId) {
        super(parent, "Bildirimler", true);
        this.dealerId = dealerId;

        setSize(1000, 600);
        setLocationRelativeTo(parent);

        
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_START, 0, h, BACKGROUND_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Filtre paneli
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(false);

        // İşlem Tipi Combobox
        JLabel typeLabel = new JLabel("İşlem Tipi:");
        typeLabel.setFont(SF_PRO_DISPLAY);
        filterPanel.add(typeLabel);
        
        typeCombo = new JComboBox<>(new String[]{"Tümü", "Fiyat Teklifi", "Deneme Sürüşü", "Sipariş"});
        typeCombo.setFont(SF_PRO_DISPLAY);
        typeCombo.setPreferredSize(new Dimension(150, 30));
        typeCombo.addActionListener(e -> loadNotifications());
        filterPanel.add(typeCombo);

        // Durum Combobox
        JLabel statusLabel = new JLabel("Durum:");
        statusLabel.setFont(SF_PRO_DISPLAY);
        filterPanel.add(statusLabel);
        
        statusCombo = new JComboBox<>(new String[]{"Tümü", "Bekliyor", "Onaylandı", "Reddedildi"});
        statusCombo.setFont(SF_PRO_DISPLAY);
        statusCombo.setPreferredSize(new Dimension(150, 30));
        statusCombo.addActionListener(e -> loadNotifications());
        filterPanel.add(statusCombo);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Bildirim tablosu
        String[] columns = {"Müşteri", "Araç", "İşlem Tipi", "Durum", "Tarih", "Tablo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        notificationTable = new JTable(tableModel);
        notificationTable.setFont(SF_PRO_DISPLAY);
        notificationTable.setRowHeight(30);
        notificationTable.setShowGrid(false);
        notificationTable.setIntercellSpacing(new Dimension(0, 0));
        
        
        JTableHeader header = notificationTable.getTableHeader();
        header.setFont(SF_PRO_DISPLAY_BOLD);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        
        notificationTable.getColumnModel().getColumn(5).setMinWidth(0);
        notificationTable.getColumnModel().getColumn(5).setMaxWidth(0);
        notificationTable.getColumnModel().getColumn(5).setWidth(0);

        // Alternatif satır renkleri ve seçim yönetimi
        notificationTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(PRIMARY_COLOR);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                    c.setForeground(Color.BLACK);
                }
                
                
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                
                return c;
            }
        });

        // Seçim modelini özelleştir
        notificationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationTable.setSelectionBackground(PRIMARY_COLOR);
        notificationTable.setSelectionForeground(Color.WHITE);

        
        notificationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = notificationTable.getSelectedRow();
                    if (row != -1) {
                        String status = (String) tableModel.getValueAt(row, 3);
                        if (status.equals("Bekliyor")) {
                            int id = getNotificationId(row);
                            String sourceTable = (String) tableModel.getValueAt(row, 5);
                            showResponseDialog(id, sourceTable);
                        } else {
                            JOptionPane.showMessageDialog(NotificationsDialog.this,
                                    "Sadece bekleyen taleplere yanıt verebilirsiniz!",
                                    "Uyarı",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(notificationTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel);
        loadNotifications();
    }

    private void loadNotifications() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectedType = (String) typeCombo.getSelectedItem();
            String selectedStatus = (String) statusCombo.getSelectedItem();
            StringBuilder query = new StringBuilder();

            
            if (selectedType.equals("Tümü")) {
                query.append(
                        "SELECT c.name AS customer_name, CONCAT(car.brand,' ',car.model,' ',car.year) AS car_info, 'Fiyat Teklifi' AS transaction_type, po.status, po.offer_date AS transaction_time, 'price_offers' AS source_table " +
                                "FROM price_offers po JOIN customers c ON po.customer_id=c.username JOIN cars car ON po.car_id=car.id WHERE po.dealer_id=? " +
                                (!selectedStatus.equals("Tümü")?"AND po.status=?":""
                                ) +
                                " UNION ALL " +
                                "SELECT c.name, CONCAT(car.brand,' ',car.model,' ',car.year), 'Deneme Sürüşü', tdr.status, tdr.request_date, 'test_drive_requests' " +
                                "FROM test_drive_requests tdr JOIN customers c ON tdr.customer_id=c.username JOIN cars car ON tdr.car_id=car.id WHERE tdr.dealer_id=? " +
                                (!selectedStatus.equals("Tümü")?"AND tdr.status=?":""
                                ) +
                                " UNION ALL " +
                                "SELECT c.name, CONCAT(car.brand,' ',car.model,' ',car.year), 'Sipariş', o.status, o.order_date, 'orders' " +
                                "FROM orders o JOIN customers c ON o.customer_id=c.username JOIN cars car ON o.car_id=car.id WHERE o.dealer_id=? " +
                                (!selectedStatus.equals("Tümü")?"AND o.status=?":""
                                )
                );
            } else {
                // Tek bir tablo
                String tbl = selectedType.equals("Fiyat Teklifi")?"price_offers":(selectedType.equals("Deneme Sürüşü")?"test_drive_requests":"orders");
                String dateCol = selectedType.equals("Fiyat Teklifi")?"po.offer_date":(selectedType.equals("Deneme Sürüşü")?"tdr.request_date":"o.order_date");
                String alias = selectedType.equals("Fiyat Teklifi")?"po":(selectedType.equals("Deneme Sürüşü")?"tdr":"o");
                query.append(String.format(
                        "SELECT c.name, CONCAT(car.brand,' ',car.model,' ',car.year), '%s', %s.status, %s AS transaction_time, '%s' AS source_table " +
                                "FROM %s %s JOIN customers c ON %s.customer_id=c.username JOIN cars car ON %s.car_id=car.id WHERE %s.dealer_id=? %s",
                        selectedType, alias, dateCol, tbl, tbl, alias, alias, alias,
                        alias, (!selectedStatus.equals("Tümü")?"AND " + alias + ".status=?":""))
                );
            }

            query.append(" ORDER BY transaction_time DESC");
            PreparedStatement pstmt = conn.prepareStatement(query.toString());

            int idx = 1;
            if (selectedType.equals("Tümü")) {
                pstmt.setInt(idx++, dealerId);
                if (!selectedStatus.equals("Tümü")) pstmt.setString(idx++, selectedStatus);
                pstmt.setInt(idx++, dealerId);
                if (!selectedStatus.equals("Tümü")) pstmt.setString(idx++, selectedStatus);
                pstmt.setInt(idx++, dealerId);
                if (!selectedStatus.equals("Tümü")) pstmt.setString(idx++, selectedStatus);
            } else {
                pstmt.setInt(idx++, dealerId);
                if (!selectedStatus.equals("Tümü")) pstmt.setString(idx++, selectedStatus);
            }

            ResultSet rs = pstmt.executeQuery();
            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                        rs.getString("customer_name"),
                        rs.getString("car_info"),
                        rs.getString("transaction_type"),
                        rs.getString("status"),
                        rs.getTimestamp("transaction_time"),
                        rs.getString("source_table")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bildirimler yüklenirken hata oluştu!");
        }
    }

    private int getNotificationId(int row) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String customer = (String) tableModel.getValueAt(row, 0);
            String car = (String) tableModel.getValueAt(row, 1);
            String type = (String) tableModel.getValueAt(row, 2);
            String status = (String) tableModel.getValueAt(row, 3);
            java.sql.Timestamp date = (java.sql.Timestamp) tableModel.getValueAt(row, 4);
            String tbl = (String) tableModel.getValueAt(row, 5);

            String sql = "";
            if (tbl.equals("price_offers")) {
                sql = "SELECT id FROM price_offers WHERE status=? AND offer_date=? AND dealer_id=? AND customer_id=(SELECT username FROM customers WHERE name=?) AND car_id=(SELECT id FROM cars WHERE CONCAT(brand,' ',model,' ',year)=?)";
            } else if (tbl.equals("test_drive_requests")) {
                sql = "SELECT id FROM test_drive_requests WHERE status=? AND request_date=? AND dealer_id=? AND customer_id=(SELECT username FROM customers WHERE name=?) AND car_id=(SELECT id FROM cars WHERE CONCAT(brand,' ',model,' ',year)=?)";
            } else {
                sql = "SELECT id FROM orders WHERE status=? AND order_date=? AND dealer_id=? AND customer_id=(SELECT username FROM customers WHERE name=?) AND car_id=(SELECT id FROM cars WHERE CONCAT(brand,' ',model,' ',year)=?)";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setTimestamp(2, date);
            ps.setInt(3, dealerId);
            ps.setString(4, customer);
            ps.setString(5, car);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showResponseDialog(int id, String sourceTable) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String q = "SELECT transaction_type, status, offer_price FROM (" +
                    "SELECT id, 'price_offers' AS source_table, 'Fiyat Teklifi' AS transaction_type, status, offer_price FROM price_offers " +
                    "UNION ALL " +
                    "SELECT id, 'test_drive_requests', 'Deneme Sürüşü', status, NULL FROM test_drive_requests " +
                    "UNION ALL " +
                    "SELECT id, 'orders', 'Sipariş', status, NULL FROM orders) AS combined WHERE id=? AND source_table=?";
            PreparedStatement st = conn.prepareStatement(q);
            st.setInt(1, id);
            st.setString(2, sourceTable);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                String type = rs.getString("transaction_type");
                Double price = rs.getObject("offer_price") != null? rs.getDouble("offer_price"): null;

                JDialog dlg = new JDialog(this, "Yanıt Ver", true);
                dlg.setSize(400, 250);
                dlg.setLocationRelativeTo(this);
                
                // Gradient arka planlı panel
                JPanel mainPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        int w = getWidth();
                        int h = getHeight();
                        GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_START, 0, h, BACKGROUND_END);
                        g2d.setPaint(gp);
                        g2d.fillRect(0, 0, w, h);
                    }
                };
                
                JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
                contentPanel.setOpaque(false);
                contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                if (type.equals("Fiyat Teklifi")) {
                    JLabel priceLabel = new JLabel("Teklif Edilen Fiyat: " + price + " TL");
                    priceLabel.setFont(SF_PRO_DISPLAY);
                    contentPanel.add(priceLabel);
                    
                    JLabel confirmLabel = new JLabel("Bu fiyat teklifini onaylamak veya reddetmek ister misiniz?");
                    confirmLabel.setFont(SF_PRO_DISPLAY);
                    contentPanel.add(confirmLabel);
                } else {
                    JLabel confirmLabel = new JLabel("Bu " + type + " talebini onaylamak veya reddetmek ister misiniz?");
                    confirmLabel.setFont(SF_PRO_DISPLAY);
                    contentPanel.add(confirmLabel);
                }

                // Buton paneli
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
                buttonPanel.setOpaque(false);
                
                JButton approveButton = new JButton("Onayla");
                approveButton.setFont(SF_PRO_DISPLAY);
                approveButton.setPreferredSize(new Dimension(100, 35));
                approveButton.setBackground(PRIMARY_COLOR);
                approveButton.setForeground(Color.WHITE);
                approveButton.setBorderPainted(false);
                approveButton.setFocusPainted(false);
                approveButton.addActionListener(e -> {
                    processNotification(id, sourceTable, "Onaylandı");
                    dlg.dispose();
                });
                
                JButton rejectButton = new JButton("Reddet");
                rejectButton.setFont(SF_PRO_DISPLAY);
                rejectButton.setPreferredSize(new Dimension(100, 35));
                rejectButton.setBackground(new Color(255, 59, 48));
                rejectButton.setForeground(Color.WHITE);
                rejectButton.setBorderPainted(false);
                rejectButton.setFocusPainted(false);
                rejectButton.addActionListener(e -> {
                    processNotification(id, sourceTable, "Reddedildi");
                    dlg.dispose();
                });
                
                buttonPanel.add(approveButton);
                buttonPanel.add(rejectButton);
                
                contentPanel.add(buttonPanel);
                mainPanel.add(contentPanel, BorderLayout.CENTER);
                
                dlg.add(mainPanel);
                dlg.setVisible(true);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bildirim detayları yüklenirken hata oluştu!");
        }
    }

    public void setOnNotificationProcessed(Runnable callback) {
        this.onNotificationProcessed = callback;
    }

    private void processNotification(int id, String sourceTable, String action) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // İşlem durumunu güncelle
                String upd = sourceTable.equals("price_offers") ? "UPDATE price_offers SET status=? WHERE id=?" :
                        sourceTable.equals("test_drive_requests") ? "UPDATE test_drive_requests SET status=? WHERE id=?" :
                                "UPDATE orders SET status=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(upd);
                ps.setString(1, action);
                ps.setInt(2, id);
                ps.executeUpdate();

                // Onaylandı durumunda araç durumunu ve satış raporlarını güncelle
                if (action.equals("Onaylandı")) {
                    // Araç ID'sini al
                    String carIdQuery = sourceTable.equals("price_offers") ? 
                            "SELECT car_id, customer_id, offer_price FROM price_offers WHERE id=?" :
                            sourceTable.equals("test_drive_requests") ? 
                                    "SELECT car_id, customer_id FROM test_drive_requests WHERE id=?" :
                                    "SELECT car_id, customer_id FROM orders WHERE id=?";
                    
                    PreparedStatement carPs = conn.prepareStatement(carIdQuery);
                    carPs.setInt(1, id);
                    ResultSet rs = carPs.executeQuery();
                    
                    if (rs.next()) {
                        int carId = rs.getInt("car_id");
                        String customerId = rs.getString("customer_id");
                        
                        // Araç durumunu güncelle
                        String carStatus = sourceTable.equals("test_drive_requests") ? "Test Sürüşünde" : "Satıldı";
                        PreparedStatement carUpdatePs = conn.prepareStatement("UPDATE cars SET status=? WHERE id=?");
                        carUpdatePs.setString(1, carStatus);
                        carUpdatePs.setInt(2, carId);
                        carUpdatePs.executeUpdate();
                        
                        // Fiyat teklifi veya sipariş onaylandıysa satış raporuna ekle
                        if (sourceTable.equals("price_offers") || sourceTable.equals("orders")) {
                            double salePrice = sourceTable.equals("price_offers") ? 
                                    rs.getDouble("offer_price") : 
                                    getCarPrice(conn, carId);
                            
                            // Önce dealer_id'yi al
                            PreparedStatement dealerPs = conn.prepareStatement(
                                "SELECT dealer FROM cars WHERE id=?"
                            );
                            dealerPs.setInt(1, carId);
                            ResultSet dealerRs = dealerPs.executeQuery();
                            
                            if (dealerRs.next()) {
                                int dealerId = dealerRs.getInt("dealer");
                                
                                PreparedStatement salesPs = conn.prepareStatement(
                                    "INSERT INTO sales_reports (car_id, customer_id, dealer_id, sale_price, sale_date) " +
                                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)"
                                );
                                salesPs.setInt(1, carId);
                                salesPs.setString(2, customerId);
                                salesPs.setInt(3, dealerId);
                                salesPs.setDouble(4, salePrice);
                                salesPs.executeUpdate();
                            }
                        }
                    }
                }
                
                conn.commit();
                JOptionPane.showMessageDialog(this, "İşlem başarıyla gerçekleştirildi!");
                loadNotifications();
                
                // Callback'i çağır
                if (onNotificationProcessed != null) {
                    onNotificationProcessed.run();
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "İşlem sırasında hata oluştu!");
        }
    }

    private double getCarPrice(Connection conn, int carId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT price FROM cars WHERE id=?");
        ps.setInt(1, carId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getDouble("price");
        }
        return 0.0;
    }
}
