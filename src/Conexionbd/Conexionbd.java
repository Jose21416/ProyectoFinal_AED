package Conexionbd;
import java.sql.*;
public class Conexionbd {
    Connection cn;
    PreparedStatement ps;
    ResultSet rs;
    
    public Conexionbd() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/EduTek", "root", "root");
            //el bd:v
            System.out.println("Conectado BD");
        } catch (Exception e) {
            System.out.println("Error al conectar BD");
        }
    }

    public Connection getConnection() {
        return cn;
    }
}
