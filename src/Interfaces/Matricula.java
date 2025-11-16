/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaces;

import Logica.Consultas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Matricula extends javax.swing.JFrame {

    Consultas consultas = new Consultas();
    DefaultTableModel modelo = new DefaultTableModel();

    public Matricula() {
        initComponents();
        setLocationRelativeTo(null);

        txtFecha.setEnabled(false);
        txtHora.setEnabled(false);

        // Cargar combos
        cargarAlumnos();
        cargarCursos();

        // Mostrar las matrículas
        mostrarMatriculas();

    }

    public void limpiar() {

        cmbAlumno.setSelectedIndex(0);
        cmbCurso.setSelectedIndex(0);
        txtFecha.setText("");
        txtHora.setText("");
        tblMatrícula.clearSelection();

    }

    private void actualizarFechaHora() {
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText(LocalTime.now().withNano(0).toString());
    }

    private void cargarAlumnos() {
        try {
            ResultSet rs = consultas.listarAlumnos();
            cmbAlumno.removeAllItems();
            cmbAlumno.addItem("Elija un alumno");
            while (rs.next()) {
                cmbAlumno.addItem(rs.getInt("codAlumno") + " - " + rs.getString("nombre") + " " + rs.getString("apellidos"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar alumnos: " + e.getMessage());
        }
    }

    private void cargarCursos() {
        try {
            ResultSet rs = consultas.listarCursos();
            cmbCurso.removeAllItems();
            cmbCurso.addItem("Elija un curso");
            while (rs.next()) {
                cmbCurso.addItem(rs.getInt("codCurso") + " - " + rs.getString("asignatura"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cursos: " + e.getMessage());
        }
    }

    private void mostrarMatriculas() {
        try {
            ResultSet rs = consultas.listarMatriculas();
            modelo = new DefaultTableModel(new Object[]{"Código Matrícula", "Alumno", "Curso", "Fecha", "Hora"}, 0);
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("codMatricula"),
                    rs.getString("alumno_nombre") + " " + rs.getString("alumno_apellidos"),
                    rs.getString("curso_asignatura"),
                    rs.getDate("fecha"),
                    rs.getTime("hora")
                });
            }
            tblMatrícula.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar matrículas: " + e.getMessage());
        }
    }

    private void registrarMatricula() {
        try {
            if (cmbAlumno.getSelectedIndex() == 0 || cmbCurso.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un alumno y un curso.");
                return;
            }

            String codAlumnoStr = cmbAlumno.getSelectedItem().toString().split(" - ")[0];
            String codCursoStr = cmbCurso.getSelectedItem().toString().split(" - ")[0];

            int codAlumno = Integer.parseInt(codAlumnoStr);
            int codCurso = Integer.parseInt(codCursoStr);

            PreparedStatement ps1 = consultas.getConnection().prepareStatement(
                    "SELECT idAlumno FROM Alumno WHERE codAlumno=?"
            );
            ps1.setInt(1, codAlumno);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            int idAlumno = rs1.getInt("idAlumno");

            PreparedStatement ps2 = consultas.getConnection().prepareStatement(
                    "SELECT idCurso FROM Curso WHERE codCurso=?"
            );
            ps2.setInt(1, codCurso);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            int idCurso = rs2.getInt("idCurso");

            boolean ok = consultas.insertarMatricula(idAlumno, idCurso);

            if (ok) {
                txtFecha.setText(LocalDate.now().toString());
                txtHora.setText(LocalTime.now().withNano(0).toString());
                JOptionPane.showMessageDialog(this, "Matrícula registrada correctamente.");
                mostrarMatriculas();
                actualizarFechaHora();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la matrícula.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + e.getMessage());
        }
    }

    private void modificarMatricula() {
        try {
            int fila = tblMatrícula.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una matrícula de la tabla.");
                return;
            }

            int codMatricula = Integer.parseInt(tblMatrícula.getValueAt(fila, 0).toString());
            int idMatricula = consultas.obtenerIdMatriculaPorCod(codMatricula);

            String codCursoStr = cmbCurso.getSelectedItem().toString().split(" - ")[0];
            int codCurso = Integer.parseInt(codCursoStr);

            PreparedStatement ps = consultas.getConnection().prepareStatement(
                    "SELECT idCurso FROM Curso WHERE codCurso=?"
            );
            ps.setInt(1, codCurso);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int idCursoNuevo = rs.getInt("idCurso");

            boolean ok = consultas.actualizarMatricula(idMatricula, idCursoNuevo);

            if (ok) {
                txtFecha.setText(LocalDate.now().toString());
                txtHora.setText(LocalTime.now().withNano(0).toString());

                JOptionPane.showMessageDialog(this, "Matrícula modificada correctamente.");
                mostrarMatriculas();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar la matrícula.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        }
    }

    private void eliminarMatricula() {
        try {
            int fila = tblMatrícula.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una matrícula de la tabla.");
                return;
            }

            int codMatricula = Integer.parseInt(tblMatrícula.getValueAt(fila, 0).toString());

            PreparedStatement ps = consultas.getConnection().prepareStatement(
                    "SELECT idMatricula FROM Matricula WHERE codMatricula=?"
            );
            ps.setInt(1, codMatricula);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int idMatricula = rs.getInt("idMatricula");

            // Confirmación antes de eliminar
            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar la matrícula seleccionada?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Operación cancelada.");
                return;
            }

            // Ejecutar eliminación
            boolean ok = consultas.eliminarMatricula(idMatricula);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Matrícula eliminada correctamente.");
                mostrarMatriculas();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la matrícula.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    private void buscarMatriculas(String texto) {
        try {
            String sql = """
            SELECT 
                m.codMatricula,
                a.nombre AS alumno_nombre,
                a.apellidos AS alumno_apellidos,
                c.asignatura AS curso_asignatura,
                m.fecha,
                m.hora
            FROM Matricula m
            JOIN Alumno a ON m.idAlumno = a.idAlumno
            JOIN Curso c ON m.idCurso = c.idCurso
            WHERE 
                (a.nombre LIKE ? 
                 OR a.apellidos LIKE ?
                 OR CONCAT(a.nombre, ' ', a.apellidos) LIKE ?
                 OR m.codMatricula LIKE ?)
                AND a.estado = 1
            ORDER BY m.codMatricula ASC
        """;

            PreparedStatement ps = consultas.getConnection().prepareStatement(sql);

            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);  // nombre
            ps.setString(2, filtro);  // apellidos
            ps.setString(3, filtro);  // nombre + apellido
            ps.setString(4, filtro);  // código matrícula

            ResultSet rs = ps.executeQuery();

            modelo = new DefaultTableModel(
                    new Object[]{"Código Matrícula", "Alumno", "Curso", "Fecha", "Hora"},
                    0
            );

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("codMatricula"),
                    rs.getString("alumno_nombre") + " " + rs.getString("alumno_apellidos"),
                    rs.getString("curso_asignatura"),
                    rs.getDate("fecha"),
                    rs.getTime("hora")
                });
            }

            tblMatrícula.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }

    private void seleccionarMatricula() {
        int fila = tblMatrícula.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una matrícula de la tabla.");
            return;
        }

        String alumnoTabla = tblMatrícula.getValueAt(fila, 1).toString().trim();
        String cursoTabla = tblMatrícula.getValueAt(fila, 2).toString().trim();
        String fecha = tblMatrícula.getValueAt(fila, 3).toString();
        String hora = tblMatrícula.getValueAt(fila, 4).toString();

        cmbAlumno.setEnabled(false);
        txtFecha.setText(fecha);
        txtHora.setText(hora);

        for (int i = 1; i < cmbAlumno.getItemCount(); i++) {
            String item = cmbAlumno.getItemAt(i);

            String nombreItem = item.substring(item.indexOf("-") + 2).trim();

            if (nombreItem.equalsIgnoreCase(alumnoTabla)) {
                cmbAlumno.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 1; i < cmbCurso.getItemCount(); i++) {
            String item = cmbCurso.getItemAt(i);

            String asignaturaItem = item.substring(item.indexOf("-") + 2).trim();

            if (asignaturaItem.equalsIgnoreCase(cursoTabla)) {
                cmbCurso.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtHora = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        txtFecha = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbCurso = new javax.swing.JComboBox<>();
        cmbAlumno = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMatrícula = new javax.swing.JTable();
        txtBusqueda = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 0, 102));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 28)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("MATRÍCULA");

        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Fecha:");

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Hora:");

        btnRegistrar.setBackground(new java.awt.Color(0, 51, 153));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("REGISTRAR");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnModificar.setBackground(new java.awt.Color(0, 51, 153));
        btnModificar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(255, 255, 255));
        btnModificar.setText("MODIFICAR");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
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

        jLabel5.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Alumno:");

        jLabel7.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Curso:");

        cmbCurso.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbAlumno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAlumnoActionPerformed(evt);
            }
        });

        tblMatrícula.setModel(new javax.swing.table.DefaultTableModel(
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
        tblMatrícula.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMatrículaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblMatrícula);

        txtBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBusquedaActionPerformed(evt);
            }
        });
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBusquedaKeyReleased(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Busqueda de matrículas por código o nombre de alumno:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addComponent(jLabel2)))
                        .addGap(48, 48, 48)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 692, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(184, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cmbAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cmbCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, 520));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        registrarMatricula();
        cargarAlumnos();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        modificarMatricula();
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarMatricula();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBusquedaActionPerformed

    }//GEN-LAST:event_txtBusquedaActionPerformed

    private void txtBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBusquedaKeyReleased
        buscarMatriculas(txtBusqueda.getText());
    }//GEN-LAST:event_txtBusquedaKeyReleased

    private void tblMatrículaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMatrículaMouseClicked
        seleccionarMatricula();
    }//GEN-LAST:event_tblMatrículaMouseClicked

    private void cmbAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAlumnoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAlumnoActionPerformed

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
            java.util.logging.Logger.getLogger(Matricula.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Matricula.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Matricula.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Matricula.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Matricula().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cmbAlumno;
    private javax.swing.JComboBox<String> cmbCurso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblMatrícula;
    private javax.swing.JTextField txtBusqueda;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtHora;
    // End of variables declaration//GEN-END:variables
}
