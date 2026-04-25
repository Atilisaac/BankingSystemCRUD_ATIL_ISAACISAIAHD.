/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banksystem;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
/**
 *
 * @author Administrator
 */
public class TransactionManager {
    // METHOD: Deposit Money
    public void deposit(String accId, String type, String amountStr) {
        if (amountStr.isEmpty() || accId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields!");
            return;
        }

        try (Connection conn = DBConnecton.getConnection()) { // Using your class name
            double amount = Double.parseDouble(amountStr);
            
            // Update Balance
            String updateSQL = "UPDATE Account SET balance = balance + ? WHERE account_id = ? AND account_type = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateSQL);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accId);
            pstmt.setString(3, type);

            if (pstmt.executeUpdate() > 0) {
                logTransaction(accId, "Deposit", amount);
                JOptionPane.showMessageDialog(null, "Deposit Successful!");
            } else {
                JOptionPane.showMessageDialog(null, "Account ID not found for this type.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // METHOD: Withdraw Money
    public void withdraw(String accId, String type, String amountStr) {
        if (amountStr.isEmpty() || accId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields!");
            return;
        }

        try (Connection conn = DBConnecton.getConnection()) {
            double amount = Double.parseDouble(amountStr);

            // 1. Check Balance
            String checkSQL = "SELECT balance FROM Account WHERE account_id = ? AND account_type = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setString(1, accId);
            checkStmt.setString(2, type);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (amount > currentBalance) {
                    JOptionPane.showMessageDialog(null, "Insufficient Funds!");
                } else {
                    // 2. Subtract Money
                    String updateSQL = "UPDATE Account SET balance = balance - ? WHERE account_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                    updateStmt.setDouble(1, amount);
                    updateStmt.setString(2, accId);
                    updateStmt.executeUpdate();

                    logTransaction(accId, "Withdraw", amount);
                    JOptionPane.showMessageDialog(null, "Withdrawal Successful!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Account not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // PRIVATE HELPER: Logs to the transaction table
    private void logTransaction(String accId, String transType, double amount) {
        try (Connection conn = DBConnecton.getConnection()) {
            String sql = "INSERT INTO Transactions (account_id, transaction_type, amount) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accId);
            pstmt.setString(2, transType);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Log Error: " + e.getMessage());
        }
    }
}

