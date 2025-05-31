package com.carsales.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.carsales.database.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class ReportsDialog extends JDialog {
    private Integer dealerId;
    private JTabbedPane tabbedPane;
    private JLabel totalTransactionsLabel;
    
    private static final Color PRIMARY_COLOR = new Color(0, 122, 255);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(206, 212, 218);
    private static final Font SF_PRO_DISPLAY = new Font("SF Pro Display", Font.PLAIN, 13);
    private static final Font SF_PRO_DISPLAY_BOLD = new Font("SF Pro Display", Font.BOLD, 13);
    
    // Sınıf seviyesinde değişken ekle
    private JComboBox<String> statusCombo;
    
    public ReportsDialog(Frame parent, Integer dealerId) {
        super(parent, "Raporlar", true);
        this.dealerId = dealerId;
        
        setSize(1000, 750);
        setLocationRelativeTo(parent);
        
        // Ana panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Tab butonları paneli
        JPanel tabButtonsPanel = new JPanel(new GridLayout(1, 7, 0, 0)); // 7 sekme için güncellendi
        tabButtonsPanel.setBackground(Color.WHITE);
        tabButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        // İçerik paneli
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Tab içerikleri
        JPanel[] panels = {
            createMonthlySalesPanel(),
            createBrandSalesPanel(),
            createStatusDistributionPanel(),
            createSpecificReportPanel(),
            createCustomerReportsPanel(),
            createMassAnalysisPanel(),
            createSalesForecastPanel() 
        };
        
        String[] tabTitles = {
            "Aylık Satışlar",
            "Marka Bazlı Satışlar",
            "Durum Dağılımı",
            "Spesifik Rapor",
            "Müşteri Raporları",
            "Kitle Analiz Raporu",
            "Satış Tahmini" 
        };
        
        JButton[] tabButtons = new JButton[tabTitles.length];
        ButtonGroup buttonGroup = new ButtonGroup();
        
        for (int i = 0; i < panels.length; i++) {
            // Panel ekle
            contentPanel.add(panels[i], String.valueOf(i));
            
            // Tab butonu oluştur
            tabButtons[i] = createTabButton(tabTitles[i], i);
            buttonGroup.add(tabButtons[i]);
            tabButtonsPanel.add(tabButtons[i]);
            
            final int index = i;
            tabButtons[i].addActionListener(e -> {
                ((CardLayout) contentPanel.getLayout()).show(contentPanel, String.valueOf(index));
                updateTabButtons(tabButtons, index);
            });
        }
        
        // İlk tab'i seçili yap
        tabButtons[0].setSelected(true);
        updateTabButtons(tabButtons, 0);
        
        mainPanel.add(tabButtonsPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JButton createTabButton(String title, int index) {
        JButton button = new JButton(title) {
            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width = 133; // Sabit genişlik
                return size;
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Arka plan
                if (isSelected()) {
                    g2d.setColor(new Color(0, 122, 255, 15));
                    g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
                    
                    // Alt çizgi
                    g2d.setColor(PRIMARY_COLOR);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                }
                
                // Metin
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                
                g2d.setColor(isSelected() ? PRIMARY_COLOR : new Color(108, 117, 125));
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setFont(SF_PRO_DISPLAY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void updateTabButtons(JButton[] buttons, int selectedIndex) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setSelected(i == selectedIndex);
            buttons[i].repaint();
        }
    }
    
    private JPanel createBrandSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Üst panel
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel("Marka Bazlı Satış Raporu");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // İstatistik özeti paneli
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);
        
        // Grafik paneli
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT brand, COUNT(*) as count FROM cars " +
                          "WHERE dealer = ? AND status = 'Satıldı' " +
                          "GROUP BY brand ORDER BY count DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, dealerId);
            ResultSet rs = pstmt.executeQuery();
            
            DefaultPieDataset dataset = new DefaultPieDataset();
            int totalCount = 0;
            List<String> brands = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            
            while (rs.next()) {
                String brand = rs.getString("brand");
                int count = rs.getInt("count");
                brands.add(brand);
                counts.add(count);
                totalCount += count;
            }

            // En çok satan marka istatistiği
            if (!brands.isEmpty()) {
                JPanel bestSellerPanel = createStatPanel("En Çok Satan Marka", 
                    brands.get(0) + " (" + counts.get(0) + " adet)", 
                    new Color(46, 213, 115));
                statsPanel.add(bestSellerPanel);
            }

            // Toplam satış istatistiği
            JPanel totalSalesPanel = createStatPanel("Toplam Satış", 
                String.valueOf(totalCount) + " adet",
                new Color(55, 164, 255));
            statsPanel.add(totalSalesPanel);
            
            topPanel.add(statsPanel, BorderLayout.EAST);
            
            // Yüzdeleri hesapla ve dataset'e ekle
            for (int i = 0; i < brands.size(); i++) {
                double percentage = (double) counts.get(i) / totalCount * 100;
                dataset.setValue(brands.get(i) + " - " + String.format("%.1f%%", percentage), counts.get(i));
            }
            
            JFreeChart chart = ChartFactory.createPieChart(
                null, 
                dataset,
                true,  // legend
                true,  // tooltips
                false  // URLs
            );
            
            // Temel grafik özelleştirmeleri
            chart.setBackgroundPaint(Color.WHITE);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelFont(new Font("SF Pro Display", Font.PLAIN, 12));
            plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
            
            // Modern renkler
            Color[] pieColors = {
                new Color(0, 122, 255),    // macOS blue
                new Color(88, 86, 214),    // macOS purple
                new Color(255, 45, 85),    // macOS red
                new Color(46, 213, 115),   // macOS green
                new Color(255, 149, 0),    // macOS orange
                new Color(90, 200, 250),   // macOS light blue
                new Color(255, 59, 48),    // macOS pink
                new Color(175, 82, 222)    // macOS violet
            };
            
            int colorIndex = 0;
            for (String brand : brands) {
                String key = brand + " - " + String.format("%.1f%%", 
                    (double)counts.get(brands.indexOf(brand)) / totalCount * 100);
                plot.setSectionPaint(key, pieColors[colorIndex % pieColors.length]);
                colorIndex++;
            }
            
            ChartPanel chartComponent = new ChartPanel(chart);
            chartComponent.setOpaque(false);
            chartPanel.add(chartComponent, BorderLayout.CENTER);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Grafik oluşturulurken hata oluştu!");
        }
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    
    private JPanel createStatPanel(String title, String value, Color accentColor) {
        JPanel statPanel = new JPanel(new BorderLayout(5, 3));
        statPanel.setOpaque(false);
        
        // İstatistik başlığı
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SF_PRO_DISPLAY);
        titleLabel.setForeground(new Color(108, 117, 125));
        
        // İstatistik değeri
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(SF_PRO_DISPLAY_BOLD);
        valueLabel.setForeground(accentColor);
        
        statPanel.add(titleLabel, BorderLayout.NORTH);
        statPanel.add(valueLabel, BorderLayout.CENTER);
        
        return statPanel;
    }
    
    private JPanel createMonthlySalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Üst panel (yıl seçimi ve başlık)
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel("Aylık Satış Raporu");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Yıl seçim paneli
        JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        yearPanel.setOpaque(false);
        
        JLabel yearLabel = new JLabel("Yıl:");
        yearLabel.setFont(SF_PRO_DISPLAY);
        yearLabel.setForeground(TEXT_COLOR);
        yearPanel.add(yearLabel);
        
        // Modern combobox
        JComboBox<Integer> yearCombo = new JComboBox<>();
        yearCombo.setFont(SF_PRO_DISPLAY);
        yearCombo.setPreferredSize(new Dimension(120, 30));
        yearCombo.setBackground(Color.WHITE);
        yearCombo.setForeground(TEXT_COLOR);
        yearCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        
        // Mevcut yılları veritabanından al
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT DISTINCT EXTRACT(YEAR FROM sale_date) as year " +
                          "FROM sales_reports WHERE dealer_id = ? " +
                          "ORDER BY year DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, dealerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                yearCombo.addItem(rs.getInt("year"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Yıllar yüklenirken hata oluştu!");
        }
        
        yearPanel.add(yearCombo);
        
        // Grafik paneli - Declare before refresh button
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        
        topPanel.add(yearPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Add chart panel to main panel
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // Yıl değiştiğinde grafiği güncelle
        yearCombo.addActionListener(e -> {
            if (yearCombo.getSelectedItem() != null) {
                updateMonthlySalesChart(chartPanel, (Integer) yearCombo.getSelectedItem());
            }
        });
        
        // İlk yükleme
        if (yearCombo.getItemCount() > 0) {
            updateMonthlySalesChart(chartPanel, (Integer) yearCombo.getItemAt(0));
        }
        
        return panel;
    }
    
    private void updateMonthlySalesChart(JPanel chartPanel, int selectedYear) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT EXTRACT(MONTH FROM sale_date) as month, COUNT(*) as count " +
                          "FROM sales_reports WHERE dealer_id = ? AND EXTRACT(YEAR FROM sale_date) = ? " +
                          "GROUP BY EXTRACT(MONTH FROM sale_date) " +
                          "ORDER BY month";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, dealerId);
            pstmt.setInt(2, selectedYear);
            ResultSet rs = pstmt.executeQuery();
            
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            // Önce tüm ayları 0 olarak ekle
            for (int i = 1; i <= 12; i++) {
                dataset.addValue(0, "Satışlar", getMonthName(i));
            }
            
            // Sonra veritabanından gelen satış verilerini güncelle
            while (rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                dataset.addValue(count, "Satışlar", getMonthName(month));
            }
            
            // Grafik oluştur
            JFreeChart chart = ChartFactory.createBarChart(
                null, // Başlık grafiğin üstünde değil, panel başlığında olacak
                null, // X ekseni etiketi
                "Satış Adedi",
                dataset
            );
            
            // Ana panel başlığı
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            JLabel titleLabel = new JLabel(selectedYear + " Yılı Aylık Satış Dağılımı");
            titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
            titleLabel.setForeground(TEXT_COLOR);
            headerPanel.add(titleLabel, BorderLayout.WEST);
            
            // Grafik özelleştirmeleri
            chart.setBackgroundPaint(Color.WHITE);
            
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinesVisible(false);
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlinePaint(new Color(240, 240, 240));
            plot.setOutlinePaint(null);
            
            // Bar özelleştirmeleri
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(0, 122, 255));
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setShadowVisible(false);
            renderer.setDrawBarOutline(false);
            renderer.setMaximumBarWidth(0.075);
            
            // Bar üzerinde değer gösterimi
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelFont(new Font("SF Pro Display", Font.PLAIN, 11));
            renderer.setDefaultItemLabelPaint(TEXT_COLOR);
            renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER
            ));
            
            // Eksen özelleştirmeleri
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setTickLabelFont(new Font("SF Pro Display", Font.PLAIN, 12));
            domainAxis.setTickLabelPaint(TEXT_COLOR);
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setAxisLinePaint(new Color(240, 240, 240));
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(new Font("SF Pro Display", Font.PLAIN, 12));
            rangeAxis.setLabelFont(new Font("SF Pro Display", Font.PLAIN, 12));
            rangeAxis.setTickLabelPaint(TEXT_COLOR);
            rangeAxis.setAxisLinePaint(new Color(240, 240, 240));
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            
            
            chartPanel.removeAll();
            chartPanel.setLayout(new BorderLayout());
            chartPanel.setBackground(Color.WHITE);
            
            // Grafik paneli
            ChartPanel newChartPanel = new ChartPanel(chart);
            newChartPanel.setBackground(Color.WHITE);
            newChartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            
            // Panel boyutlandırma
            chartPanel.add(headerPanel, BorderLayout.NORTH);
            chartPanel.add(newChartPanel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Grafik oluşturulurken hata oluştu!");
        }
    }
    
    private JPanel createStatusDistributionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Üst panel
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel("Araç Durum Dağılımı");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // İstatistik özeti paneli
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setOpaque(false);
        
        // Grafik paneli
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status, COUNT(*) as count FROM cars " +
                          "WHERE dealer = ? GROUP BY status ORDER BY count DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, dealerId);
            ResultSet rs = pstmt.executeQuery();
            
            DefaultPieDataset dataset = new DefaultPieDataset();
            int totalCount = 0;
            
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                totalCount += count;
                dataset.setValue(status, count);
            }
            
            // Ana panel başlığı
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            JLabel totalLabel = new JLabel("Toplam Araç: " + totalCount);
            totalLabel.setFont(new Font("SF Pro Display", Font.BOLD, 14));
            totalLabel.setForeground(TEXT_COLOR);
            statsPanel.add(totalLabel);
            
            headerPanel.add(statsPanel, BorderLayout.EAST);
            
            
            JFreeChart chart = ChartFactory.createPieChart(
                null, 
                dataset,
                true,
                true,
                false
            );
            
            chart.setBackgroundPaint(Color.WHITE);
            
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlinePaint(null);
            plot.setShadowPaint(null);
            plot.setLabelFont(new Font("SF Pro Display", Font.PLAIN, 12));
            plot.setLabelBackgroundPaint(null);
            plot.setLabelOutlinePaint(null);
            plot.setLabelShadowPaint(null);
            plot.setInteriorGap(0.04);
            
            // Modern renk paleti
            plot.setSectionPaint("Stokta", new Color(46, 213, 115));
            plot.setSectionPaint("Satıldı", new Color(0, 122, 255));
            plot.setSectionPaint("Test Sürüşünde", new Color(255, 149, 0));
            plot.setSectionPaint("Gösterimde", new Color(90, 200, 250));
            
            // Etiket stilleri
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}\n{1} adet ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0.0%")
            ));
            
            // Legend özelleştirmeleri
            LegendTitle legend = chart.getLegend();
            legend.setPosition(RectangleEdge.RIGHT);
            legend.setItemFont(new Font("SF Pro Display", Font.PLAIN, 12));
            legend.setBackgroundPaint(Color.WHITE);
            legend.setMargin(new RectangleInsets(10, 10, 10, 10));
            
           
            chartPanel.removeAll();
            chartPanel.setLayout(new BorderLayout());
            chartPanel.setBackground(Color.WHITE);
            
            // Grafik paneli
            ChartPanel newChartPanel = new ChartPanel(chart);
            newChartPanel.setBackground(Color.WHITE);
            newChartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            
            // Panel boyutlandırma
            chartPanel.add(headerPanel, BorderLayout.NORTH);
            chartPanel.add(newChartPanel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Grafik oluşturulurken hata oluştu!");
        }
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSpecificReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Üst panel
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        // Başlık
        JLabel titleLabel = new JLabel("Filtrele");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        
        final JPanel filterPanelContainer = new JPanel(new BorderLayout(10, 0));
        filterPanelContainer.setOpaque(false);

        // Ortalamak için wrapper panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JPanel filterPanel = new JPanel();
        filterPanel.setOpaque(false);
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        Color inputBg = Color.WHITE;
        Border inputBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        Font inputFont = new Font("SF Pro Display", Font.PLAIN, 13);

        // Marka filtresi
        JLabel brandLabel = new JLabel("Marka:");
        brandLabel.setFont(inputFont);
        brandLabel.setForeground(TEXT_COLOR);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(brandLabel);
        JTextField brandField = new JTextField();
        styleTextField(brandField, inputBg, inputBorder, inputFont);
        brandField.setMaximumSize(new Dimension(250, 35));
        brandField.setPreferredSize(new Dimension(250, 35));
        brandField.setMinimumSize(new Dimension(250, 35));
        brandField.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(brandField);
        filterPanel.add(Box.createVerticalStrut(10));

        // Model filtresi
        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setFont(inputFont);
        modelLabel.setForeground(TEXT_COLOR);
        modelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(modelLabel);
        JTextField modelField = new JTextField();
        styleTextField(modelField, inputBg, inputBorder, inputFont);
        modelField.setMaximumSize(new Dimension(250, 35));
        modelField.setPreferredSize(new Dimension(250, 35));
        modelField.setMinimumSize(new Dimension(250, 35));
        modelField.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(modelField);
        filterPanel.add(Box.createVerticalStrut(10));
        
        // Yıl filtresi
        JLabel yearLabel = new JLabel("Yıl:");
        yearLabel.setFont(inputFont);
        yearLabel.setForeground(TEXT_COLOR);
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(yearLabel);
        JTextField yearField = new JTextField();
        styleTextField(yearField, inputBg, inputBorder, inputFont);
        yearField.setMaximumSize(new Dimension(250, 35));
        yearField.setPreferredSize(new Dimension(250, 35));
        yearField.setMinimumSize(new Dimension(250, 35));
        yearField.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(yearField);
        filterPanel.add(Box.createVerticalStrut(10));
        
        // Paket filtresi
        JLabel packageLabel = new JLabel("Paket:");
        packageLabel.setFont(inputFont);
        packageLabel.setForeground(TEXT_COLOR);
        packageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(packageLabel);
        JTextField packageField = new JTextField();
        styleTextField(packageField, inputBg, inputBorder, inputFont);
        packageField.setMaximumSize(new Dimension(250, 35));
        packageField.setPreferredSize(new Dimension(250, 35));
        packageField.setMinimumSize(new Dimension(250, 35));
        packageField.setAlignmentX(Component.CENTER_ALIGNMENT);
        filterPanel.add(packageField);
        filterPanel.add(Box.createVerticalStrut(20));

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        // Rapor butonu
        JButton reportButton = new JButton("Filtrele");
        styleButton(reportButton, PRIMARY_COLOR);
        buttonPanel.add(reportButton);

        // Temizle butonu
        JButton clearButton = new JButton("Temizle");
        styleButton(clearButton, new Color(108, 117, 125));
        clearButton.addActionListener(e -> {
            brandField.setText("");
            modelField.setText("");
            yearField.setText("");
            packageField.setText("");
        });
        buttonPanel.add(clearButton);

        filterPanel.add(buttonPanel);

        centerWrapper.add(filterPanel, gbc);
        filterPanelContainer.add(centerWrapper, BorderLayout.CENTER);

        
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        chartPanel.setVisible(false); // Başlangıçta gizli
        
        
        reportButton.addActionListener(e -> {
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            String year = yearField.getText().trim();
            String packageType = packageField.getText().trim();
            
            
            generateSpecificReport(chartPanel, brand, model, year, packageType, filterPanelContainer);
            filterPanelContainer.setVisible(false);
            chartPanel.setVisible(true);
        });

        
        topPanel.add(filterPanelContainer, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleTextField(JTextField field, Color bg, Border border, Font font) {
        field.setBackground(bg);
        field.setBorder(border);
        field.setFont(font);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(PRIMARY_COLOR);
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("SF Pro Display", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover efekti
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(getDarkerColor(bgColor));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private Color getDarkerColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(0f, hsb[2] - 0.1f));
    }
    
    private void generateSpecificReport(JPanel chartPanel, String brand, String model, String year, String packageType, JPanel filterPanelContainer) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            String totalQuery = "SELECT COUNT(*) as total_count FROM sales_reports WHERE dealer_id = ?";
            PreparedStatement totalStmt = conn.prepareStatement(totalQuery);
            totalStmt.setInt(1, dealerId);
            ResultSet totalRs = totalStmt.executeQuery();
            
            int totalCount = 0;
            if (totalRs.next()) {
                totalCount = totalRs.getInt("total_count");
            }
            
            if (totalCount == 0) {
                JOptionPane.showMessageDialog(this, "Satış verisi bulunamadı!");
                return;
            }
            
            // Filtrelenmiş satış sayısını al
            StringBuilder filteredQuery = new StringBuilder(
                "SELECT COUNT(*) as filtered_count FROM sales_reports sr " +
                "JOIN cars c ON sr.car_id = c.id " +
                "WHERE sr.dealer_id = ?");
            
            List<Object> params = new ArrayList<>();
            params.add(dealerId);
            
            // Marka filtresi
            if (!brand.isEmpty()) {
                filteredQuery.append(" AND LOWER(c.brand) LIKE LOWER(?)");
                params.add("%" + brand + "%");
            }
            
            // Model filtresi
            if (!model.isEmpty()) {
                filteredQuery.append(" AND LOWER(c.model) LIKE LOWER(?)");
                params.add("%" + model + "%");
            }
            
            // Yıl filtresi
            if (!year.isEmpty()) {
                try {
                    int yearValue = Integer.parseInt(year);
                    filteredQuery.append(" AND c.year = ?");
                    params.add(yearValue);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Yıl alanı sayısal bir değer olmalıdır!");
                    return;
                }
            }
            
            // Paket filtresi
            if (!packageType.isEmpty()) {
                filteredQuery.append(" AND LOWER(c.package_type) LIKE LOWER(?)");
                params.add("%" + packageType + "%");
            }
            
            // Filtrelenmiş satış sayısını al
            PreparedStatement filteredStmt = conn.prepareStatement(filteredQuery.toString());
            for (int i = 0; i < params.size(); i++) {
                filteredStmt.setObject(i + 1, params.get(i));
            }
            ResultSet filteredRs = filteredStmt.executeQuery();
            
            int filteredCount = 0;
            if (filteredRs.next()) {
                filteredCount = filteredRs.getInt("filtered_count");
            }
            
            // İstatistik paneli
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            statsPanel.setOpaque(false);
            
            // Filtrelenmiş satış sayısı
            JPanel filteredPanel = createStatPanel("Filtreye Uyan Satışlar", 
                filteredCount + " adet", new Color(46, 213, 115));
            statsPanel.add(filteredPanel);
            
            // Toplam satış sayısı
            JPanel totalPanel = createStatPanel("Toplam Satış", 
                totalCount + " adet", new Color(55, 164, 255));
            statsPanel.add(totalPanel);
            
            // Pasta grafik verilerini hazırla
            DefaultPieDataset dataset = new DefaultPieDataset();
            double filteredPercentage = (double) filteredCount / totalCount * 100;
            double otherPercentage = 100 - filteredPercentage;
            
            String filterDescription = "Seçilen Filtreye Uyan";
            if (!brand.isEmpty()) filterDescription += " - " + brand;
            if (!model.isEmpty()) filterDescription += " - " + model;
            if (!year.isEmpty()) filterDescription += " - " + year;
            if (!packageType.isEmpty()) filterDescription += " - " + packageType;
            
            // Diğer satışları hesapla
            int otherCount = totalCount - filteredCount;
            
            dataset.setValue(filterDescription + " (" + String.format("%.1f%%", filteredPercentage) + ")", 
                           filteredCount);
            dataset.setValue("Diğer Satışlar (" + String.format("%.1f%%", otherPercentage) + ")", 
                           otherCount);
            
            // Grafiği oluştur
            JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                true,
                true,
                false
            );
            
            // Grafik özelleştirmeleri
            chart.setBackgroundPaint(Color.WHITE);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelFont(new Font("SF Pro Display", Font.PLAIN, 12));
            plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
            
            // Renkleri ayarla
            plot.setSectionPaint(filterDescription + " (" + String.format("%.1f%%", filteredPercentage) + ")", 
                               new Color(0, 122, 255));
            plot.setSectionPaint("Diğer Satışlar (" + String.format("%.1f%%", otherPercentage) + ")", 
                               new Color(108, 117, 125));
            
            // Grafik panelini güncelle
            chartPanel.removeAll();
            chartPanel.setLayout(new BorderLayout(10, 10));
            
            // Üst panel (istatistikler ve geri dön butonu)
            JPanel topStatsPanel = new JPanel(new BorderLayout(10, 0));
            topStatsPanel.setOpaque(false);
            
            // Geri dön butonu
            JButton backButton = new JButton("← Geri Dön");
            styleButton(backButton, new Color(108, 117, 125));
            backButton.addActionListener(e -> {
                chartPanel.setVisible(false);
                filterPanelContainer.setVisible(true);
            });
            
            topStatsPanel.add(backButton, BorderLayout.WEST);
            topStatsPanel.add(statsPanel, BorderLayout.CENTER);
            
            chartPanel.add(topStatsPanel, BorderLayout.NORTH);
            
            // Grafik boyutunu ayarla
            ChartPanel chartComponent = new ChartPanel(chart);
            chartComponent.setOpaque(false);
            chartComponent.setPreferredSize(new Dimension(600, 400));
            
            // Grafik container'ı
            JPanel chartContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            chartContainer.setOpaque(false);
            chartContainer.add(chartComponent);
            
            chartPanel.add(chartContainer, BorderLayout.CENTER);
            
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Rapor oluşturulurken bir hata oluştu!",
                "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getMonthName(int month) {
        String[] months = {
            "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
        };
        return months[month - 1];
    }
    
    private JPanel createCustomerReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Üst panel
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel("Müşteri Raporları");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Filtre paneli
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);
        
        // Bayi filtresi
        JLabel dealerLabel = new JLabel("Bayi:");
        dealerLabel.setFont(SF_PRO_DISPLAY);
        dealerLabel.setForeground(TEXT_COLOR);
        filterPanel.add(dealerLabel);
        
        // Bayi combobox - modern stil
        JComboBox<DealerItem> dealerCombo = new JComboBox<>();
        dealerCombo.setFont(SF_PRO_DISPLAY);
        dealerCombo.setPreferredSize(new Dimension(200, 32));
        dealerCombo.setBackground(new Color(209, 215, 221));
        dealerCombo.setForeground(TEXT_COLOR);
        dealerCombo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton();
                button.setBackground(new Color(209, 215, 221));
                button.setBorder(BorderFactory.createEmptyBorder());
                
                button.setIcon(new Icon() {
                    @Override
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(TEXT_COLOR);
                        int[] xPoints = {x + 4, x + 8, x + 12};
                        int[] yPoints = {y + 6, y + 10, y + 6};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                    
                    @Override
                    public int getIconWidth() {
                        return 16;
                    }
                    
                    @Override
                    public int getIconHeight() {
                        return 16;
                    }
                });
                return button;
            }
        });
        dealerCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 215, 221), 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        
        // Combobox popup menü stili
        Object comp = dealerCombo.getUI().getAccessibleChild(dealerCombo, 0);
        if (comp instanceof JPopupMenu) {
            JPopupMenu popup = (JPopupMenu) comp;
            popup.setBorder(BorderFactory.createLineBorder(new Color(209, 215, 221)));
        }
        
        // Combobox renderer - özel görünüm için
        dealerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                
                if (isSelected) {
                    setBackground(new Color(209, 215, 221));
                    setForeground(TEXT_COLOR);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(TEXT_COLOR);
                }
                
                return this;
            }
        });
        
        loadDealers(dealerCombo); // Bayileri yükle
        filterPanel.add(dealerCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        
        // Kullanıcı adı filtresi
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        usernameLabel.setFont(SF_PRO_DISPLAY);
        usernameLabel.setForeground(TEXT_COLOR);
        filterPanel.add(usernameLabel);
        
        JTextField usernameField = new JTextField(20);
        styleTextField(usernameField, Color.WHITE, BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ), SF_PRO_DISPLAY);
        usernameField.putClientProperty("JTextField.placeholderText", "Ara...");
        
        JButton filterButton = new JButton("Filtrele");
        styleButton(filterButton, PRIMARY_COLOR);
        
        JButton refreshButton = new JButton("Yenile");
        styleButton(refreshButton, new Color(46, 213, 115));
        
        filterPanel.add(usernameField);
        filterPanel.add(filterButton);
        filterPanel.add(refreshButton);
        
        topPanel.add(filterPanel, BorderLayout.EAST);
        
        // Müşteri listesi tablosu
        String[] columns = {"Kullanıcı Adı", "Ad Soyad", "Yaş", "Telefon"};
        DefaultTableModel customerModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable customerTable = new JTable(customerModel);
        styleTable(customerTable);
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        styleScrollPane(customerScrollPane);
        
        // Müşteri bilgileri paneli
        JPanel customerInfoPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        customerInfoPanel.setOpaque(false);
        customerInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                "Müşteri Bilgileri",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SF Pro Display", Font.BOLD, 14),
                TEXT_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel registerDateLabel = new JLabel("Kayıt Tarihi: ");
        styleInfoLabel(registerDateLabel);
        totalTransactionsLabel = new JLabel("Toplam İşlem: ");
        styleInfoLabel(totalTransactionsLabel);
        JLabel totalSpentLabel = new JLabel("Toplam Harcama: ");
        styleInfoLabel(totalSpentLabel);
        
        customerInfoPanel.add(registerDateLabel);
        customerInfoPanel.add(totalTransactionsLabel);
        customerInfoPanel.add(totalSpentLabel);
        
        // Müşteri işlemleri tablosu
        String[] transactionColumns = {"İşlem Tipi", "Araç", "Tarih", "Açıklama", "Fiyat"};
        DefaultTableModel transactionModel = new DefaultTableModel(transactionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable transactionTable = new JTable(transactionModel);
        styleTable(transactionTable);
        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
        styleScrollPane(transactionScrollPane);
        
        // Sağ panel (müşteri bilgileri + işlemler)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);
        rightPanel.add(customerInfoPanel, BorderLayout.NORTH);
        rightPanel.add(transactionScrollPane, BorderLayout.CENTER);
        
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, customerScrollPane, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        
        // Bayi seçimi değiştiğinde
        dealerCombo.addActionListener(e -> {
            DealerItem selectedDealer = (DealerItem) dealerCombo.getSelectedItem();
            if (selectedDealer != null) {
                dealerId = selectedDealer.getId();
                loadCustomers(customerModel, usernameField.getText().trim());
            }
        });
        
        // Müşteri seçildiğinde işlemleri ve bilgileri göster
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    String username = (String) customerTable.getValueAt(selectedRow, 0);
                    loadCustomerTransactions(username, transactionModel);
                    loadCustomerInfo(username, registerDateLabel, totalTransactionsLabel, totalSpentLabel);
                }
            }
        });
        
        // Filtreleme işlemi
        filterButton.addActionListener(e -> {
            DealerItem selectedDealer = (DealerItem) dealerCombo.getSelectedItem();
            if (selectedDealer != null) {
                dealerId = selectedDealer.getId();
            String username = usernameField.getText().trim();
            loadCustomers(customerModel, username);
            }
        });
        
        // Yenileme işlemi
        refreshButton.addActionListener(e -> {
            DealerItem selectedDealer = (DealerItem) dealerCombo.getSelectedItem();
            if (selectedDealer != null) {
                dealerId = selectedDealer.getId();
                usernameField.setText("");
                loadCustomers(customerModel, "");
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                String selectedUsername = (String) customerTable.getValueAt(selectedRow, 0);
                loadCustomerTransactions(selectedUsername, transactionModel);
                    loadCustomerInfo(selectedUsername, registerDateLabel, totalTransactionsLabel, totalSpentLabel);
                }
            }
        });
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // İlk yükleme
        loadCustomers(customerModel, "");
        
        return panel;
    }
    
    private void styleTable(JTable table) {
        table.setFont(SF_PRO_DISPLAY);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Başlık stili
        JTableHeader header = table.getTableHeader();
        header.setFont(SF_PRO_DISPLAY_BOLD);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Alternatif satır renkleri
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(0, 122, 255, 30));
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    c.setForeground(TEXT_COLOR);
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }
    
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setOpaque(false);
    }
    
    private void styleInfoLabel(JLabel label) {
        label.setFont(SF_PRO_DISPLAY);
        label.setForeground(TEXT_COLOR);
    }
    
    private void loadCustomers(DefaultTableModel model, String usernameFilter) {
        model.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT DISTINCT c.username, c.name, c.age, c.phone " +
                "FROM customers c " +
                "LEFT JOIN price_offers po ON c.username = po.customer_id AND po.dealer_id = ? " +
                "LEFT JOIN test_drive_requests tdr ON c.username = tdr.customer_id AND tdr.dealer_id = ? " +
                "LEFT JOIN sales_reports sr ON c.username = sr.customer_id AND sr.dealer_id = ? " +
                "WHERE (po.customer_id IS NOT NULL OR " +
                "tdr.customer_id IS NOT NULL OR sr.customer_id IS NOT NULL)"
            );
            
            if (!usernameFilter.isEmpty()) {
                query.append(" AND LOWER(c.username) LIKE LOWER(?)");
            }
            
            query.append(" ORDER BY c.username");
            
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setInt(1, dealerId);
            pstmt.setInt(2, dealerId);
            pstmt.setInt(3, dealerId);
            
            if (!usernameFilter.isEmpty()) {
                pstmt.setString(4, "%" + usernameFilter + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("username"),
                    rs.getString("name"),
                    rs.getString("age"),
                    rs.getString("phone")
                };
                model.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Müşteri listesi yüklenirken hata oluştu!");
        }
    }
    
    private void loadCustomerTransactions(String username, DefaultTableModel model) {
        model.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = 
                "SELECT 'Satın Alım' as transaction_type, " +
                "       c.brand || ' ' || c.model as car_info, " +
                "       sr.sale_date as transaction_time, " +
                "       'Araç satın alındı' as description, " +
                "       sr.sale_price as price " +
                "FROM sales_reports sr " +
                "JOIN cars c ON sr.car_id = c.id " +
                "WHERE sr.customer_id = ? AND sr.dealer_id = ? " +
                "UNION ALL " +
                "SELECT 'Test Sürüşü' as transaction_type, " +
                "       c.brand || ' ' || c.model as car_info, " +
                "       tdr.request_date as transaction_time, " +
                "       'Test sürüşü ' || LOWER(tdr.status) as description, " +
                "       NULL as price " +
                "FROM test_drive_requests tdr " +
                "JOIN cars c ON tdr.car_id = c.id " +
                "WHERE tdr.customer_id = ? AND tdr.dealer_id = ? " +
                "UNION ALL " +
                "SELECT 'Fiyat Teklifi' as transaction_type, " +
                "       c.brand || ' ' || c.model as car_info, " +
                "       pt.offer_date as transaction_time, " +
                "       'Fiyat teklifi ' || LOWER(pt.status) as description, " +
                "       pt.offer_price as price " +
                "FROM price_offers pt " +
                "JOIN cars c ON pt.car_id = c.id " +
                "WHERE pt.customer_id = ? AND pt.dealer_id = ? " +
                "ORDER BY transaction_time DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setInt(2, dealerId);
            pstmt.setString(3, username);
            pstmt.setInt(4, dealerId);
            pstmt.setString(5, username);
            pstmt.setInt(6, dealerId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("transaction_type"),
                    rs.getString("car_info"),
                    rs.getTimestamp("transaction_time"),
                    rs.getString("description"),
                    rs.getObject("price") != null ? String.format("%.2f TL", rs.getDouble("price")) : "-"
                };
                model.addRow(row);
            }
            
            // Toplam işlem sayısını güncelle
            totalTransactionsLabel.setText("Toplam İşlem: " + model.getRowCount());
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Müşteri işlemleri yüklenirken hata oluştu!");
        }
    }
    
    private void loadCustomerInfo(String username, JLabel registerDateLabel, JLabel totalTransactionsLabel,
                                JLabel totalSpentLabel) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Müşteri kayıt bilgileri
            String customerQuery = 
                "SELECT registration_date " +
                "FROM customers " +
                "WHERE username = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(customerQuery);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                registerDateLabel.setText("Kayıt Tarihi: " + rs.getTimestamp("registration_date"));
            }
            
            // Toplam harcama
            String spendingQuery = 
                "SELECT COALESCE(SUM(sale_price), 0) as total_spent " +
                "FROM sales_reports " +
                "WHERE customer_id = ? AND dealer_id = ?";
            
            pstmt = conn.prepareStatement(spendingQuery);
            pstmt.setString(1, username);
            pstmt.setInt(2, dealerId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double totalSpent = rs.getDouble("total_spent");
                totalSpentLabel.setText(String.format("Toplam Harcama: %.2f TL", totalSpent));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Müşteri bilgileri yüklenirken hata oluştu!");
        }
    }
    
    private JPanel createMassAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Üst panel (başlık ve filtreler)
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel("Kitle Analiz Raporu");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Filtre paneli
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setOpaque(false);
        
        // Modern checkbox'lar
        JCheckBox[] checkboxes = {
            createStyledCheckbox("Marka"),
            createStyledCheckbox("Model"),
            createStyledCheckbox("Yıl"),
            createStyledCheckbox("Paket")
        };
        
        for (JCheckBox checkbox : checkboxes) {
            filterPanel.add(checkbox);
        }
        
        
        JButton analyzeButton = new JButton("Analiz Et");
        analyzeButton.setFont(SF_PRO_DISPLAY);
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setBackground(PRIMARY_COLOR);
        analyzeButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        analyzeButton.setFocusPainted(false);
        analyzeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover efekti
        analyzeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                analyzeButton.setBackground(getDarkerColor(PRIMARY_COLOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                analyzeButton.setBackground(PRIMARY_COLOR);
            }
        });
        
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(analyzeButton);
        
        topPanel.add(filterPanel, BorderLayout.EAST);
        
        // Sonuç paneli
        JPanel resultPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        resultPanel.setOpaque(false);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Sonuç alt panelleri
        JPanel testDrivePanel = createModernResultSubPanel("En Çok Test Sürüşü Yapılan Araçlar");
        JPanel salesPanel = createModernResultSubPanel("En Çok Satılan Araçlar");
        JPanel offersPanel = createModernResultSubPanel("En Çok Fiyat Teklifi Yapılan Araçlar");

        resultPanel.add(testDrivePanel);
        resultPanel.add(salesPanel);
        resultPanel.add(offersPanel);
        
        // Analiz butonu işlemi
        analyzeButton.addActionListener(e -> {
            boolean filterByBrand = checkboxes[0].isSelected();
            boolean filterByModel = checkboxes[1].isSelected();
            boolean filterByYear = checkboxes[2].isSelected();
            boolean filterByPackage = checkboxes[3].isSelected();
            
            analyzePopularCars(testDrivePanel, salesPanel, offersPanel,
                             filterByBrand, filterByModel, filterByYear, filterByPackage);
        });
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JCheckBox createStyledCheckbox(String text) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(SF_PRO_DISPLAY);
        checkbox.setForeground(TEXT_COLOR);
        checkbox.setOpaque(false);
        checkbox.setFocusPainted(false);
        checkbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Özel ikon boyutları
        int iconSize = 18;
        checkbox.setIcon(new CheckBoxIcon(iconSize, false));
        checkbox.setSelectedIcon(new CheckBoxIcon(iconSize, true));
        
        return checkbox;
    }

    private static class CheckBoxIcon implements Icon {
        private final int size;
        private final boolean selected;
        
        public CheckBoxIcon(int size, boolean selected) {
            this.size = size;
            this.selected = selected;
        }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Checkbox arkaplanı
            if (selected) {
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(x, y, size, size, 6, 6);
                
                // Tik işareti
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x + 4, y + 9, x + 7, y + 12);
                g2d.drawLine(x + 7, y + 12, x + 14, y + 5);
            } else {
                g2d.setColor(BORDER_COLOR);
                g2d.drawRoundRect(x, y, size - 1, size - 1, 6, 6);
            }
            
            g2d.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return size;
        }
        
        @Override
        public int getIconHeight() {
            return size;
        }
    }

    private JPanel createModernResultSubPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Modern başlık paneli
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titlePanel, BorderLayout.NORTH);

        
        String[] columns = {"Sıra", "Adet"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        styleAnalysisTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setOpaque(false);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void styleAnalysisTable(JTable table) {
        table.setFont(SF_PRO_DISPLAY);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Başlık stili
        JTableHeader header = table.getTableHeader();
        header.setFont(SF_PRO_DISPLAY_BOLD);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Alternatif satır renkleri ve hücre stili
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(0, 122, 255, 30));
                    c.setForeground(TEXT_COLOR);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    c.setForeground(TEXT_COLOR);
                }
                
                // Sıra sütunu için özel stil
                if (column == 0) {
                    setHorizontalAlignment(CENTER);
                    setFont(SF_PRO_DISPLAY_BOLD);
                } else {
                    setHorizontalAlignment(LEFT);
                    setFont(SF_PRO_DISPLAY);
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }
    
    private void analyzePopularCars(JPanel testDrivePanel, JPanel salesPanel, JPanel offersPanel,
                                  boolean filterByBrand, boolean filterByModel, 
                                  boolean filterByYear, boolean filterByPackage) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Test sürüşleri analizi
            StringBuilder testDriveQuery = new StringBuilder(
                "SELECT ");
            
            if (filterByBrand) testDriveQuery.append("c.brand, ");
            if (filterByModel) testDriveQuery.append("c.model, ");
            if (filterByYear) testDriveQuery.append("c.year, ");
            if (filterByPackage) testDriveQuery.append("c.package_type, ");
            
            // En az bir özellik seçili değilse varsayılan olarak marka ve model göster
            if (!filterByBrand && !filterByModel && !filterByYear && !filterByPackage) {
                testDriveQuery.append("c.brand, c.model, ");
            }
            
            testDriveQuery.append("COUNT(*) as count FROM test_drive_requests tdr ")
                         .append("JOIN cars c ON tdr.car_id = c.id ")
                         .append("WHERE tdr.dealer_id = ? ")
                         .append("GROUP BY ");
            
            if (filterByBrand) testDriveQuery.append("c.brand, ");
            if (filterByModel) testDriveQuery.append("c.model, ");
            if (filterByYear) testDriveQuery.append("c.year, ");
            if (filterByPackage) testDriveQuery.append("c.package_type, ");
            
            // En az bir özellik seçili değilse varsayılan olarak marka ve model grupla
            if (!filterByBrand && !filterByModel && !filterByYear && !filterByPackage) {
                testDriveQuery.append("c.brand, c.model, ");
            }
            
            // Son virgülü kaldır
            testDriveQuery.setLength(testDriveQuery.length() - 2);
            testDriveQuery.append(" ORDER BY count DESC LIMIT 3");
            
            // Satışlar analizi
            StringBuilder salesQuery = new StringBuilder(
                "SELECT ");
            
            if (filterByBrand) salesQuery.append("c.brand, ");
            if (filterByModel) salesQuery.append("c.model, ");
            if (filterByYear) salesQuery.append("c.year, ");
            if (filterByPackage) salesQuery.append("c.package_type, ");
            
            // En az bir özellik seçili değilse varsayılan olarak marka ve model göster
            if (!filterByBrand && !filterByModel && !filterByYear && !filterByPackage) {
                salesQuery.append("c.brand, c.model, ");
            }
            
            salesQuery.append("COUNT(*) as count FROM sales_reports sr ")
                     .append("JOIN cars c ON sr.car_id = c.id ")
                     .append("WHERE sr.dealer_id = ? ")
                     .append("GROUP BY ");
            
            if (filterByBrand) salesQuery.append("c.brand, ");
            if (filterByModel) salesQuery.append("c.model, ");
            if (filterByYear) salesQuery.append("c.year, ");
            if (filterByPackage) salesQuery.append("c.package_type, ");
            
            // En az bir özellik seçili değilse varsayılan olarak marka ve model grupla
            if (!filterByBrand && !filterByModel && !filterByYear && !filterByPackage) {
                salesQuery.append("c.brand, c.model, ");
            }
            
            // Son virgülü kaldır
            salesQuery.setLength(salesQuery.length() - 2);
            salesQuery.append(" ORDER BY count DESC LIMIT 3");
            
            // Fiyat teklifleri analizi
            StringBuilder offersQuery = new StringBuilder(
                "SELECT ");
            
            if (filterByBrand) offersQuery.append("c.brand, ");
            if (filterByModel) offersQuery.append("c.model, ");
            if (filterByYear) offersQuery.append("c.year, ");
            if (filterByPackage) offersQuery.append("c.package_type, ");
            
            // En az bir özellik seçili değilse varsayılan olarak marka ve model göster
            if (!filterByBrand && !filterByModel && !filterByYear && !filterByPackage) {
                offersQuery.append("c.brand, c.model, ");
            }
            
            offersQuery.append("COUNT(*) as count FROM price_offers po ")
                      .append("JOIN cars c ON po.car_id = c.id ")
                      .append("WHERE po.dealer_id = ? ")
                      .append("GROUP BY ");
            
            if (filterByBrand) offersQuery.append("c.brand, ");
            if (filterByModel) offersQuery.append("c.model, ");
            if (filterByYear) offersQuery.append("c.year, ");
            if (filterByPackage) offersQuery.append("c.package_type, ");
            
            // En az bir özellik seçili değilse varsayılan olarak marka ve model grupla
            if (!filterByBrand && !filterByModel && !filterByYear && !filterByPackage) {
                offersQuery.append("c.brand, c.model, ");
            }
            
            // Son virgülü kaldır
            offersQuery.setLength(offersQuery.length() - 2);
            offersQuery.append(" ORDER BY count DESC LIMIT 3");
            
            // Tablo başlıklarını güncelle
            updateTableHeaders(testDrivePanel, filterByBrand, filterByModel, filterByYear, filterByPackage);
            updateTableHeaders(salesPanel, filterByBrand, filterByModel, filterByYear, filterByPackage);
            updateTableHeaders(offersPanel, filterByBrand, filterByModel, filterByYear, filterByPackage);
            
            // Sonuçları göster
            showAnalysisResults(testDrivePanel, testDriveQuery.toString(), conn, filterByBrand, filterByModel, filterByYear, filterByPackage);
            showAnalysisResults(salesPanel, salesQuery.toString(), conn, filterByBrand, filterByModel, filterByYear, filterByPackage);
            showAnalysisResults(offersPanel, offersQuery.toString(), conn, filterByBrand, filterByModel, filterByYear, filterByPackage);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Analiz sırasında bir hata oluştu!");
        }
    }
    
    private void updateTableHeaders(JPanel panel, boolean filterByBrand, boolean filterByModel, 
                                  boolean filterByYear, boolean filterByPackage) {
        JScrollPane scrollPane = findScrollPane(panel);
        if (scrollPane == null) return;
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        // Mevcut sütunları temizle
        model.setColumnCount(0);
        
        // Sıra sütunu her zaman var
        model.addColumn("Sıra");
        
        // Seçili özelliklere göre sütunları ekle
        if (filterByBrand) model.addColumn("Marka");
        if (filterByModel) model.addColumn("Model");
        if (filterByYear) model.addColumn("Yıl");
        if (filterByPackage) model.addColumn("Paket");
        
        // Adet sütunu her zaman var
        model.addColumn("Adet");
        
        // Tüm mevcut satırları temizle
        model.setRowCount(0);
    }
    
    private JScrollPane findScrollPane(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                return (JScrollPane) comp;
            } else if (comp instanceof JPanel) {
                JScrollPane scrollPane = findScrollPane((JPanel) comp);
                if (scrollPane != null) {
                    return scrollPane;
                }
            }
        }
        return null;
    }
    
    private void showAnalysisResults(JPanel panel, String query, Connection conn,
                                   boolean filterByBrand, boolean filterByModel, 
                                   boolean filterByYear, boolean filterByPackage) throws SQLException {
        JScrollPane scrollPane = findScrollPane(panel);
        if (scrollPane == null) return;
        
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, dealerId);
        ResultSet rs = pstmt.executeQuery();
        
        int rank = 1;
        while (rs.next()) {
            List<Object> rowData = new ArrayList<>();
            rowData.add(rank++);
            
            // Seçili filtrelere göre verileri ekle
            if (filterByBrand) rowData.add(rs.getString("brand"));
            if (filterByModel) rowData.add(rs.getString("model"));
            if (filterByYear) rowData.add(rs.getInt("year"));
            if (filterByPackage) rowData.add(rs.getString("package_type"));
            
            rowData.add(rs.getInt("count"));
            
            model.addRow(rowData.toArray());
        }
    }

    // Yeni sınıf - Bayi bilgilerini tutmak için
    private static class DealerItem {
        private final int id;
        private final String name;
        
        public DealerItem(int id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }

    
    private void loadDealers(JComboBox<DealerItem> dealerCombo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, name FROM dealers ORDER BY name";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            dealerCombo.removeAllItems();
            while (rs.next()) {
                DealerItem dealer = new DealerItem(rs.getInt("id"), rs.getString("name"));
                dealerCombo.addItem(dealer);
                
                // Mevcut bayiyi seç
                if (rs.getInt("id") == dealerId) {
                    dealerCombo.setSelectedItem(dealer);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Bayi listesi yüklenirken hata oluştu!");
        }
    }

    private JPanel createSalesForecastPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Üst panel
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel("Satış Tahmini");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Kontrol paneli
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);
        
        JLabel periodLabel = new JLabel("Tahmin Periyodu:");
        periodLabel.setFont(SF_PRO_DISPLAY);
        periodLabel.setForeground(TEXT_COLOR);
        
        // Periyot seçimi için combobox
        JComboBox<Integer> periodCombo = new JComboBox<>();
        periodCombo.setFont(SF_PRO_DISPLAY);
        periodCombo.setPreferredSize(new Dimension(80, 32));
        periodCombo.setBackground(Color.WHITE);
        periodCombo.setForeground(TEXT_COLOR);
        
        JButton calculateButton = new JButton("Tahmin Hesapla");
        styleButton(calculateButton, PRIMARY_COLOR);
        calculateButton.setEnabled(false); // Başlangıçta devre dışı
        
        controlPanel.add(periodLabel);
        controlPanel.add(periodCombo);
        controlPanel.add(calculateButton);
        topPanel.add(controlPanel, BorderLayout.EAST);
        
        // Grafik paneli
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setOpaque(false);
        
        // Mevcut yıl sayısını kontrol et
        try (Connection conn = DatabaseConnection.getConnection()) {
            String countQuery = "SELECT COUNT(DISTINCT EXTRACT(YEAR FROM sale_date)) as year_count " +
                              "FROM sales_reports WHERE dealer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(countQuery);
            pstmt.setInt(1, dealerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int availableYears = rs.getInt("year_count");
                
                if (availableYears >= 2) {
                    // Sadece mevcut veri sayısına göre periyot seçeneklerini ekle
                    for (int i = 2; i <= Math.min(5, availableYears); i++) {
                        periodCombo.addItem(i);
                    }
                    periodCombo.setSelectedItem(2);
                    calculateButton.setEnabled(true);
                    
                    // İlk tahmin
                    updateForecastChart(chartPanel, 2);
                } else {
                    showNoDataMessage(chartPanel);
                    periodCombo.setEnabled(false);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showNoDataMessage(chartPanel);
            periodCombo.setEnabled(false);
        }
        
        // Hesaplama ve grafik oluşturma
        calculateButton.addActionListener(e -> {
            int selectedPeriod = (Integer) periodCombo.getSelectedItem();
            updateForecastChart(chartPanel, selectedPeriod);
        });
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void updateForecastChart(JPanel chartPanel, int period) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Önce seçilen periyot için yeterli veri olup olmadığını kontrol et
            String countQuery = "SELECT COUNT(DISTINCT EXTRACT(YEAR FROM sale_date)) as year_count " +
                              "FROM sales_reports WHERE dealer_id = ?";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            countStmt.setInt(1, dealerId);
            ResultSet countRs = countStmt.executeQuery();
            
            if (!countRs.next() || countRs.getInt("year_count") < period) {
                JOptionPane.showMessageDialog(this,
                    "Seçilen periyot için yeterli veri yok! En fazla " + countRs.getInt("year_count") + " yıllık veri mevcut.",
                    "Uyarı",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Son n yılın satış verilerini çek
            String query = "SELECT EXTRACT(YEAR FROM sale_date) as year, " +
                          "COUNT(*) as sales_count " +
                          "FROM sales_reports " +
                          "WHERE dealer_id = ? " +
                          "GROUP BY EXTRACT(YEAR FROM sale_date) " +
                          "ORDER BY year DESC " +
                          "LIMIT ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, dealerId);
            pstmt.setInt(2, period);
            ResultSet rs = pstmt.executeQuery();
            
            List<Integer> years = new ArrayList<>();
            List<Integer> sales = new ArrayList<>();
            
            while (rs.next()) {
                years.add(0, rs.getInt("year"));
                sales.add(0, rs.getInt("sales_count"));
            }
            
            if (sales.size() < period) {
                JOptionPane.showMessageDialog(this,
                    "Seçilen periyot için yeterli veri yok! Sadece " + sales.size() + " yıllık veri mevcut.",
                    "Uyarı",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Hareketli ortalama hesapla
            double forecast = calculateMovingAverage(sales);
            int nextYear = years.get(years.size() - 1) + 1;

            // Grafik verilerini hazırla
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            // Geçmiş veriler
            for (int i = 0; i < years.size(); i++) {
                dataset.addValue(sales.get(i), "Gerçekleşen Satışlar", String.valueOf(years.get(i)));
            }
            
            // Tahmin
            dataset.addValue(forecast, "Tahmin", String.valueOf(nextYear));
            
            // Grafik oluştur
            JFreeChart chart = ChartFactory.createLineChart(
                null,
                "Yıl",
                "Satış Adedi",
                dataset
            );
            
            // Grafik özelleştirmeleri
            chart.setBackgroundPaint(Color.WHITE);
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(new Color(240, 240, 240));
            plot.setRangeGridlinePaint(new Color(240, 240, 240));
            
            // Çizgi ve nokta stilleri
            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, new Color(0, 122, 255)); // Gerçekleşen satışlar
            renderer.setSeriesPaint(1, new Color(46, 213, 115)); // Tahmin
            renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
                                                      1.0f, new float[]{6.0f, 6.0f}, 0.0f));
            
            // Değer etiketleri
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            renderer.setDefaultItemLabelsVisible(true);
            plot.setRenderer(renderer);
            
            // Eksen özellikleri
            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setTickLabelFont(SF_PRO_DISPLAY);
            domainAxis.setLabelFont(SF_PRO_DISPLAY);
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(SF_PRO_DISPLAY);
            rangeAxis.setLabelFont(SF_PRO_DISPLAY);
            
            // Grafik panelini güncelle
            chartPanel.removeAll();
            
            // Tahmin özeti paneli
            JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
            summaryPanel.setOpaque(false);
            
            JLabel forecastLabel = new JLabel(String.format(
                "<html><b>%d Yılı Satış Tahmini:</b> %.0f adet</html>", 
                nextYear, forecast));
            forecastLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            forecastLabel.setForeground(TEXT_COLOR);
            
            JLabel methodLabel = new JLabel(String.format(
                "<html><b>Kullanılan Yöntem:</b> %d Yıllık Hareketli Ortalama</html>", 
                period));
            methodLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
            methodLabel.setForeground(TEXT_COLOR);
            
            summaryPanel.add(forecastLabel);
            summaryPanel.add(methodLabel);
            
            chartPanel.setLayout(new BorderLayout());
            chartPanel.add(summaryPanel, BorderLayout.NORTH);
            chartPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
            
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Tahmin hesaplanırken bir hata oluştu!");
        }
    }

    private double calculateMovingAverage(List<Integer> sales) {
        return sales.stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
    }

    private void showNoDataMessage(JPanel chartPanel) {
        chartPanel.removeAll();
        
        JPanel messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setOpaque(false);
        
        JLabel messageLabel = new JLabel("<html><center>" +
            "Henüz satış verisi bulunmuyor.<br>" +
            "Tahmin yapabilmek için en az 2 yıllık satış verisi gereklidir.</center></html>");
        messageLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(108, 117, 125));
        
        messagePanel.add(messageLabel);
        chartPanel.add(messagePanel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
} 