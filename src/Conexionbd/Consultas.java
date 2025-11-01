package Conexionbd;

import Datos.DMatricula;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

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

    // ==================== MÉTODOS PARA MATRÍCULA ====================
    public boolean insertarMatricula(int idAlumno, int idCurso) {
        String verificarUnica = "SELECT * FROM Matricula WHERE idAlumno=?";
        String verificarCurso = "SELECT * FROM Matricula WHERE idAlumno=? AND idCurso=?";
        String insertar = "INSERT INTO Matricula(fecha, hora, idAlumno, idCurso) VALUES (?, ?, ?, ?)";
        String actualizarEstado = "UPDATE Alumno SET estado=1 WHERE idAlumno=?";

        try {
            Connection cn = conexion.getConnection();

            // Verificar si ya tiene alguna matrícula activa¿
            PreparedStatement psCheck = cn.prepareStatement(verificarUnica);
            psCheck.setInt(1, idAlumno);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                System.out.println("El alumno ya tiene una matrícula activa.");
                psCheck.close();
                return false;
            }
            psCheck.close();

            // Verificar si ya está matriculado en el mismo curso
            PreparedStatement psVerif = cn.prepareStatement(verificarCurso);
            psVerif.setInt(1, idAlumno);
            psVerif.setInt(2, idCurso);
            ResultSet rs = psVerif.executeQuery();
            if (rs.next()) {
                System.out.println("El alumno ya está matriculado en este curso.");
                psVerif.close();
                return false;
            }

            //  Insertar matrícula nueva
            PreparedStatement ps = cn.prepareStatement(insertar);
            ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps.setTime(2, java.sql.Time.valueOf(java.time.LocalTime.now()));
            ps.setInt(3, idAlumno);
            ps.setInt(4, idCurso);
            int filas = ps.executeUpdate();

            // Actualizar estado del alumno a matriculado
            PreparedStatement ps2 = cn.prepareStatement(actualizarEstado);
            ps2.setInt(1, idAlumno);
            ps2.executeUpdate();

            ps.close();
            ps2.close();
            psVerif.close();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar matrícula: " + e.getMessage());
            return false;
        }
    }

    //  Actualizar matrícula (por ejemplo, cambiar el curso asignado)
    public boolean actualizarMatricula(int idMatricula, int idCursoNuevo) {
        String sql = "UPDATE Matricula SET idCurso=?, fecha=?, hora=? WHERE idMatricula=?";
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setInt(1, idCursoNuevo);
            ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now())); // Fecha actual
            ps.setTime(3, java.sql.Time.valueOf(java.time.LocalTime.now())); // Hora actual
            ps.setInt(4, idMatricula);

            int filas = ps.executeUpdate();
            ps.close();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar matrícula: " + e.getMessage());
            return false;
        }
    }

// Solo se puede eliminar si el alumno no está retirado (estado ≠ 2)
    public boolean eliminarMatricula(int idMatricula) {
        String consultarEstado = """
        SELECT a.estado, a.idAlumno 
        FROM Alumno a 
        JOIN Matricula m ON a.idAlumno = m.idAlumno 
        WHERE m.idMatricula = ?
    """;
        String eliminar = "DELETE FROM Matricula WHERE idMatricula=?";
        try {
            Connection cn = conexion.getConnection();

            PreparedStatement ps1 = cn.prepareStatement(consultarEstado);
            ps1.setInt(1, idMatricula);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int estado = rs.getInt("estado");
                int idAlumno = rs.getInt("idAlumno");

                if (estado == 2) {
                    System.out.println("No se puede eliminar. El alumno está retirado.");
                    ps1.close();
                    return false;
                }

                PreparedStatement ps2 = cn.prepareStatement(eliminar);
                ps2.setInt(1, idMatricula);
                int filas = ps2.executeUpdate();

                ps1.close();
                ps2.close();
                return filas > 0;
            } else {
                System.out.println("No se encontró la matrícula especificada.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar matrícula: " + e.getMessage());
            return false;
        }
    }

//  Listar todas las matrículas (con datos del alumno y curso)
    public ResultSet listarMatriculas() {
        String sql = """
        SELECT 
            m.idMatricula,
            a.nombre AS alumno_nombre,
            a.apellidos AS alumno_apellidos,
            c.asignatura AS curso_asignatura,
            m.fecha,
            m.hora
        FROM Matricula m
        JOIN Alumno a ON m.idAlumno = a.idAlumno
        JOIN Curso c ON m.idCurso = c.idCurso
        ORDER BY m.idMatricula DESC
    """;
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al listar matrículas: " + e.getMessage());
            return null;
        }
    }

//  Buscar matrícula por ID
    public ResultSet buscarMatriculaPorId(int idMatricula) {
        String sql = """
        SELECT 
            m.idMatricula,
            a.nombre, a.apellidos,
            c.asignatura,
            m.fecha, m.hora
        FROM Matricula m
        JOIN Alumno a ON m.idAlumno = a.idAlumno
        JOIN Curso c ON m.idCurso = c.idCurso
        WHERE m.idMatricula=?
    """;
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setInt(1, idMatricula);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar matrícula: " + e.getMessage());
            return null;
        }
    }

//  Buscar matrículas por Alumno
    public ResultSet buscarMatriculasPorAlumno(int idAlumno) {
        String sql = """
        SELECT 
            m.idMatricula,
            c.asignatura AS curso_asignatura,
            m.fecha, m.hora
        FROM Matricula m
        JOIN Curso c ON m.idCurso = c.idCurso
        WHERE m.idAlumno=?
        ORDER BY m.fecha DESC
    """;
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            ps.setInt(1, idAlumno);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al buscar matrículas por alumno: " + e.getMessage());
            return null;
        }
    }

    // ==================== GETTER DE CONEXIÓN ====================
    public Connection getConnection() {
        return conexion.getConnection();
    }

}
