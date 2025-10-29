package Conexionbd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Consultas {

    private Conexionbd conexion;

    public Consultas() {
        this.conexion = new Conexionbd();
    }

    //====================Para alumno====================

        public boolean insertarAlumno(String nombre, String apellidos, String dni, int edad, int celular, int estado) {
        String sql = "INSERT INTO Alumno(nombre, apellidos, dni, edad, celular, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            ps.setString(3, dni);
            ps.setInt(4, edad);
            ps.setInt(5, celular);
            ps.setInt(6, estado);
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar alumno: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarAlumno(int idAlumno, String nombre, String apellidos, String dni, int edad, int celular, int estado) {
        String sql = "UPDATE Alumno SET nombre=?, apellidos=?, dni=?, edad=?, celular=?, estado=? WHERE idAlumno=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            ps.setString(3, dni);
            ps.setInt(4, edad);
            ps.setInt(5, celular);
            ps.setInt(6, estado);
            ps.setInt(7, idAlumno);
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar alumno: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarAlumno(int idAlumno) {
        String sql = "DELETE FROM Alumno WHERE idAlumno=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setInt(1, idAlumno);
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar alumno: " + e.getMessage());
            return false;
        }
    }
    
    public ResultSet listarAlumnos() {
        String sql = "SELECT * FROM Alumno";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al listar alumnos: " + e.getMessage());
            return null;
        }
    }
    
    public ResultSet buscarAlumnoPorId(int idAlumno) {
        String sql = "SELECT * FROM Alumno WHERE idAlumno=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setInt(1, idAlumno);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar alumno: " + e.getMessage());
            return null;
        }
    }
    
    public ResultSet buscarAlumnoPorDni(String dni) {
        String sql = "SELECT * FROM Alumno WHERE dni=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setString(1, dni);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar alumno por DNI: " + e.getMessage());
            return null;
        }
    }
        // ==================== MÉTODOS PARA CURSO ====================
    
    public boolean insertarCurso(String asignatura, int ciclo, int creditos, int horas) {
        String sql = "INSERT INTO Curso(asignatura, ciclo, creditos, horas) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setString(1, asignatura);
            ps.setInt(2, ciclo);
            ps.setInt(3, creditos);
            ps.setInt(4, horas);
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar curso: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarCurso(int idCurso, String asignatura, int ciclo, int creditos, int horas) {
        String sql = "UPDATE Curso SET asignatura=?, ciclo=?, creditos=?, horas=? WHERE idCurso=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setString(1, asignatura);
            ps.setInt(2, ciclo);
            ps.setInt(3, creditos);
            ps.setInt(4, horas);
            ps.setInt(5, idCurso);
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar curso: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarCurso(int idCurso) {
        String sql = "DELETE FROM Curso WHERE idCurso=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setInt(1, idCurso);
            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar curso: " + e.getMessage());
            return false;
        }
    }
    
    public ResultSet listarCursos() {
        String sql = "SELECT * FROM Curso";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al listar cursos: " + e.getMessage());
            return null;
        }
    }
    
    public ResultSet buscarCursoPorId(int idCurso) {
        String sql = "SELECT * FROM Curso WHERE idCurso=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setInt(1, idCurso);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar curso: " + e.getMessage());
            return null;
        }
    }
}
