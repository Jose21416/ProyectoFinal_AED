/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import Conexionbd.Consultas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Retiro extends javax.swing.JFrame {

    Consultas consultas = new Consultas();
    DefaultTableModel modelo = new DefaultTableModel();

    public Retiro() {
        initComponents();
        setLocationRelativeTo(null);

        txtFecha.setEnabled(false);
        txtHora.setEnabled(false);

        cargarMatriculas();
        mostrarRetiros();
    }

    public void limpiar() {

        cmbMatricula.setSelectedIndex(0);
        txtFecha.setText("");
        txtHora.setText("");
        txtBusqueda.setText("");

    }

    private void actualizarFechaHora() {
        // Asigna la fecha actual del sistema en formato yyyy-MM-dd
        txtFecha.setText(LocalDate.now().toString());

        // Asigna la hora actual del sistema en formato hh:mm:ss
        txtHora.setText(LocalTime.now().withNano(0).toString());
    }

    private void cargarMatriculas() {
        try {
            ResultSet rs = consultas.listarMatriculas();
            cmbMatricula.removeAllItems();
            cmbMatricula.addItem("Seleccione una matrícula");
            while (rs.next()) {
                cmbMatricula.addItem(
                        rs.getInt("idMatricula") + " - "
                        + rs.getString("alumno_nombre") + " "
                        + rs.getString("alumno_apellidos") + " ("
                        + rs.getString("curso_asignatura") + ")"
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar matrículas: " + e.getMessage());
        }
    }

    private void mostrarRetiros() {
        try {
            ResultSet rs = consultas.listarRetiros();
            modelo = new DefaultTableModel(new Object[]{"ID retiro", "ID Matricula", "Alumno", "Curso","Fecha", "Hora"}, 0);
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("idRetiro"),
                    rs.getInt("idMatricula"),
                    rs.getString("alumno_nombre") + " " + rs.getString("alumno_apellidos"),
                    rs.getString("curso_asignatura"),
                    rs.getDate("fecha"),
                    rs.getTime("hora")
                });
            }
            tblRetiros.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar retiros: " + e.getMessage());
        }
    }

    private void registrarRetiro() {
        try {
            if (cmbMatricula.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una matrícula válida.");
                return;
            }

            int idMatricula = Integer.parseInt(cmbMatricula.getSelectedItem().toString().split(" - ")[0]);
            boolean ok = consultas.insertarRetiro(idMatricula);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Retiro registrado correctamente.");
                mostrarRetiros();
                actualizarFechaHora();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el retiro (ya existe o error).");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + e.getMessage());
        }
    }

    private void eliminarRetiro() {
        try {
            int fila = tblRetiros.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un retiro de la tabla.");
                return;
            }

            int idRetiro = Integer.parseInt(tblRetiros.getValueAt(fila, 0).toString());

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este retiro?\nEl alumno será reactivado.",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = consultas.eliminarRetiro(idRetiro);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Retiro eliminado correctamente.");
                    mostrarRetiros();
                    limpiar();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el retiro.");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    private void buscarRetiros(String texto) {
        try {
            String sql = """
                SELECT r.idRetiro, a.nombre AS alumno_nombre, a.apellidos AS alumno_apellidos,
                       c.asignatura AS curso_asignatura, r.fecha, r.hora
                FROM Retiro r
                JOIN Matricula m ON r.idMatricula = m.idMatricula
                JOIN Alumno a ON m.idAlumno = a.idAlumno
                JOIN Curso c ON m.idCurso = c.idCurso
                WHERE (a.nombre LIKE ? OR r.idRetiro LIKE ?)
                  AND a.estado = 2
            """;
            PreparedStatement ps = consultas.getConnection().prepareStatement(sql);
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();

            modelo = new DefaultTableModel(new Object[]{"ID", "Alumno", "Curso", "Fecha", "Hora"}, 0);
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("idRetiro"),
                    rs.getString("alumno_nombre") + " " + rs.getString("alumno_apellidos"),
                    rs.getString("curso_asignatura"),
                    rs.getDate("fecha"),
                    rs.getTime("hora")
                });
            }
            tblRetiros.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }

    private void seleccionarRetiro() {
        int fila = tblRetiros.getSelectedRow();
        if (fila >= 0) {

            //Tomamos los valores desde la tabla, así esté oculta se puede hace ya que solo es algo visual
            String idMatriculaTabla = tblRetiros.getValueAt(fila, 1).toString();
            String fecha = tblRetiros.getValueAt(fila, 4).toString();
            String hora = tblRetiros.getValueAt(fila, 5).toString();

            // Muestra los valores en los campos
            txtFecha.setText(fecha);
            txtHora.setText(hora);

            for (int i = 0; i < cmbMatricula.getItemCount(); i++) {
                String item = cmbMatricula.getItemAt(i);
                if (item.startsWith(idMatriculaTabla + " -")) {
                    cmbMatricula.setSelectedIndex(i);
                    break;
                }
            }

        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtHora = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        txtFecha = new javax.swing.JTextField();
        cmbMatricula = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        txtBusqueda = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRetiros = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 102));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 28)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("RETIRO");

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Fecha:");

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Hora:");

        jLabel8.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Matrícula:");

        btnRegistrar.setBackground(new java.awt.Color(0, 51, 153));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("REGISTRAR");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(0, 51, 153));
        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnLimpiar.setBackground(new java.awt.Color(0, 51, 153));
        btnLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("LIMPIAR");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        cmbMatricula.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Búsqueda de retiros por ID o nombre de alumno:");

        txtBusqueda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBusquedaMouseClicked(evt);
            }
        });
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBusquedaKeyReleased(evt);
            }
        });

        tblRetiros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblRetiros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRetirosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblRetiros);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4)
                    .addComponent(jSeparator2))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap(64, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 25, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtHora)
                                    .addComponent(txtFecha)
                                    .addComponent(cmbMatricula, 0, 150, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(0, 42, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(317, 317, 317))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 21, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cmbMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(52, 52, 52))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(28, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 750, 690));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        registrarRetiro();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarRetiro();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtBusquedaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBusquedaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBusquedaMouseClicked

    private void tblRetirosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRetirosMouseClicked
        seleccionarRetiro();
    }//GEN-LAST:event_tblRetirosMouseClicked

    private void txtBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBusquedaKeyReleased
        buscarRetiros(txtBusqueda.getText().trim());
    }//GEN-LAST:event_txtBusquedaKeyReleased

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Retiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Retiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Retiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Retiro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Retiro().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cmbMatricula;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTable tblRetiros;
    private javax.swing.JTextField txtBusqueda;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtHora;
    // End of variables declaration//GEN-END:variables
}
