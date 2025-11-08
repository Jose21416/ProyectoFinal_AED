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
        WHERE a.estado = 1
        ORDER BY m.idMatricula ASC
    """;
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al listar matrículas: " + e.getMessage());
            return null;
        }
    }

    // ==================== MÉTODOS PARA RETIRO ====================
    //  Registrar Retiro
    public boolean insertarRetiro(int idMatricula) {
        String verificar = "SELECT * FROM Retiro WHERE idMatricula=?";
        String insertar = "INSERT INTO Retiro(fecha, hora, idMatricula) VALUES (?, ?, ?)";
        String actualizarEstado = """
        UPDATE Alumno 
        SET estado = 2 
        WHERE idAlumno = (SELECT idAlumno FROM Matricula WHERE idMatricula = ?)
    """;

        try {
            Connection cn = conexion.getConnection();

            // Verificar si ya existe retiro de esa matrícula
            try (PreparedStatement psVerif = cn.prepareStatement(verificar)) {
                psVerif.setInt(1, idMatricula);
                try (ResultSet rs = psVerif.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("La matrícula ya tiene un retiro registrado.");
                        return false;
                    }
                }
            }

            // Insertar nuevo retiro
            try (PreparedStatement ps = cn.prepareStatement(insertar)) {
                ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
                ps.setTime(2, java.sql.Time.valueOf(java.time.LocalTime.now()));
                ps.setInt(3, idMatricula);
                ps.executeUpdate();
            }

            // Cambiar el estado del alumno a retirado (2)
            try (PreparedStatement ps2 = cn.prepareStatement(actualizarEstado)) {
                ps2.setInt(1, idMatricula);
                ps2.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar retiro: " + e.getMessage());
            return false;
        }
    }

//  Listar Retiros
    public ResultSet listarRetiros() {
        String sql = """
        SELECT 
            r.idRetiro,
            a.nombre AS alumno_nombre,
            a.apellidos AS alumno_apellidos,
            c.asignatura AS curso_asignatura,
            r.fecha,
            r.hora
        FROM Retiro r
        JOIN Matricula m ON r.idMatricula = m.idMatricula
        JOIN Alumno a ON m.idAlumno = a.idAlumno
        JOIN Curso c ON m.idCurso = c.idCurso
        WHERE a.estado = 2
        ORDER BY r.idRetiro ASC
    """;
        try {
            PreparedStatement ps = conexion.getConnection().prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error al listar retiros: " + e.getMessage());
            return null;
        }
    }

//  Eliminar Retiro (previa confirmación y estado == 2)
    public boolean eliminarRetiro(int idRetiro) {
        String consulta = """
        SELECT a.estado, a.idAlumno 
        FROM Alumno a
        JOIN Matricula m ON a.idAlumno = m.idAlumno
        JOIN Retiro r ON r.idMatricula = m.idMatricula
        WHERE r.idRetiro = ?
    """;
        String eliminar = "DELETE FROM Retiro WHERE idRetiro = ?";
        String reactivarAlumno = "UPDATE Alumno SET estado = 1 WHERE idAlumno = ?";

        try {
            Connection cn = conexion.getConnection();

            PreparedStatement ps1 = cn.prepareStatement(consulta);
            ps1.setInt(1, idRetiro);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int estado = rs.getInt("estado");
                int idAlumno = rs.getInt("idAlumno");

                if (estado != 2) {
                    System.out.println("No se puede eliminar. El alumno no está retirado.");
                    ps1.close();
                    return false;
                }

                PreparedStatement ps2 = cn.prepareStatement(eliminar);
                ps2.setInt(1, idRetiro);
                int filas = ps2.executeUpdate();

                // Reactivar alumno (estado = 1)
                PreparedStatement ps3 = cn.prepareStatement(reactivarAlumno);
                ps3.setInt(1, idAlumno);
                ps3.executeUpdate();

                ps1.close();
                ps2.close();
                ps3.close();
                return filas > 0;
            } else {
                System.out.println("No se encontró el retiro.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar retiro: " + e.getMessage());
            return false;
        }
    }

    // MODIFICAR RETIRO
    public boolean actualizarRetiro(int idRetiro, int idMatriculaNueva) {
        String sqlEstadoAlumnoNueva = """
        SELECT a.estado
        FROM Alumno a
        JOIN Matricula m ON a.idAlumno = m.idAlumno
        WHERE m.idMatricula = ?
    """;

        String sqlExisteOtroRetiro = """
        SELECT 1
        FROM Retiro
        WHERE idMatricula = ? AND idRetiro <> ?
        LIMIT 1
    """;

        String sqlUpdate = "UPDATE Retiro SET idMatricula = ?, fecha = ?, hora = ? WHERE idRetiro = ?";

        try {
            Connection cn = conexion.getConnection();

            // 1) Verificar que la matrícula nueva pertenece a un alumno retirado (estado=2)
            try (PreparedStatement ps1 = cn.prepareStatement(sqlEstadoAlumnoNueva)) {
                ps1.setInt(1, idMatriculaNueva);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No existe la matrícula destino.");
                        return false;
                    }
                    int estado = rs.getInt("estado");
                    if (estado != 2) {
                        System.out.println("Solo puede asociar el retiro a una matrícula cuyo alumno esté en estado 2 (retirado).");
                        return false;
                    }
                }
            }

            // 2) Evitar duplicar retiro para la misma matrícula
            try (PreparedStatement ps2 = cn.prepareStatement(sqlExisteOtroRetiro)) {
                ps2.setInt(1, idMatriculaNueva);
                ps2.setInt(2, idRetiro);
                try (ResultSet rs = ps2.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Ya existe un retiro para esa matrícula.");
                        return false;
                    }
                }
            }

            // 3) Actualizar idMatricula y la fecha/hora del sistema
            try (PreparedStatement ps3 = cn.prepareStatement(sqlUpdate)) {
                ps3.setInt(1, idMatriculaNueva);
                ps3.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                ps3.setTime(3, java.sql.Time.valueOf(java.time.LocalTime.now()));
                ps3.setInt(4, idRetiro);
                int filas = ps3.executeUpdate();
                return filas > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar retiro: " + e.getMessage());
            return false;
        }
    }

    // ==================== GETTER DE CONEXIÓN ====================
    public Connection getConnection() {
        return conexion.getConnection();
    }

}
