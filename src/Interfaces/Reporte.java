/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import Logica.Excel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 *
 * @author leand
 */
public final class Reporte extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reporte.class.getName());

    Logica.Consultas consulta = new Logica.Consultas();
    DefaultTableModel modelo;
    
    
    public Reporte() {
        initComponents();
        cargarGraficosEnPaneles();
        listarMatriculaVigente();
        listarMatriculaPendiente();
        listarAlumnosPorCurso();
        listarRetirados(); 
        mostrarGraficoAlumnosPorCurso();
    }

    void listarMatriculaPendiente() {
        modelo = (DefaultTableModel) tblMaticulaPendiente.getModel();
        modelo.setRowCount(0);
        
        try {
            ResultSet rs = consulta.listarMatriculaPendiente();
            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idAlumno");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("apellidos");
                fila[3]=rs.getString("dni");
                fila[4] = rs.getInt("edad");
                fila[5] = rs.getInt("celular");
                fila[6] = rs.getInt("estado") == 0 ? "Registrado" : "Error";
                modelo.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar alumnos: " + e.getMessage());
        }
    }
    
    

    void listarMatriculaVigente() {
        modelo = (DefaultTableModel) tblMaticulaVigente.getModel();
        modelo.setRowCount(0);
        
        try {
            ResultSet rs = consulta.listarMatriculaVigente();
            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idAlumno");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("apellidos");
                fila[3]=rs.getString("dni");
                fila[4] = rs.getInt("edad");
                fila[5] = rs.getInt("celular");
                fila[6] = rs.getInt("estado") == 1 ? "Matriculado" : "Error";
                modelo.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar alumnos: " + e.getMessage());
        }
    }

    void listarRetirados() {
        modelo = (DefaultTableModel) tblRetirados.getModel();
        modelo.setRowCount(0);
        
        try {
            ResultSet rs = consulta.listarRetirados();
            while (rs.next()) {
                Object[] fila = new Object[7];
                fila[0] = rs.getInt("idAlumno");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("apellidos");
                fila[3]=rs.getString("dni");
                fila[4] = rs.getInt("edad");
                fila[5] = rs.getInt("celular");
                fila[6] = rs.getInt("estado") == 2 ? "Retirado" : "Error";
                modelo.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar alumnos: " + e.getMessage());
        }
    }
    
    
    void listarAlumnosPorCurso() {
        modelo = (DefaultTableModel) tblAlumnosPorCurso.getModel();
        modelo.setRowCount(0);
        
        try {
            ResultSet rs = consulta.listarAlumnosPorCurso();
            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getInt("idCurso");
                fila[1] = rs.getString("asignatura");
                fila[2] = rs.getInt("Cantidad_de_Alumnos");
                modelo.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar alumnos: " + e.getMessage());
        }
    }
    
// 1. Método para obtener el conjunto de datos (Se llama una sola vez)
private DefaultCategoryDataset obtenerDatosGraficoAlumnos() {
    String url = "jdbc:mysql://localhost:3306/EduTek";
    String usuario = "root";
    String clave = "root";

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    try (Connection con = DriverManager.getConnection(url, usuario, clave)) {
        String sql = """
            SELECT COUNT(CASE WHEN a.estado=1 THEN 1 END) AS matriculados,
                   COUNT(CASE WHEN a.estado=0 THEN 1 END) AS registrados,
                   COUNT(CASE WHEN a.estado=2 THEN 1 END) AS retirados
            FROM Alumno a
            """;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) { 
            int matriculados = rs.getInt("matriculados");
            int registrados = rs.getInt("registrados");
            int retirados = rs.getInt("retirados");

            dataset.addValue(registrados, "Alumnos", "Registrados");
            dataset.addValue(matriculados, "Alumnos", "Matriculados");
            dataset.addValue(retirados, "Alumnos", "Retirados");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar datos del gráfico: " + e.getMessage());
    }
    return dataset;
}

// 2. Método para crear y configurar el gráfico (Se llama una sola vez)
private JFreeChart crearConfigurarGrafico(DefaultCategoryDataset dataset) {
    JFreeChart chart = ChartFactory.createBarChart(
        "Estado de Alumnos",
        "Estado", // Eje X
        "Cantidad de Alumnos", // Eje Y
        dataset,
        PlotOrientation.VERTICAL,
        true, // Leyenda
        true,
        false
    );

    CategoryPlot plot = chart.getCategoryPlot();

    BarRenderer renderer = new BarRenderer() {
        private final Color[] colores = {
            new Color(79, 129, 189), // Azul 
            new Color(192, 80, 77), // Rojo 
            new Color(155, 187, 89) // Verde 
        };

        @Override
        public Paint getItemPaint(int row, int column) {
            return colores[column % colores.length];
        }
    };

    renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    renderer.setDefaultItemLabelsVisible(true);
    renderer.setDefaultItemLabelFont(new Font("Tahoma", Font.BOLD, 14));
    renderer.setDefaultPositiveItemLabelPosition(
        new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER)
    );

    plot.setRenderer(renderer);
    plot.setBackgroundPaint(Color.WHITE);
    plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    plot.setOutlineVisible(false);
    
    return chart;
}


