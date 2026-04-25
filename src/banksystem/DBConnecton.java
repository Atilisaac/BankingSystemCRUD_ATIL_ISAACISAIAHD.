/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banksystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author Administrator
 */
public class DBConnecton {
    // Replace 'bankingsystem' with your actual database name
    // Replace 'root' and 'password' with your MySQL credentials
    private static final String URL = "jdbc:mysql://localhost:3306/bankingsystem";
    private static final String USER = "root";
    private static final String PASS = "@Choyisaac12"; // Put your MySQL password here

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MySQL Driver (Optional in newer versions, but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish Connection
            conn = DriverManager.getConnection(URL, USER, PASS);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Connection Failed: " + e.getMessage());
        }
        return conn;
    }
}
