/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexionbd;

import java.sql.*;

/**
 *
 * @author HOME
 */
public class Conexionbd {
    Connection cn;
    PreparedStatement ps;
    ResultSet rs;
    
    public Conexionbd() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/faltaponereldb:v", "root", "root");
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
