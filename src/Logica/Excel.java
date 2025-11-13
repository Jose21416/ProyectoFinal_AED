/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author leand
 */
public class Excel {
    public void Excel(JTable t) throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de excel", "xls");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Guardar archivo");
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().toString().concat(".xls");
            try {
                File archivoXLS = new File(ruta);
                if (archivoXLS.exists()) {
                    archivoXLS.delete();
                }
                archivoXLS.createNewFile();
                Workbook libro = new HSSFWorkbook();
                FileOutputStream archivo = new FileOutputStream(archivoXLS);
                Sheet hoja = libro.createSheet("Mi hoja de trabajo 1");
                hoja.setDisplayGridlines(false);
                for (int f = 0; f < t.getRowCount(); f++) {
                    org.apache.poi.ss.usermodel.Row fila = hoja.createRow(f);
                    for (int c = 0; c < t.getColumnCount(); c++) {
                        Cell celda = fila.createCell(c);
                        if (f == 0) {
                            celda.setCellValue(t.getColumnName(c));
                        }
                    }
                }
                int filaInicio = 1;
                for (int f = 0; f < t.getRowCount(); f++) {
                    org.apache.poi.ss.usermodel.Row fila = hoja.createRow(filaInicio);
                    filaInicio++;
                    for (int c = 0; c < t.getColumnCount(); c++) {
                        Cell celda = fila.createCell(c);
                        if (t.getValueAt(f, c) instanceof Double) {
                            celda.setCellValue(Double.parseDouble(t.getValueAt(f, c).toString()));
                        } else if (t.getValueAt(f, c) instanceof Float) {
                            celda.setCellValue(Float.parseFloat((String) t.getValueAt(f, c)));
                        } else {
                            celda.setCellValue(String.valueOf(t.getValueAt(f, c)));
                        }
                    }
                }
                libro.write(archivo);
                archivo.close();
                Desktop.getDesktop().open(archivoXLS);
            } catch (IOException | NumberFormatException e) {
                throw e;
            }
        }
    }
    
        public void ExcelTotal (LinkedHashMap<String, JTable> tablas) throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de excel", "xls");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Guardar archivo");
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            // Aseguramos la extensión .xls
            String ruta = chooser.getSelectedFile().toString();
            if (!ruta.toLowerCase().endsWith(".xls")) {
                ruta = ruta.concat(".xls");
            }

            File archivoXLS = new File(ruta);
            Workbook libro = new HSSFWorkbook();
            FileOutputStream archivo = null;

            try {
                // Prepara el archivo, eliminando si ya existe
                if (archivoXLS.exists()) {
                    archivoXLS.delete();
                }
                archivoXLS.createNewFile();
                archivo = new FileOutputStream(archivoXLS);

                // --- Bucle principal para crear cada hoja ---
                for (String nombreHoja : tablas.keySet()) {
                    JTable t = tablas.get(nombreHoja);
                    
                    // 1. Crear una nueva hoja para cada JTable
                    Sheet hoja = libro.createSheet(nombreHoja);
                    hoja.setDisplayGridlines(false);
                    
                    // 2. Escribir encabezados
                    org.apache.poi.ss.usermodel.Row filaEncabezados = hoja.createRow(0);
                    for (int c = 0; c < t.getColumnCount(); c++) {
                        Cell celda = filaEncabezados.createCell(c);
                        celda.setCellValue(t.getColumnName(c));
                    }

                    // 3. Escribir datos
                    int filaInicio = 1; // Empezar en la fila 1 (después de los encabezados)
                    for (int f = 0; f < t.getRowCount(); f++) {
                        org.apache.poi.ss.usermodel.Row fila = hoja.createRow(filaInicio);
                        filaInicio++;
                        
                        for (int c = 0; c < t.getColumnCount(); c++) {
                            Cell celda = fila.createCell(c);
                            Object valor = t.getValueAt(f, c);
                            
                            // Lógica mejorada para manejar tipos de datos
                            if (valor != null) {
                                if (valor instanceof Number) {
                                    // Para Double, Integer, etc.
                                    celda.setCellValue(((Number) valor).doubleValue());
                                } else {
                                    // Para String y otros tipos
                                    celda.setCellValue(String.valueOf(valor));
                                }
                            }
                        }
                    }
                } // --- Fin del bucle de tablas ---

                // 4. Guardar y cerrar
                libro.write(archivo);
                archivo.close();
                
                // 5. Abrir el archivo
                Desktop.getDesktop().open(archivoXLS);
                
            } catch (IOException | NumberFormatException e) {
                // Cerrar el FileOutputStream en caso de error para liberar recursos
                if (archivo != null) {
                    try { archivo.close(); } catch (IOException ignored) {}
                }
                throw e; // Relanza la excepción para que sea manejada por el llamador
            }
        }
    }
}