// 3. Método para mostrar un JFreeChart en un JPanel dado (Se llama tres veces)
private void mostrarGraficoEnPanel(JPanel panelDestino, JFreeChart chart) {
    ChartPanel panel = new ChartPanel(chart);
    panel.setPreferredSize(new Dimension(1264, 583));

    panelDestino.removeAll();
    panelDestino.setLayout(new BorderLayout());
    panelDestino.add(panel, BorderLayout.CENTER);
    panelDestino.validate();
    panelDestino.repaint();
}


// 4. Método principal que orquesta el proceso (Llamar desde tu constructor/inicialización)
public void cargarGraficosEnPaneles() {
    
    // A. Obtener los datos (Solo una consulta a la BD)
    DefaultCategoryDataset dataset = obtenerDatosGraficoAlumnos();
    
    // B. Crear y configurar el gráfico (Solo una vez)
    JFreeChart chart = crearConfigurarGrafico(dataset);
    
    // C. Mostrar el mismo gráfico en cada JPanel (Reutilizando el objeto JFreeChart)
    mostrarGraficoEnPanel(jEstadoAlumno1, chart);
    mostrarGraficoEnPanel(jEstadoAlumno2, chart);
    mostrarGraficoEnPanel(jEstadoAlumno3, chart);
}
     

   private void mostrarGraficoAlumnosPorCurso() {
    String url = "jdbc:mysql://localhost:3306/EduTek";  // Cambia a tu base de datos
    String usuario = "root";
    String clave = "root";

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    try (Connection con = DriverManager.getConnection(url, usuario, clave)) {
        
        // CONSULTA SQL: Contar alumnos matriculados y no matriculados
        String sql = """
            SELECT c.asignatura, COUNT(A.idAlumno) as Cantidad_de_Alumnos
                                 FROM curso c
                                 inner join Matricula M ON C.idCurso = M.idCurso
                                 inner join Alumno A ON M.idAlumno = A.idAlumno
                                 group by C.idCurso, C.asignatura
            """;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            String curso = rs.getString("asignatura");
            int cantidad = rs.getInt("Cantidad_de_Alumnos");

            dataset.addValue(cantidad, "Alumnos", curso);
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Cantidad de Alumnos por Curso",
            "Curso",                    // Eje X
            "Número de Alumnos",       // Eje Y
            dataset,
            PlotOrientation.VERTICAL,
            true,                        // Leyenda
            true,
            false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        
        BarRenderer renderer = new BarRenderer() {

            private final Color[] colores = {
                    new Color(79, 129, 189),   // Azul
                    new Color(192, 80, 77),    // Rojo
                    new Color(155, 187, 89),   // Verde
                    new Color(128, 100, 162),  // Púrpura
                    new Color(75, 172, 198),   // Cian
                    new Color(247, 150, 70)    // Naranja
                };

                @Override
                public Paint getItemPaint(int row, int column) {
                    return colores[column % colores.length];
                }
            };

        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Tahoma", Font.BOLD, 14));
        renderer.setDefaultPositiveItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER)
        );

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineVisible(false);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(1264, 583));

        jCantidadDeAlumnosPorCurso.removeAll();
        jCantidadDeAlumnosPorCurso.setLayout(new BorderLayout());
        jCantidadDeAlumnosPorCurso.add(panel, BorderLayout.CENTER);
        jCantidadDeAlumnosPorCurso.validate();
        jCantidadDeAlumnosPorCurso.repaint(); 
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar gráfico: " + e.getMessage());
    }
}
   
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpanel = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMaticulaVigente = new javax.swing.JTable();
        btnMatriculaVigente = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jEstadoAlumno3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMaticulaPendiente = new javax.swing.JTable();
        btnMatriculaPendiente = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jEstadoAlumno2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblRetirados = new javax.swing.JTable();
        btnRetirados = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jEstadoAlumno1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlumnosPorCurso = new javax.swing.JTable();
        btnAlumnosPorCurso = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jCantidadDeAlumnosPorCurso = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 0, 102));

        jPanel2.setBackground(new java.awt.Color(0, 0, 102));

        jTabbedPane3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel10.setBackground(new java.awt.Color(0, 0, 102));

        tblMaticulaVigente.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblMaticulaVigente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombres", "Apellidos", "DNI", "Edad", "Celular", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMaticulaVigente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMaticulaVigenteMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblMaticulaVigente);
        if (tblMaticulaVigente.getColumnModel().getColumnCount() > 0) {
            tblMaticulaVigente.getColumnModel().getColumn(3).setResizable(false);
            tblMaticulaVigente.getColumnModel().getColumn(5).setResizable(false);
            tblMaticulaVigente.getColumnModel().getColumn(6).setResizable(false);
        }

        btnMatriculaVigente.setText("Exportar");
        btnMatriculaVigente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMatriculaVigenteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(571, Short.MAX_VALUE)
                .addComponent(btnMatriculaVigente, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(518, 518, 518))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMatriculaVigente))
        );

        jTabbedPane3.addTab("Tabla", jPanel10);

        jPanel9.setBackground(new java.awt.Color(0, 0, 102));

        javax.swing.GroupLayout jEstadoAlumno3Layout = new javax.swing.GroupLayout(jEstadoAlumno3);
        jEstadoAlumno3.setLayout(jEstadoAlumno3Layout);
        jEstadoAlumno3Layout.setHorizontalGroup(
            jEstadoAlumno3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1270, Short.MAX_VALUE)
        );
        jEstadoAlumno3Layout.setVerticalGroup(
            jEstadoAlumno3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 583, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jEstadoAlumno3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jEstadoAlumno3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Grafico", jPanel9);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane3)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane3)
                .addContainerGap())
        );

        jpanel.addTab(" Alumnos con matrícula vigente", jPanel2);

        jPanel1.setBackground(new java.awt.Color(0, 0, 102));

        jPanel12.setBackground(new java.awt.Color(0, 0, 102));

        tblMaticulaPendiente.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblMaticulaPendiente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombres", "Apellidos", "DNI", "Edad", "Celular", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMaticulaPendiente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMaticulaPendienteMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMaticulaPendiente);

        btnMatriculaPendiente.setText("Exportar");
        btnMatriculaPendiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMatriculaPendienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(569, Short.MAX_VALUE)
                .addComponent(btnMatriculaPendiente, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(524, 524, 524))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMatriculaPendiente))
        );

        jTabbedPane4.addTab("Tabla", jPanel12);

        jPanel11.setBackground(new java.awt.Color(0, 0, 102));

        javax.swing.GroupLayout jEstadoAlumno2Layout = new javax.swing.GroupLayout(jEstadoAlumno2);
        jEstadoAlumno2.setLayout(jEstadoAlumno2Layout);
        jEstadoAlumno2Layout.setHorizontalGroup(
            jEstadoAlumno2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1276, Short.MAX_VALUE)
        );
        jEstadoAlumno2Layout.setVerticalGroup(
            jEstadoAlumno2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 583, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jEstadoAlumno2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jEstadoAlumno2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Grafico", jPanel11);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane4)
                .addContainerGap())
        );

        jpanel.addTab("Alumnos con matrícula pendiente", jPanel1);

        jPanel4.setBackground(new java.awt.Color(0, 0, 102));

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(0, 0, 102));

        tblRetirados.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblRetirados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombres", "Apellidos", "DNI", "Edad", "Celular", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRetirados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRetiradosMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblRetirados);
        if (tblRetirados.getColumnModel().getColumnCount() > 0) {
            tblRetirados.getColumnModel().getColumn(3).setResizable(false);
            tblRetirados.getColumnModel().getColumn(5).setResizable(false);
            tblRetirados.getColumnModel().getColumn(6).setResizable(false);
        }

        btnRetirados.setText("Exportar");
        btnRetirados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetiradosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(567, Short.MAX_VALUE)
                .addComponent(btnRetirados, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(516, 516, 516))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRetirados)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Tabla", jPanel7);

        jPanel8.setBackground(new java.awt.Color(0, 0, 102));

        javax.swing.GroupLayout jEstadoAlumno1Layout = new javax.swing.GroupLayout(jEstadoAlumno1);
        jEstadoAlumno1.setLayout(jEstadoAlumno1Layout);
        jEstadoAlumno1Layout.setHorizontalGroup(
            jEstadoAlumno1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1264, Short.MAX_VALUE)
        );
        jEstadoAlumno1Layout.setVerticalGroup(
            jEstadoAlumno1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 583, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jEstadoAlumno1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jEstadoAlumno1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Grafico", jPanel8);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanel.addTab(" Alumnos Retirados", jPanel4);

        jPanel3.setBackground(new java.awt.Color(0, 0, 102));

        jPanel6.setBackground(new java.awt.Color(0, 0, 102));

        tblAlumnosPorCurso.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblAlumnosPorCurso.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IdCurso", "Curso", "Cantidad de Alumnos"
            }
        ));
        tblAlumnosPorCurso.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAlumnosPorCursoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAlumnosPorCurso);

        btnAlumnosPorCurso.setText("Exportar");
        btnAlumnosPorCurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlumnosPorCursoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(564, Short.MAX_VALUE)
                .addComponent(btnAlumnosPorCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(519, 519, 519))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAlumnosPorCurso)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tabla", jPanel6);

        jCantidadDeAlumnosPorCurso.setPreferredSize(new java.awt.Dimension(1264, 583));

        javax.swing.GroupLayout jCantidadDeAlumnosPorCursoLayout = new javax.swing.GroupLayout(jCantidadDeAlumnosPorCurso);
        jCantidadDeAlumnosPorCurso.setLayout(jCantidadDeAlumnosPorCursoLayout);
        jCantidadDeAlumnosPorCursoLayout.setHorizontalGroup(
            jCantidadDeAlumnosPorCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1264, Short.MAX_VALUE)
        );
        jCantidadDeAlumnosPorCursoLayout.setVerticalGroup(
            jCantidadDeAlumnosPorCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCantidadDeAlumnosPorCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jCantidadDeAlumnosPorCurso, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Grafico", jPanel5);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1264, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanel.addTab(" Alumnos matriculados por curso", jPanel3);

        jButton1.setBackground(new java.awt.Color(204, 204, 204));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("Exportar todo");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpanel)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(440, 440, 440))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 665, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tblAlumnosPorCursoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAlumnosPorCursoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblAlumnosPorCursoMouseClicked

    private void tblMaticulaVigenteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMaticulaVigenteMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblMaticulaVigenteMouseClicked

    private void tblMaticulaPendienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMaticulaPendienteMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblMaticulaPendienteMouseClicked

    private void btnMatriculaVigenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatriculaVigenteActionPerformed
        Excel obj;

        try {
            obj = new Excel();
            obj.Excel(tblMaticulaVigente);
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }//GEN-LAST:event_btnMatriculaVigenteActionPerformed

    private void btnRetiradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetiradosActionPerformed
        Excel obj;

        try {
            obj = new Excel();
            obj.Excel(tblRetirados);
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }//GEN-LAST:event_btnRetiradosActionPerformed

    private void btnAlumnosPorCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlumnosPorCursoActionPerformed
            Excel obj;

        try {
            obj = new Excel();
            obj.Excel(tblAlumnosPorCurso);
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }//GEN-LAST:event_btnAlumnosPorCursoActionPerformed

    private void btnMatriculaPendienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatriculaPendienteActionPerformed
        Excel obj;

        try {
            obj = new Excel();
            obj.Excel(tblMaticulaPendiente);
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }//GEN-LAST:event_btnMatriculaPendienteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    java.util.LinkedHashMap<String, JTable> tablasParaExportar = new java.util.LinkedHashMap<>();
    
    // Añade tus tablas con el nombre que deseas para cada hoja:
    tablasParaExportar.put("Matrículas Pendientes", tblMaticulaPendiente);
    tablasParaExportar.put("Matriculas Vigentes", tblMaticulaVigente);
    tablasParaExportar.put("Retirados", tblRetirados);
    tablasParaExportar.put("Alumnos por Curso", tblAlumnosPorCurso);

    Excel obj = new Excel();

    try {
        obj.ExcelTotal(tablasParaExportar);
    } catch (IOException ex) {
        System.out.println("Error al exportar a Excel: " + ex);
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblRetiradosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRetiradosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblRetiradosMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Reporte().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlumnosPorCurso;
    private javax.swing.JButton btnMatriculaPendiente;
    private javax.swing.JButton btnMatriculaVigente;
    private javax.swing.JButton btnRetirados;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jCantidadDeAlumnosPorCurso;
    private javax.swing.JPanel jEstadoAlumno1;
    private javax.swing.JPanel jEstadoAlumno2;
    private javax.swing.JPanel jEstadoAlumno3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jpanel;
    private javax.swing.JTable tblAlumnosPorCurso;
    private javax.swing.JTable tblMaticulaPendiente;
    private javax.swing.JTable tblMaticulaVigente;
    private javax.swing.JTable tblRetirados;
    // End of variables declaration//GEN-END:variables
}
