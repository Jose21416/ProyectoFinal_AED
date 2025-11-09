/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
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
public class Reporte extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reporte.class.getName());

    Conexionbd.Consultas consulta = new Conexionbd.Consultas();
    DefaultTableModel modelo;
    
    
    public Reporte() {
        initComponents();
        mostrarGraficoAlumnos2();
        mostrarGraficoAlumnos1();
        listarMatriculaVigente();
        listarMatriculaPendiente();
        listarAlumnosPorCurso();
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
                fila[6] = rs.getInt("estado") == 1 ? "Activo" : "Inactivo";
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
                fila[6] = rs.getInt("estado") == 1 ? "Activo" : "Inactivo";
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
    
   private void mostrarGraficoAlumnos1() { 
    String url = "jdbc:mysql://localhost:3306/EduTek";  // Cambia a tu base de datos
    String usuario = "root";
    String clave = "root";

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    try (Connection con = DriverManager.getConnection(url, usuario, clave)) {
        
        String sql = """
            SELECT 
                COUNT(CASE WHEN m.idMatricula IS NOT NULL AND r.idMatricula IS NULL THEN 1 END) AS matriculados,
                COUNT(CASE WHEN m.idMatricula IS NULL THEN 1 END) AS no_matriculados
            FROM Alumno a
            LEFT JOIN Matricula m ON a.idAlumno = m.idAlumno
            LEFT JOIN Retiro r ON m.idMatricula = r.idMatricula
            """;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int matriculados = rs.getInt("matriculados");
            int noMatriculados = rs.getInt("no_matriculados");

            dataset.addValue(matriculados, "Alumnos", "Matriculados");
            dataset.addValue(noMatriculados, "Alumnos", "No Matriculados");
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Estado de Matrícula de Alumnos",
            "Estado",                    // Eje X
            "Cantidad de Alumnos",       // Eje Y
            dataset,
            PlotOrientation.VERTICAL,
            true,                        // Leyenda
            true,
            false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        
        BarRenderer renderer = new BarRenderer() {
            private final Color[] colores = {
                new Color(79, 129, 189),  // Azul
                new Color(192, 80, 77)     // Rojo
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
        panel.setPreferredSize(new Dimension(600, 400));

        jMatriculaPendiente.removeAll();
        jMatriculaPendiente.setLayout(new BorderLayout());
        jMatriculaPendiente.add(panel, BorderLayout.CENTER);
        jMatriculaPendiente.validate();
        jMatriculaPendiente.repaint();
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar gráfico: " + e.getMessage());
    }
}
    private void mostrarGraficoAlumnos2() {
    String url = "jdbc:mysql://localhost:3306/EduTek";  // Cambia a tu base de datos
    String usuario = "root";
    String clave = "root";

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    try (Connection con = DriverManager.getConnection(url, usuario, clave)) {
        
        String sql = """
            SELECT 
                COUNT(CASE WHEN m.idMatricula IS NOT NULL AND r.idMatricula IS NULL THEN 1 END) AS matriculados,
                COUNT(CASE WHEN m.idMatricula IS NULL THEN 1 END) AS no_matriculados
            FROM Alumno a
            LEFT JOIN Matricula m ON a.idAlumno = m.idAlumno
            LEFT JOIN Retiro r ON m.idMatricula = r.idMatricula
            """;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int matriculados = rs.getInt("matriculados");
            int noMatriculados = rs.getInt("no_matriculados");

            // Agregar al dataset: (valor, serie, categoría)
            dataset.addValue(matriculados, "Alumnos", "Matriculados");
            dataset.addValue(noMatriculados, "Alumnos", "No Matriculados");
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Estado de Matrícula de Alumnos",
            "Estado",                    // Eje X
            "Cantidad de Alumnos",       // Eje Y
            dataset,
            PlotOrientation.VERTICAL,
            true,                        // Leyenda
            true,
            false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        
        BarRenderer renderer = new BarRenderer() {
            private final Color[] colores = {
                new Color(79, 129, 189),  // Azul
                new Color(192, 80, 77)     // Rojo
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
        panel.setPreferredSize(new Dimension(600, 400));

        jMatriculaPendiente1.removeAll();
        jMatriculaPendiente1.setLayout(new BorderLayout());
        jMatriculaPendiente1.add(panel, BorderLayout.CENTER);
        jMatriculaPendiente1.validate();
        jMatriculaPendiente1.repaint();
        
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar gráfico: " + e.getMessage());
    }
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
        panel.setPreferredSize(new Dimension(600, 400));

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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMaticulaPendiente = new javax.swing.JTable();
        jMatriculaPendiente = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblMaticulaVigente = new javax.swing.JTable();
        jMatriculaPendiente1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlumnosPorCurso = new javax.swing.JTable();
        jCantidadDeAlumnosPorCurso = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(0, 0, 102));

        tblMaticulaPendiente.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblMaticulaPendiente.setModel(new javax.swing.table.DefaultTableModel(
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
        tblMaticulaPendiente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMaticulaPendienteMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMaticulaPendiente);
        if (tblMaticulaPendiente.getColumnModel().getColumnCount() > 0) {
            tblMaticulaPendiente.getColumnModel().getColumn(3).setResizable(false);
            tblMaticulaPendiente.getColumnModel().getColumn(5).setResizable(false);
            tblMaticulaPendiente.getColumnModel().getColumn(6).setResizable(false);
        }

        javax.swing.GroupLayout jMatriculaPendienteLayout = new javax.swing.GroupLayout(jMatriculaPendiente);
        jMatriculaPendiente.setLayout(jMatriculaPendienteLayout);
        jMatriculaPendienteLayout.setHorizontalGroup(
            jMatriculaPendienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jMatriculaPendienteLayout.setVerticalGroup(
            jMatriculaPendienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 268, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jMatriculaPendiente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jMatriculaPendiente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpanel.addTab("Alumnos con matrícula pendiente", jPanel1);

        jPanel2.setBackground(new java.awt.Color(0, 0, 102));

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

        javax.swing.GroupLayout jMatriculaPendiente1Layout = new javax.swing.GroupLayout(jMatriculaPendiente1);
        jMatriculaPendiente1.setLayout(jMatriculaPendiente1Layout);
        jMatriculaPendiente1Layout.setHorizontalGroup(
            jMatriculaPendiente1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jMatriculaPendiente1Layout.setVerticalGroup(
            jMatriculaPendiente1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 268, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jMatriculaPendiente1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMatriculaPendiente1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpanel.addTab(" Alumnos con matrícula vigente", jPanel2);

        jPanel3.setBackground(new java.awt.Color(0, 0, 102));

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

        javax.swing.GroupLayout jCantidadDeAlumnosPorCursoLayout = new javax.swing.GroupLayout(jCantidadDeAlumnosPorCurso);
        jCantidadDeAlumnosPorCurso.setLayout(jCantidadDeAlumnosPorCursoLayout);
        jCantidadDeAlumnosPorCursoLayout.setHorizontalGroup(
            jCantidadDeAlumnosPorCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jCantidadDeAlumnosPorCursoLayout.setVerticalGroup(
            jCantidadDeAlumnosPorCursoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 845, Short.MAX_VALUE)
                    .addComponent(jCantidadDeAlumnosPorCurso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jCantidadDeAlumnosPorCurso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpanel.addTab(" Alumnos matriculados por curso", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpanel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpanel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblMaticulaPendienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMaticulaPendienteMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblMaticulaPendienteMouseClicked

    private void tblAlumnosPorCursoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAlumnosPorCursoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblAlumnosPorCursoMouseClicked

    private void tblMaticulaVigenteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMaticulaVigenteMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblMaticulaVigenteMouseClicked

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
    private javax.swing.JPanel jCantidadDeAlumnosPorCurso;
    private javax.swing.JPanel jMatriculaPendiente;
    private javax.swing.JPanel jMatriculaPendiente1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jpanel;
    private javax.swing.JTable tblAlumnosPorCurso;
    private javax.swing.JTable tblMaticulaPendiente;
    private javax.swing.JTable tblMaticulaVigente;
    // End of variables declaration//GEN-END:variables
}
